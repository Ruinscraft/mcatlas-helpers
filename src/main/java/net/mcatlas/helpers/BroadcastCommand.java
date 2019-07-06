package net.mcatlas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Please supply a msg");
            return true;
        }

        String msg = String.join(" ", args);

        msg = ChatColor.translateAlternateColorCodes('&', msg);

        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Broadcast" + ChatColor.GRAY + "] " + ChatColor.LIGHT_PURPLE + msg);

        return true;
    }

}
