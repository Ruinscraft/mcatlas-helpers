package net.mcatlas.helpers.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class BorderCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.GRAY + "Earth: " + ChatColor.UNDERLINE + 
				"+/- 43008x" + ChatColor.RESET + "" + ChatColor.GRAY + ", " + ChatColor.UNDERLINE + "+/- 21504z");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GRAY + "Nether: " + ChatColor.UNDERLINE + 
				"+/- 5376x" + ChatColor.RESET + "" + ChatColor.GRAY + ", " + ChatColor.UNDERLINE + "+/- 2688z");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GRAY + "The Nether border is passable, but you'll need to travel back " + 
				"to a portal within the border to get back to Earth.");
		return false;
	}

}
