package net.mcatlas.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityClickListener implements Listener {
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;
		if (entity instanceof Tameable) {
			Tameable tamed = (Tameable) entity;
			if (!tamed.isTamed()) return;
			if (tamed.getOwner().equals(player)) return;
			if (!player.hasPermission("mcatlas.tamedowner")) return;
			player.sendMessage(ChatColor.GREEN + "The owner of this creature is " 
					+ ChatColor.GOLD + tamed.getOwner());
		}
	}

}
