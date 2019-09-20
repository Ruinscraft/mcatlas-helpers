package net.mcatlas.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (!(sender instanceof Player)) return false;

    	Map<Player, Integer> nearPlayers = getNearbyPlayers((Player) sender, 200);

    	String list = ChatColor.RED + "Nearby: ";
    	int i = 0;
    	for (Entry<Player, Integer> entry : nearPlayers.entrySet()) {
    		i++;
    		list = list.concat(ChatColor.WHITE + entry.getKey().getName() + " " +
    				ChatColor.GRAY + "(" + entry.getValue() + "m)");
    		if (i != nearPlayers.size()) {
    			list = list.concat(ChatColor.WHITE + ", ");
    		}
    	}

    	sender.sendMessage(list);
        return true;
    }

    public Map<Player, Integer> getNearbyPlayers(Player firstPlayer, double distance) {
    	Map<Player, Integer> map = new HashMap<Player, Integer>();

    	for (Player player : firstPlayer.getWorld().getPlayers()) {
    		if (player.getLocation().distance(firstPlayer.getLocation()) <= distance) {
    			map.put(player, (int) distance);
    		}
    	}

    	return map;
    }

}
