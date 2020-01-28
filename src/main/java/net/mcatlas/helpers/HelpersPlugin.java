package net.mcatlas.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.mcatlas.helpers.command.BroadcastCommand;
import net.mcatlas.helpers.command.EntityCountCommand;
import net.mcatlas.helpers.command.FlyCommand;
import net.mcatlas.helpers.command.GCCommand;
import net.mcatlas.helpers.command.HelpCommand;
import net.mcatlas.helpers.command.MapCommand;
import net.mcatlas.helpers.command.NearCommand;
import net.mcatlas.helpers.command.RulesCommand;
import net.mcatlas.helpers.command.SeenCommand;
import net.mcatlas.helpers.command.SpeedCommand;
import net.mcatlas.helpers.command.SuicideCommand;
import net.mcatlas.helpers.command.TPCommand;
import net.mcatlas.helpers.command.UptimeCommand;
import net.mcatlas.helpers.command.VoteCommand;

public class HelpersPlugin extends JavaPlugin implements Listener {

	public Map<UUID, Long> players;
	public Set<UUID> recentDeaths;

	// tps stuff
	private transient long lastPoll = System.nanoTime();
	private final LinkedList<Double> history = new LinkedList<>();
	private final long tickInterval = 50;

	private Random random = new Random();

	public static HelpersPlugin plugin;

	public static HelpersPlugin get() {
		return plugin;
	}

	public static final long STARTUP_TIME;

	static {
		STARTUP_TIME = System.currentTimeMillis();
	}

	@Override
	public void onEnable() {
		plugin = this;

		saveDefaultConfig();

		this.players = new HashMap<UUID, Long>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.players.put(player.getUniqueId(), System.currentTimeMillis());
		}
		this.recentDeaths = new HashSet<UUID>();

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new EntityListener(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new BedListener(), this);
		getServer().getPluginManager().registerEvents(new BoatExploitFix(), this);
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("rules").setExecutor(new RulesCommand());
		getCommand("map").setExecutor(new MapCommand());
		getCommand("seen").setExecutor(new SeenCommand());
		getCommand("help").setExecutor(new HelpCommand());
		getCommand("uptime").setExecutor(new UptimeCommand());
		getCommand("suicide").setExecutor(new SuicideCommand());
		getCommand("fly").setExecutor(new FlyCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("tp").setExecutor(new TPCommand());
		getCommand("gc").setExecutor(new GCCommand());
		getCommand("speed").setExecutor(new SpeedCommand());
		getCommand("entitycount").setExecutor(new EntityCountCommand());
		getCommand("near").setExecutor(new NearCommand());

		// tps stuff
		history.add(20D);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			final long startTime = System.nanoTime();
			long timeSpent = (startTime - lastPoll) / 1000;
			if (timeSpent == 0) {
				timeSpent = 1;
			}
			if (history.size() > 10) {
				history.remove();
			}
			double tps = tickInterval * 1000000.0 / timeSpent;
			if (tps <= 21) {
				history.add(tps);
			}
			lastPoll = startTime;

			long memory = getFreeMemory();
			if (memory < 400) {
				this.getLogger().warning(ChatColor.YELLOW + "Memory at " + memory + " MB!");
			}
		}, 1000, 50);
	}

	public double getAverageTPS() {
		double avg = 0;
		for (Double f : history) {
			if (f != null) {
				avg += f;
			}
		}
		return avg / history.size();
	}

	public long getAllocatedMemory() {
		return Runtime.getRuntime().totalMemory() / 1024 / 1024;
	}

	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory() / 1024 / 1024;
	}

	@Override
	public void onDisable() {
		plugin = null;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		final Player player = event.getPlayer();

		if (player.hasPermission("group.vip1")) {
			if (!player.hasPermission("group.sponsor")) {
				// add to sponsor rank in mcatlas server context
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"lp user " + player.getName() + " parent set mcatlas-sponsor server=mcatlas");
			}
		}

		getServer().getScheduler().runTaskLater(this, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2.0F, 0.2F);
		}, 10);

		if (player.getBedSpawnLocation() == null) {
			player.setCompassTarget(player.getLocation());
		} else {
			player.setCompassTarget(player.getBedSpawnLocation());
		}

		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage(ChatColor.GRAY + "Welcome to " + ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "MCATLAS");
		player.sendMessage("");
		player.sendMessage(ChatColor.YELLOW + "https://mcatlas.net/");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");

		this.players.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		this.players.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		UUID playerUUID = event.getEntity().getUniqueId();
		if (this.recentDeaths.contains(playerUUID)) {
			event.setDeathMessage(null);
			return;
		}
		this.recentDeaths.add(playerUUID);
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
			this.recentDeaths.remove(playerUUID);
		}, 20 * 90);
	}

	public long getTimeOnline(Player player) {
		return this.players.get(player.getUniqueId());
	}

	// chance out of 100
	public boolean chance(int outOf100) {
		return random.nextInt(100) < outOf100; 
	}

}
