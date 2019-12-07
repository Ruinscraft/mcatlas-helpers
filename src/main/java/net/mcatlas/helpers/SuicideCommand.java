package net.mcatlas.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SuicideCommand implements CommandExecutor {

    private static final Map<UUID, Long> recent = new HashMap<>();
    private static final long WAIT_TIME = TimeUnit.SECONDS.toMillis(30);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "This command cannot be used to kill other players.");
            return true;
        }

        Player player = (Player) sender;

        if (recent.containsKey(player.getUniqueId())) {
            if (System.currentTimeMillis() - recent.get(player.getUniqueId()) < WAIT_TIME) {
                player.sendMessage(ChatColor.RED + "You must wait a bit before using this command again.");
                return true;
            }
        }

        player.setHealth(0.0F);

        recent.put(player.getUniqueId(), System.currentTimeMillis());

        return true;
    }

}
