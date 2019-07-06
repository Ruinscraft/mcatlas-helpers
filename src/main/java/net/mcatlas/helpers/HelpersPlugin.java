package net.mcatlas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpersPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new BedListener(), this);
        getCommand("vote").setExecutor(new VoteCommand());
        getCommand("rules").setExecutor(new RulesCommand());
        getCommand("map").setExecutor(new MapCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("tp").setExecutor(new TPCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("entitycount").setExecutor(new EntityCountCommand());
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
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

}
