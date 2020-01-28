package net.mcatlas.helpers.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class EntityCountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (World world : Bukkit.getWorlds()) {
            commandSender.sendMessage(ChatColor.RED + "Entity counts for world: " + world.getName());

            Map<EntityType, Integer> counts = new HashMap<>();

            for (EntityType entityType : EntityType.values()) {
                counts.put(entityType, 0);
            }

            for (Entity entity : world.getEntities()) {
                counts.put(entity.getType(), counts.get(entity.getType()) + 1);
            }

            for (Map.Entry<EntityType, Integer> count : counts.entrySet()) {
                if (count.getValue() == 0) {
                    continue;
                }

                commandSender.sendMessage(ChatColor.GOLD + count.getKey().name() + ChatColor.RESET + ": " + count.getValue());
            }
        }

        return true;
    }

}
