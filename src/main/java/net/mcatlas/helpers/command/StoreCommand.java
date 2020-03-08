package net.mcatlas.helpers.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class StoreCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.AQUA + "You can visit our store at " + ChatColor.YELLOW + "https://store.ruinscraft.com/");
		return false;
	}

}
