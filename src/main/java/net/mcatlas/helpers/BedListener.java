package net.mcatlas.helpers;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BedListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType().name().contains("_BED")) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "Set your spawnpoint.");
                event.getPlayer().setBedSpawnLocation(event.getClickedBlock().getLocation());

                if (event.getPlayer().getBedSpawnLocation() != null) {
                    event.getPlayer().setCompassTarget(event.getPlayer().getBedSpawnLocation());
                }
            }
        }
    }

}
