package net.mcatlas.helpers.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TPCommand implements CommandExecutor {

    private static final Map<Player, Location> lastLocations = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (label.toLowerCase().equals("back")) {
            if (lastLocations.get(player) == null) {
                player.sendMessage(ChatColor.RED + "No last location");
                return true;
            }

            player.teleport(lastLocations.get(player));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Specify a player");
            return true;
        }

        int x, y, z;
        try {
        	x = Integer.valueOf(args[0]);
        	y = Integer.valueOf(args[1]);
        	z = Integer.valueOf(args[2]);
        	player.teleport(new Location(player.getWorld(), x, y, z));
        	player.sendMessage(ChatColor.YELLOW + "Teleported to " + x + " " + y + " " + z + ".");
        	return true;
        } catch (Exception e) { }

        List<Player> targets = Bukkit.matchPlayer(args[0]);

        if (targets.isEmpty()) {
            player.sendMessage(ChatColor.RED + args[0] + " is not online");
            return true;
        }

        Player target = targets.get(0);

        if (label.toLowerCase().equals("tphere") && player.hasPermission("mcatlas.command.tphere")) {
            target.teleport(player.getLocation());
            return true;
        }

        lastLocations.put(player, player.getLocation());

        player.teleport(target.getLocation());
    	player.sendMessage(ChatColor.YELLOW + "Teleported to " + target.getName());

        return true;
    }
}
