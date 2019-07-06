package net.mcatlas.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GRAY + "Vote for MCATLAS and earn gold here: " + ChatColor.YELLOW + "https://mcatlas.net/vote");
        return true;
    }

}
