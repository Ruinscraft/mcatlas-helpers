package net.mcatlas.helpers.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class RenderDistanceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "/renderdistance <1-16>");
			return false;
		}

		int dist;
		try {
			dist = Integer.valueOf(args[0]);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Please enter a number between 1 and 16");
			return false;
		}

		player.setViewDistance(dist);
		player.sendMessage(ChatColor.GRAY + "Render distance set to " + ChatColor.YELLOW + dist);
		player.sendMessage(ChatColor.GRAY + "Make sure it's set at or above " + 
				ChatColor.YELLOW + dist + ChatColor.GRAY + " in your Video Settings!");

		return true;
	}

}
