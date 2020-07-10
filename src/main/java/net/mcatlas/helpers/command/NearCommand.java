package net.mcatlas.helpers.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

        if (nearPlayers.isEmpty()) {
            list = list + ChatColor.GRAY + "None";
        }

        sender.sendMessage(list);
        return true;
    }

    public Map<Player, Integer> getNearbyPlayers(Player firstPlayer, double maxDistance) {
        Map<Player, Integer> map = new HashMap<Player, Integer>();

        for (Player player : firstPlayer.getWorld().getPlayers()) {
            if (player.equals(firstPlayer)) continue;
            if (!firstPlayer.canSee(player)) continue;
            int playerDistance = (int) player.getLocation().distance(firstPlayer.getLocation());
            if (player.getLocation().distance(firstPlayer.getLocation()) <= maxDistance) {
                map.put(player, (int) playerDistance);
            }
        }

        return map;
    }

}
