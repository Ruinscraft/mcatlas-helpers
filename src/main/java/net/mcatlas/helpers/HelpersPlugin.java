package net.mcatlas.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

public class HelpersPlugin extends JavaPlugin implements Listener {

	public Map<UUID, Long> players;
	public Set<UUID> recentDeaths;

	public static HelpersPlugin plugin;

	public static HelpersPlugin get() {
		return plugin;
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
        getServer().getPluginManager().registerEvents(new BedListener(), this);
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
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("entitycount").setExecutor(new EntityCountCommand());
        getCommand("near").setExecutor(new NearCommand());
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

}
