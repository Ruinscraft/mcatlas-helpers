package net.mcatlas.helpers.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.mcatlas.helpers.HelpersPlugin;
import net.mcatlas.helpers.HelpersPlugin.Coordinate;
import net.mcatlas.helpers.geonames.Destination;
import net.md_5.bungee.api.ChatColor;

public class GotoCommand implements CommandExecutor, TabCompleter {

	private List<Player> recents = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!HelpersPlugin.get().hasStorage()) {
			sender.sendMessage(ChatColor.RED + "This command is not currently enabled.");
			return false;
		}

		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if (args.length == 0 || (args.length == 1 && args[0].length() < 3)) {
			player.sendMessage(ChatColor.RED + "Not enough arguments!");
			player.sendMessage(ChatColor.RED + "/goto <place>, example: /goto New York City");
			return false;
		}

		if (recents.contains(player)) {
			player.sendMessage(ChatColor.RED + "You've used this command too recently!");
			return false;
		}

		if (!Bukkit.getWorlds().get(0).equals(player.getWorld())) {
			player.sendMessage(ChatColor.RED + "This command can only be used in the Overworld!");
			return false;
		}

		HelpersPlugin.get().getStorage().getAutoCompleteFuture(String.join(" ", args)).thenAccept(destinations -> {
			if (destinations == null || destinations.size() == 0) {
				player.sendMessage(ChatColor.RED + "There were no locations with this name.");
				return;
			}

			Collections.sort(destinations);
			Destination best = destinations.get(0);
			double lat = best.getLat();
			double lon = best.getLong();
			Coordinate coord = HelpersPlugin.get().getMCFromLife(lat, lon);
			int x = coord.getX();
			int z = coord.getY();
			System.out.println(best.getFormattedName() + " " + x + " " + z);

			Bukkit.getScheduler().runTask(HelpersPlugin.get(), () -> {
				Location teleportTo = new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z) + 1, z);
				if (teleportTo.getBlock().getBlockData().getMaterial() == Material.LAVA ||
						teleportTo.getBlock().getBlockData().getMaterial() == Material.CACTUS) {
					player.sendMessage(ChatColor.RED + "There is lava or cactus at this location! Too unsafe!");
					return;
				}

				player.sendMessage(ChatColor.YELLOW + "Teleporting in 3 seconds...");

				Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
					player.teleportAsync(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z) + 1, z));
					player.sendMessage(ChatColor.YELLOW + "You've been teleported to " + 
							ChatColor.GREEN + best.getFormattedName());
				}, 20 * 3);
			});
		});

		this.recents.add(player);
		Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
			this.recents.remove(player);
		}, 20 * 5);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// GotoTabCompletion handles this
		// if GotoTabCompletion doesn't complete, it checks here
		return new ArrayList<>();
	}

}
