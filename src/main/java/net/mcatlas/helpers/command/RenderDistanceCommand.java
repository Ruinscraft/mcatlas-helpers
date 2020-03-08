package net.mcatlas.helpers.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mcatlas.helpers.HelpersPlugin;
import net.md_5.bungee.api.ChatColor;

public class RenderDistanceCommand implements CommandExecutor {

	private List<Player> recents = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "/renderdistance <1-32>");
			return false;
		}

		int dist;
		try {
			dist = Integer.valueOf(args[0]);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Please enter a number between 1 and 16");
			return false;
		}

		if (dist > 32) {
			player.sendMessage(ChatColor.RED + "Limit is 32!");
			return false;
		}

		if (recents.contains(player)) {
			player.sendMessage(ChatColor.RED + "You've sent this command too recently!");
			return false;
		}

		this.recents.add(player);
		Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
			this.recents.remove(player);
		}, 20 * 8);

		player.setViewDistance(dist);
		player.sendMessage(ChatColor.AQUA + "Render distance set to " + ChatColor.YELLOW + dist);
		player.sendMessage(ChatColor.AQUA + "Make sure it's set at or above " + 
				ChatColor.YELLOW + dist + ChatColor.AQUA + " in your Video Settings!");

		return true;
	}

}
