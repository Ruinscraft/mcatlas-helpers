package net.mcatlas.helpers.command;

import net.mcatlas.helpers.HelpersPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JoinInfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Please write a message! (or type \"reset\" to reset the message");
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            HelpersPlugin.get().resetJoinInfoMessages();
            return true;
        }

        String combined = "";
        for (String arg : args) {
            combined += arg + " ";
        }
        combined = ChatColor.translateAlternateColorCodes('&', combined);
        String[] messages = combined.split("\\\\n");

        HelpersPlugin.get().setJoinInfoMessages(messages);

        sender.sendMessage(ChatColor.GREEN + "Here are the messages");
        sender.sendMessage("");
        for (String msg : messages) {
            sender.sendMessage(msg);
        }

        return true;
    }

}
