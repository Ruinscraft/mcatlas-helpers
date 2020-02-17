package net.mcatlas.helpers.command;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.md_5.bungee.api.ChatColor;

public class HatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		ItemStack existingHelmet = player.getInventory().getHelmet();

		// if holding nothing
		if (itemInHand == null || itemInHand.getType() == Material.AIR) {
			if (existingHelmet == null || existingHelmet.getType() == Material.AIR) { // if nothing on helmet either
				player.sendMessage(ChatColor.RED + "Hold something to put on your head!");
				return false;
			}

			// if has helmet, take helmet off
			setItem(player, new ItemStack(Material.AIR), existingHelmet);
			player.sendMessage(ChatColor.GREEN + "Hat has been taken off.");
			return true;
		}

		// if hat exists & holding something
		if (existingHelmet != null && existingHelmet.getType() != Material.AIR) {
			setItem(player, itemInHand, existingHelmet);

			if (itemInHand.getType() == existingHelmet.getType()) {
				player.sendMessage(ChatColor.GREEN + "Hat has been taken off.");
			} else {
				player.sendMessage(ChatColor.GREEN + "Hat has been put on, and your previous hat taken off.");
			}
		} else { // if hat doesn't exist & holding something
			setItem(player, itemInHand, new ItemStack(Material.AIR));
			player.sendMessage(ChatColor.GREEN + "Hat has been put on.");
		}

		return true;
	}

	public void setItem(Player player, ItemStack itemInHand, ItemStack existingHelmet) {
		PlayerInventory inventory = player.getInventory();
		// if hat stack and item held stack are both a single item, just swap
		if (itemInHand.getAmount() <= 1 && existingHelmet.getAmount() <= 1) {
			inventory.setHelmet(itemInHand);
			inventory.setItemInMainHand(existingHelmet);
		} else if (itemInHand.getType() == existingHelmet.getType()) { // if hat and item held are same type
			inventory.setHelmet(new ItemStack(Material.AIR));
			Collection<ItemStack> items = inventory.addItem(existingHelmet).values();
			for (ItemStack item : items) {
				if (item == null || item.getType() == Material.AIR) continue;
				inventory.getLocation().getWorld().dropItem(inventory.getLocation(), item);
				player.sendMessage(ChatColor.YELLOW + "Your hat has dropped.");
			}
		} else { // what usually happens
			itemInHand.setAmount(itemInHand.getAmount() - 1);
			inventory.setItemInMainHand(itemInHand);
			itemInHand.setAmount(1);
			inventory.setHelmet(itemInHand);
			// if the inventory is full and we attempt to add items, this collection contains those items
			Collection<ItemStack> items = inventory.addItem(existingHelmet).values();
			for (ItemStack item : items) {
				if (item == null || item.getType() == Material.AIR) continue;
				inventory.getLocation().getWorld().dropItem(inventory.getLocation(), item);
				player.sendMessage(ChatColor.YELLOW + "Your hat has dropped.");
			}
		}
	}

}
