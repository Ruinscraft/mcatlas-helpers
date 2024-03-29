package net.mcatlas.helpers.geonames;

import com.zaxxer.hikari.HikariDataSource;
import net.mcatlas.helpers.Coordinate;
import net.mcatlas.helpers.HelpersPlugin;
import net.mcatlas.helpers.geonames.Destination.Builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MySQLStorage {

    private HikariDataSource dataSource;

    private String query_destination;
    private String query_area;
    private String query_random;

    public MySQLStorage(String host, int port,
                        String database, String geoTable, String username, String password) {
        query_destination = "SELECT * FROM " + geoTable + " WHERE asciiname LIKE ? " +
                " AND admin1 LIKE ? AND country LIKE ?;";
        query_area = "SELECT * FROM " + geoTable + " WHERE latitude < ? AND latitude > ? AND " +
                "longitude < ? AND longitude > ?;";
        query_random = "SELECT * FROM " + geoTable + " ORDER BY RAND() LIMIT 1;";

        dataSource = new HikariDataSource();

        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setPoolName("destination-pool");
        dataSource.setMaximumPoolSize(3);
        dataSource.setConnectionTimeout(3000);
        dataSource.setLeakDetectionThreshold(3000);
    }

    // run async!!
    // cycles a few times to check in a radius for nearby destinations
    public List<Destination> getNearbyDestinations(int x, int z, LocationAccuracy locationAccuracy) {
        List<Destination> destinations = HelpersPlugin.get().getStorage().getNearby(x, z, locationAccuracy.range);

        if (destinations.size() > 0) {
            final LocationAccuracy currentAccuracy = locationAccuracy;
            destinations.forEach(d -> d.setAccuracy(currentAccuracy));
            return destinations;
        }

        switch (locationAccuracy) {
            case LOW:
                return destinations;
            case MEDIUM:
                locationAccuracy = LocationAccuracy.LOW;
                break;
            case HIGH:
                locationAccuracy = LocationAccuracy.MEDIUM;
                break;
            case VERY_HIGH:
                locationAccuracy = LocationAccuracy.HIGH;
                break;
            default:
                break;
        }

        return getNearbyDestinations(x, z, locationAccuracy);
    }

    public CompletableFuture<List<Destination>> getNearbyFuture(int x, int z, int blockRange) {
        return CompletableFuture.supplyAsync(() -> this.getNearby(x, z, blockRange));
    }

    public List<Destination> getNearby(int x, int z, int blockRange) {
        Coordinate coord = Coordinate.getLifeFromMC(-z, -x);
        return getNearby(coord.getX(), coord.getY(), blockRange);
    }

    public List<Destination> getNearby(double lat, double lon, int blockRange) {
        List<Destination> destinations = new ArrayList<>();
        double range = blockRange / HelpersPlugin.get().getScaling();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(query_area)) {
            ps.setDouble(1, lat + range);
            ps.setDouble(2, lat - range);
            ps.setDouble(3, lon + range);
            ps.setDouble(4, lon - range);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("asciiname");
                    double locationLat = rs.getDouble("latitude");
                    double locationLon = rs.getDouble("longitude");

                    Destination.Builder builder = new Builder(name, locationLat, locationLon);
                    builder.alternateNames(
                            rs.getString("alternateNames")).fcode(rs.getString("fcode"))
                            .country(rs.getString("country")).adminZone(rs.getString("admin1"))
                            .population(rs.getLong("population")).timezone(rs.getString("timezone"));

                    Destination destination = builder.build();
                    destinations.add(destination);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.sort(destinations);
        return destinations;
    }

    public CompletableFuture<List<Destination>> getDestinationsFromLocationNameFuture(String string) {
        return CompletableFuture.supplyAsync(() -> {
            return getDestinationsFromLocationName(string);
        });
    }

    public List<Destination> getDestinationsFromLocationName(String string) {
        List<Destination> destinations = new ArrayList<>();

        String asciiname = "";
        String admin1 = "";
        String country = "";

        String[] locations = string.split(", ");

        if (locations.length >= 1) {
            asciiname = locations[0];
            asciiname = asciiname.replace(", ", "").replace(",", "");
        }
        if (locations.length >= 2) {
            admin1 = locations[1];
            admin1 = admin1.replace(", ", "").replace(",", "");
        }
        if (locations.length >= 3) {
            country = locations[2];
            country = country + "%";
        }

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(query_destination)) {
            if (locations.length == 1 && !string.endsWith(", ")) {
                ps.setString(1, asciiname + "%");
            } else {
                ps.setString(1, asciiname);
            }
            if ((locations.length == 2 && !string.endsWith(", ")) ||
                    locations.length == 1) {
                ps.setString(2, admin1 + "%");
            } else {
                ps.setString(2, admin1);
            }
            ps.setString(3, country + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("asciiname");
                    double lat = rs.getDouble("latitude");
                    double lon = rs.getDouble("longitude");

                    Destination.Builder builder = new Builder(name, lat, lon);
                    builder.alternateNames(
                            rs.getString("alternateNames")).fcode(rs.getString("fcode"))
                            .country(rs.getString("country")).adminZone(rs.getString("admin1"))
                            .population(rs.getInt("population")).timezone(rs.getString("timezone"));

                    Destination destination = builder.build();
                    destinations.add(destination);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.sort(destinations);
        return destinations;
    }

    public CompletableFuture<Destination> getRandomLocationFuture() {
        return CompletableFuture.supplyAsync(() -> {
            Destination destination = null;

            try (Connection c = getConnection();
                 PreparedStatement ps = c.prepareStatement(query_random)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("asciiname");
                        double lat = rs.getDouble("latitude");
                        double lon = rs.getDouble("longitude");

                        Destination.Builder builder = new Builder(name, lat, lon);
                        builder.alternateNames(
                                rs.getString("alternateNames")).fcode(rs.getString("fcode"))
                                .country(rs.getString("country")).adminZone(rs.getString("admin1"))
                                .population(rs.getInt("population")).timezone(rs.getString("timezone"));

                        destination = builder.build();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return destination;
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
