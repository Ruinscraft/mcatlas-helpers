package net.mcatlas.helpers.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMSurvivalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        ((Player) sender).setGameMode(GameMode.SURVIVAL);
        sender.sendMessage(ChatColor.AQUA + "Gamemode set to Survival");
        return false;
    }

}
