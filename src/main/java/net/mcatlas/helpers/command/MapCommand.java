package net.mcatlas.helpers.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MapCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.GRAY + "Find the live map here: " + ChatColor.YELLOW + "https://mcatlas.net/map");
		sender.sendMessage(ChatColor.GRAY + "Type " + ChatColor.YELLOW + "/whereami " + ChatColor.GRAY +
				"to see where you would be in real life!");

		return true;
	}

}
