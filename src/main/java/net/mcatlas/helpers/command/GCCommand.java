package net.mcatlas.helpers.command;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.mcatlas.helpers.HelpersPlugin;

// much of this code is from EssentialsX!
public class GCCommand implements CommandExecutor {

	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long diff = System.currentTimeMillis() - HelpersPlugin.STARTUP_TIME;

        sender.sendMessage(ChatColor.GOLD + "(Time online may be inaccurate)");
        sender.sendMessage(ChatColor.GOLD + "The server has been up for " + 
        		ChatColor.GREEN + DurationFormatUtils.formatDurationWords(diff, true, true));

        double tps = HelpersPlugin.get().getAverageTPS();
        String tpsString = "" + tps;
        if (tpsString.length() > 5) tpsString = tpsString.substring(0, 5);

        if (tps < 16) {
        	sender.sendMessage(ChatColor.GOLD + "Average TPS: " + ChatColor.RED + tpsString);
        } else if (tps < 18) {
        	sender.sendMessage(ChatColor.GOLD + "Average TPS: " + ChatColor.YELLOW + tpsString);
        } else {
        	sender.sendMessage(ChatColor.GOLD + "Average TPS: " + ChatColor.GREEN + tpsString);
        }

        long allocated = HelpersPlugin.get().getAllocatedMemory();
        long free = HelpersPlugin.get().getFreeMemory();

        if (free < 450) {
        	sender.sendMessage(ChatColor.GOLD + "Memory: " + 
        			ChatColor.RED + free + ChatColor.GREEN + " / " + allocated + " MB");
        } else if (free < 1000) {
        	sender.sendMessage(ChatColor.GOLD + "Memory: " + 
        			ChatColor.YELLOW + free + ChatColor.GREEN + " / " + allocated + " MB");
        } else {
        	sender.sendMessage(ChatColor.GOLD + "Memory: " + 
        			ChatColor.GREEN + free + ChatColor.GREEN + " / " + allocated + " MB");
        }

        return true;
    }

}
