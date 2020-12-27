package net.mcatlas.helpers;

import net.mcatlas.helpers.command.*;
import net.mcatlas.helpers.geonames.GotoTabCompletion;
import net.mcatlas.helpers.geonames.MySQLStorage;
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

import java.lang.management.ManagementFactory;
import java.util.*;

public class HelpersPlugin extends JavaPlugin implements Listener {

    public static final long STARTUP_TIME;
    public static HelpersPlugin plugin;

    static {
        STARTUP_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    private final LinkedList<Double> history = new LinkedList<>();
    private final long tickInterval = 50;

    public Map<UUID, Long> players;
    public Set<UUID> recentDeaths;
    public Set<UUID> recentPVPed;

    // tps stuff
    private transient long lastPoll = System.nanoTime();

    private Random random = new Random();
    private MySQLStorage storage = null;
    private double scaling = 120;

    public static HelpersPlugin get() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        this.scaling = getConfig().getInt("scaling", 120);

        if (getConfig().getBoolean("storage.mysql.use")) {
            String host = getConfig().getString("storage.mysql.host");
            int port = getConfig().getInt("storage.mysql.port");
            String database = getConfig().getString("storage.mysql.database");
            String table = getConfig().getString("storage.mysql.table");
            String username = getConfig().getString("storage.mysql.username");
            String password = getConfig().getString("storage.mysql.password");
            this.storage = new MySQLStorage(host, port, database, table, username, password);
            this.getLogger().info("MySQL enabled!");
        }

        this.players = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.players.put(player.getUniqueId(), System.currentTimeMillis());
        }

        this.recentDeaths = new HashSet<>();
        this.recentPVPed = new HashSet<>();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new BedListener(), this);
        getServer().getPluginManager().registerEvents(new ExploitFixes(), this);
        getServer().getPluginManager().registerEvents(new GotoTabCompletion(), this);
        getServer().getPluginManager().registerEvents(new WorldTeleportListener(), this);

        getCommand("vote").setExecutor(new VoteCommand());
        getCommand("rules").setExecutor(new RulesCommand());
        getCommand("map").setExecutor(new MapCommand());
        getCommand("seen").setExecutor(new SeenCommand());
        getCommand("help").setExecutor(new HelpCommand());
        getCommand("history").setExecutor(new HistoryCommand());
        getCommand("goto").setExecutor(new GotoCommand());
        getCommand("whereami").setExecutor(new WhereAmICommand());
        getCommand("border").setExecutor(new BorderCommand());
        getCommand("renderdistance").setExecutor(new RenderDistanceCommand());
        getCommand("hat").setExecutor(new HatCommand());
        getCommand("uptime").setExecutor(new UptimeCommand());
        getCommand("suicide").setExecutor(new SuicideCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("tp").setExecutor(new TPCommand());
        getCommand("gc").setExecutor(new GCCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("entitycount").setExecutor(new EntityCountCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("store").setExecutor(new StoreCommand());
        getCommand("gmc").setExecutor(new GMCreativeCommand());
        getCommand("gmsp").setExecutor(new GMSpectatorCommand());
        getCommand("gms").setExecutor(new GMSurvivalCommand());

        setupTPS();

        // Handles lilypad growing
        getServer().getScheduler().scheduleSyncRepeatingTask(
                this, new LilypadRecipeHandler(plugin), 120, 200);

        // Removes donkeys / mules / llamas to prevent a dupe bug. This is hopefully temporary
        getServer().getScheduler().scheduleSyncRepeatingTask(
                this, new RemoveChestedHorseTask(getLogger()), 120, 80);
    }

    public void setupTPS() {
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
                this.getLogger().warning(ChatColor.YELLOW + "Low memory! " + memory + " MB remaining.");
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

    public MySQLStorage getStorage() {
        return this.storage;
    }

    public boolean hasStorage() {
        return this.storage != null;
    }

    @Override
    public void onDisable() {

        if (storage != null) {
            storage.close();
        }

        plugin = null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        final Player player = event.getPlayer();

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

        if (player.hasPermission("mcatlas.increasedrenderdistance")) {
            player.setViewDistance(8);
        }

        // if Sponsor, show them their perks
        if (player.hasPermission("group.sponsor")) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Thank you for your sponsorship!");
            player.sendMessage(ChatColor.GRAY + "Your benefits include: High View Distance, /hat Command, /goto Command, Better Vote Rewards, Powders, Check Owner of Tamed Mob, Get Skulls of Killed Players");
        }

        this.players.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        this.players.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

    public double getScaling() {
        return this.scaling;
    }

    // chance out of 100
    public boolean chance(int outOf100) {
        return random.nextInt(100) < outOf100;
    }

    public Random getRandom() {
        return this.random;
    }

}
