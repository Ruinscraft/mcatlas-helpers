package net.mcatlas.helpers.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setItemInMainHand(existingHelmet);
			player.sendMessage(ChatColor.GREEN + "Hat has been taken off.");
			return true;
		}

		player.getInventory().setHelmet(itemInHand);

		if (existingHelmet != null && existingHelmet.getType() != Material.AIR) {
			player.getInventory().setItemInMainHand(existingHelmet);
			player.sendMessage(ChatColor.GREEN + "Hat has been put on, and your previous hat taken off.");
		} else {
			player.sendMessage(ChatColor.GREEN + "Hat has been put on.");
		}

		return true;
	}

}
