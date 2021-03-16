package net.mcatlas.helpers.command;

import net.mcatlas.helpers.HelpersPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HopperActionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Map.Entry<String, Integer>> hopperActions = new ArrayList<>(HelpersPlugin.get().getHopperActions().entrySet());
        hopperActions.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        int i = 1;
        sender.sendMessage(ChatColor.GOLD + "Top 15 hopper active areas:");
        for (Map.Entry<String, Integer> entry : hopperActions) {
            sender.sendMessage(ChatColor.GREEN + "" + i + ". " + entry.getKey() +
                    ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + entry.getValue());
            i++;
            if (i > 15) break;
        }
        return true;
    }

}