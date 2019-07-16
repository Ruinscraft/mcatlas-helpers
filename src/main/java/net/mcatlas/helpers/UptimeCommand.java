package net.mcatlas.helpers;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UptimeCommand implements CommandExecutor {

    private static final long STARTUP_TIME;

    static {
        STARTUP_TIME = System.currentTimeMillis();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long diff = System.currentTimeMillis() - STARTUP_TIME;

        sender.sendMessage(ChatColor.YELLOW + "The server has been up for " + DurationFormatUtils.formatDurationWords(diff, true, true));

        return true;
    }

}
