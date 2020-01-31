package net.mcatlas.helpers.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.mcatlas.helpers.HelpersPlugin;

public class MapCommand implements CommandExecutor {

	private Set<UUID> recentFourSquareAPIUsers = new HashSet<>();
	private static final int RECENT_MAX_SIZE = 15;
	private static final int TIME_BETWEEN_USE = 15;
	private boolean apiOffline;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String address = null;

		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		double x = player.getLocation().getX();
		double z = player.getLocation().getZ();

		if (canFindLocation(player)) {
			recentFourSquareAPIUsers.add(player.getUniqueId());
			HelpersPlugin.get().getServer().getScheduler().scheduleSyncDelayedTask(HelpersPlugin.get(), () -> {
				recentFourSquareAPIUsers.remove(player.getUniqueId());
			}, 20 * TIME_BETWEEN_USE);

			address = getAddress(x, z, false);
		}

		if (address == null) {
			if (HelpersPlugin.get().hasStorage()) {
				// query nearby
			}
		}

		sender.sendMessage(ChatColor.GRAY + "Find the live map here: " + ChatColor.YELLOW + "https://mcatlas.net/map");
		if (address != null) {
			sender.sendMessage(ChatColor.GRAY + "You're currently near " + address);
		}
		return true;
	}

	public boolean canFindLocation(Player player) {
		UUID uuid = player.getUniqueId();

		if (player.getWorld() != player.getServer().getWorlds().get(0)) return false;
		if (recentFourSquareAPIUsers.contains(uuid)) return false;
		if (recentFourSquareAPIUsers.size() > RECENT_MAX_SIZE) return false;

		if (player.isSwimming()) {
			return false;
		} else if (player.isInsideVehicle()) {
			Entity vehicle = player.getVehicle();
			if (vehicle instanceof Boat) {
				return false;
			}
		}

		return true;
	}

	public String getAddress(double x, double z, boolean largeRadius) {
		JsonObject rootObj = getLocationData(getRealLifeCoord(x, z), largeRadius);

		JsonObject bestVenue = getBestVenue(rootObj, getRealLifeX(z), getRealLifeZ(x));
		if (bestVenue == null && !largeRadius) {
			return getAddress(x, z, true);
		} else if (bestVenue == null && largeRadius) {
			return null;
		}

		JsonObject location = bestVenue.getAsJsonObject("location");

		String city = null;
		if (location.get("city") != null) {
			city = location.get("city").getAsString();
		}
		String state = null;
		if (location.get("state") != null) {
			state = location.get("state").getAsString();
		}
		String country = null;
		if (location.get("country") != null) {
			country = location.get("country").getAsString();
		}

		String address;
		if (city == null || largeRadius) {
			address = state + ", " + country;
		} else if (city.equals(state)) {
			address = state + ", " + country;
		} else {
			address = city + ", " + state + ", " + country;
		}
		return address;
	}

	public JsonObject getBestVenue(JsonObject rootObj, double xReal, double zReal) {
		JsonObject responseObj = rootObj.getAsJsonObject("response");
		JsonArray venues = responseObj.getAsJsonArray("venues");

		if (venues.size() == 0) return null;

		double nearest = Double.MAX_VALUE;
		JsonObject bestVenue = null;
		for (int i = 0; i < venues.size(); i++) {
			JsonObject venue = venues.get(i).getAsJsonObject();
			JsonObject location = venue.getAsJsonObject("location");
			if (location.get("country") == null) continue;
			if (location.get("state") == null) continue;
			double xNear = location.get("lat").getAsDouble();
			double zNear = location.get("lng").getAsDouble();
			if (Math.abs((xReal - xNear) + (zReal - zNear)) < nearest) {
				bestVenue = venue;
			}
		}
		return bestVenue;
	}

	public double getRealLifeX(double z) {
		return z / -120;
	}

	public double getRealLifeZ(double x) {
		return x / 120;
	}

	public String getRealLifeCoord(double x, double z) {
		return getRealLifeX(z) + "," + getRealLifeZ(x);
	}

	public JsonObject getLocationData(Location location, boolean largeRadius) {
		return getLocationData(getRealLifeCoord(location.getX(), location.getZ()), largeRadius);
	}

	public JsonObject getLocationData(double xReal, double zReal, boolean largeRadius) {
		return getLocationData(xReal + ", " + zReal, largeRadius);
	}

	public JsonObject getLocationData(String coord, boolean largeRadius) {
		String client_id = HelpersPlugin.get().getConfig().getString("clientId");
		String client_secret = HelpersPlugin.get().getConfig().getString("clientSecret");
		String updated_date = HelpersPlugin.get().getConfig().getString("updatedDate");
		String link = "https://api.foursquare.com/v2/venues/search" + 
				"?client_id=" + client_id + 
				"&client_secret=" + client_secret + 
				"&ll=" + coord +
				"&v=" + updated_date +
				"&locale=en";
		if (largeRadius) {
			link = link + "&radius=100000";
		} else {
			link = link + "&radius=2500";
		}

		URL url = null;
		HttpURLConnection connection = null;

		try {
			url = new URL(link);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
		} catch (Exception e) { // any issue with the connection, like an error code
			int code = 0;
			try {
				code = connection.getResponseCode();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (code == 500 && apiOffline) { // api is known to be offline
				return null; 
			} else if (code == 500 && !apiOffline) { // api is not known to be offline
				Bukkit.getLogger().warning("FourSquare API went offline! No location data for now.");
				apiOffline = true;
				return null; 
			} else if (code == 400) { // bad request (happens occasionally)
				return null;
			} else { // anything else
				e.printStackTrace();
				return null;
			}
		}

		JsonParser jp = new JsonParser();
		JsonElement root;
		try {
			root = jp.parse(new InputStreamReader((InputStream) connection.getContent()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (apiOffline) {
			Bukkit.getLogger().info("FourSquare API is back online.");
			apiOffline = false;
		}

		return root.getAsJsonObject();
	}

}
