package net.mcatlas.helpers.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RenderDistanceCommand implements CommandExecutor {

    private List<Player> recents = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "For technical reasons, this command is no longer supported.");

//        if (!(sender instanceof Player)) return false;
//        Player player = (Player) sender;
//
//        if (args.length == 0) {
//            player.sendMessage(ChatColor.RED + "/renderdistance <1-8>");
//            return false;
//        }
//
//        int dist;
//        try {
//            dist = Integer.valueOf(args[0]);
//        } catch (Exception e) {
//            player.sendMessage(ChatColor.RED + "Please enter a number between 1 and 8");
//            return false;
//        }
//
//        if (dist > 8) {
//            player.sendMessage(ChatColor.RED + "Limit is 8!");
//            return false;
//        }
//
//        if (recents.contains(player)) {
//            player.sendMessage(ChatColor.RED + "You've sent this command too recently!");
//            return false;
//        }
//
//        this.recents.add(player);
//        Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
//            this.recents.remove(player);
//        }, 20 * 8);
//
//        player.setViewDistance(dist);
//        player.sendMessage(ChatColor.AQUA + "Render distance set to " + ChatColor.YELLOW + dist);
//        player.sendMessage(ChatColor.AQUA + "Make sure it's set at or above " +
//                ChatColor.YELLOW + dist + ChatColor.AQUA + " in your Video Settings!");

        return true;
    }

}
