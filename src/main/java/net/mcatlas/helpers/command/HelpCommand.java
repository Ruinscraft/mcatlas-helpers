package net.mcatlas.helpers.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Find tutorials on how to play this server here: " + ChatColor.YELLOW + "https://github.com/Ruinscraft/mcatlas-wiki/wiki");
        sender.sendMessage(ChatColor.AQUA + "Visit the website here: " + ChatColor.YELLOW + "https://mcatlas.net");
        return true;
    }

}
