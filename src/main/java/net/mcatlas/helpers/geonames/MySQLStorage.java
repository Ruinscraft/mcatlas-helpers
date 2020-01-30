package net.mcatlas.helpers.geonames;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.zaxxer.hikari.HikariDataSource;

import net.mcatlas.helpers.geonames.Destination.Builder;

public class MySQLStorage {

	private HikariDataSource dataSource;

	private String query_destination;

	public MySQLStorage(String host, int port,
			String database, String geoTable, String username, String password) {
		query_destination = "SELECT * FROM " + geoTable + " WHERE asciiname LIKE ? " + 
			" AND admin1 LIKE ? AND country LIKE ?;";

		dataSource = new HikariDataSource();

		dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setPoolName("destination-pool");
		dataSource.setMaximumPoolSize(3);
		dataSource.setConnectionTimeout(3000);
		dataSource.setLeakDetectionThreshold(3000);
	}

	public CompletableFuture<List<Destination>> getAutoCompleteFuture(String string) {
		return CompletableFuture.supplyAsync(() -> {
			return getAutoComplete(string);
		});
	}

	public List<Destination> getAutoComplete(String string) {
		List<Destination> destinations = new ArrayList<>();

		String asciiname = "";
		String admin1 = "";
		String country = "";

		string = string.replace("§r", "").replace("§a", "");
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
		}

		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(query_destination)) {
			ps.setString(1, asciiname + "%");
			ps.setString(2, admin1 + "%");
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

		return destinations;
	}

	public void close() {
		dataSource.close();
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}
