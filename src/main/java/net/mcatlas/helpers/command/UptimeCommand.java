package net.mcatlas.helpers.command;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.mcatlas.helpers.HelpersPlugin;

public class UptimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long diff = System.currentTimeMillis() - HelpersPlugin.STARTUP_TIME;

        sender.sendMessage(ChatColor.YELLOW + "The server has been up for " + DurationFormatUtils.formatDurationWords(diff, true, true));

        return true;
    }

}
