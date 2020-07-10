package net.mcatlas.helpers.command;

import net.mcatlas.helpers.HelpersPlugin;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Please specify a username.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player has not played before.");
            return true;
        }

        if (target.isOnline()) {
            Player player = (Player) target;
            if (sender instanceof Player) {
                Player playerSender = (Player) sender;
                if (playerSender.canSee(player)) {
                    sendSeenWithTimeOnline(sender, target);
                    return true;
                }
            } else {
                sendSeenWithTimeOnline(sender, target);
                return true;
            }
        }

        long durationSinceSeen = System.currentTimeMillis() - target.getLastPlayed();

        sender.sendMessage(ChatColor.YELLOW + target.getName() + " was last seen " + DurationFormatUtils.formatDurationWords(durationSinceSeen, true, true) + " ago.");

        return true;
    }

    public void sendSeenWithTimeOnline(CommandSender sender, OfflinePlayer target) {
        long loggedOn = HelpersPlugin.get().getTimeOnline(target.getPlayer());
        long timeOnline = System.currentTimeMillis() - loggedOn;
        String timeOnlineString = DurationFormatUtils.formatDurationWords(timeOnline, true, true);
        sender.sendMessage(ChatColor.GREEN + target.getName() + " has been online for " + timeOnlineString);
    }

}
