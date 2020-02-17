package net.mcatlas.helpers.command;

import java.util.Collection;

import org.bukkit.Bukkit;
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

		if (itemInHand == null || itemInHand.getType() == Material.AIR) {
			if (existingHelmet == null || existingHelmet.getType() == Material.AIR) {
				player.sendMessage(ChatColor.RED + "Hold something to put on your head!");
				return false;
			}

			setItem(player, new ItemStack(Material.AIR), existingHelmet);
			player.sendMessage(ChatColor.GREEN + "Hat has been taken off.");
			return true;
		}

		if (existingHelmet != null && existingHelmet.getType() != Material.AIR) {
			setItem(player, itemInHand, existingHelmet);
			player.sendMessage(ChatColor.GREEN + "Hat has been put on, and your previous hat taken off.");
		} else {
			setItem(player, itemInHand, new ItemStack(Material.AIR));
			player.sendMessage(ChatColor.GREEN + "Hat has been put on.");
		}

		return true;
	}

	public void setItem(Player player, ItemStack itemInHand, ItemStack existingHelmet) {
		PlayerInventory inventory = player.getInventory();
		if (itemInHand.getAmount() <= 1 && existingHelmet.getAmount() <= 1) {
			inventory.setHelmet(itemInHand);
			inventory.setItemInMainHand(existingHelmet);
		} else if (itemInHand.getType() == existingHelmet.getType()) {
			inventory.setHelmet(new ItemStack(Material.AIR));
			itemInHand.setAmount(itemInHand.getAmount() + 1);
			inventory.setItemInMainHand(itemInHand);
		} else {
			itemInHand.setAmount(itemInHand.getAmount() - 1);
			inventory.setItemInMainHand(itemInHand);
			itemInHand.setAmount(1);
			inventory.setHelmet(itemInHand);
			Collection<ItemStack> items = inventory.addItem(existingHelmet).values();
			for (ItemStack item : items) {
				inventory.getLocation().getWorld().dropItem(inventory.getLocation(), item);
				player.sendMessage(ChatColor.YELLOW + "Your hat has dropped.");
			}
		}
	}

}
