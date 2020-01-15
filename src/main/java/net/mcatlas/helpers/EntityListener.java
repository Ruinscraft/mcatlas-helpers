package net.mcatlas.helpers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityListener implements Listener {

	Set<UUID> recent = new HashSet<>();
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("mcatlas.tamedowner")) return;

		if (recent.contains(player.getUniqueId())) return;
		recent.add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
			recent.remove(player.getUniqueId());
		}, 20);

		Entity entity = event.getRightClicked();
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;
		if (entity instanceof Tameable) {
			Tameable tamed = (Tameable) entity;
			if (!tamed.isTamed()) return;
			if (tamed.getOwner().equals(player)) return;
			player.sendMessage(ChatColor.GREEN + "The owner of this creature is " 
					+ ChatColor.GOLD + tamed.getOwner().getName());
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Player killer = event.getEntity().getKiller();
		if (killer == null) return;
		if (event.getEntity() instanceof Tameable) {
			Tameable tamed = (Tameable) event.getEntity();
			if (tamed == null || tamed.getOwner() == null) return;
			Bukkit.getLogger().info(killer.getName() + " killed " 
					+ tamed.getOwner().getName() + "'s " + tamed.getType().name());
		}
	}

}
