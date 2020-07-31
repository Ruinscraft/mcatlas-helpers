package net.mcatlas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldTeleportListener implements Listener {

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World nextWorld = player.getWorld();
        World prevWorld = event.getFrom().getWorld();

        // Not changing worlds
        if (prevWorld.equals(nextWorld)) {
            return;
        }

        // Coming from an End world
        if (prevWorld.getEnvironment() == World.Environment.THE_END) {
            if (player.hasPermission("mcatlas.increasedrenderdistance")) {
                player.setViewDistance(8);
            } else {
                player.setViewDistance(Bukkit.getViewDistance());
            }
        }

        // Going to an End world
        else if (nextWorld.getEnvironment() == World.Environment.THE_END) {
            if (player.hasPermission("mcatlas.increasedrenderdistance")) {
                player.setViewDistance(12);
            } else {
                player.setViewDistance(8);
            }
        }
    }

}
