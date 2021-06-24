package net.mcatlas.helpers.command;

import net.mcatlas.helpers.Coordinate;
import net.mcatlas.helpers.HelpersPlugin;
import net.mcatlas.helpers.geonames.Destination;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhereIsCommand implements CommandExecutor, TabCompleter {

    private List<Player> recents = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!HelpersPlugin.get().hasStorage()) {
            sender.sendMessage(ChatColor.RED + "This command is not currently enabled.");
            return false;
        }

        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (args.length == 0 || (args.length == 1 && args[0].length() < 3)) {
            player.sendMessage(ChatColor.RED + "Not enough arguments!");
            player.sendMessage(ChatColor.RED + "/whereis <place>, example: /whereis New York City");
            return false;
        }

        if (recents.contains(player)) {
            player.sendMessage(ChatColor.RED + "You've used this command too recently!");
            return false;
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "This command can only be used in the Overworld!");
            return false;
        }

        HelpersPlugin.get().getStorage().getDestinationsFromLocationNameFuture(String.join(" ", args)).thenAccept(destinations -> {
            if (destinations == null || destinations.size() == 0) {
                player.sendMessage(ChatColor.RED + "There were no locations with this name.");
                return;
            }

            Destination best = destinations.get(0);
            double lat = best.getLat();
            double lon = best.getLong();
            Coordinate coord = Coordinate.getMCFromLife(lat, lon);
            int x = (int) coord.getX();
            int z = (int) coord.getY();

            player.sendMessage(ChatColor.GREEN + best.getFormattedName());
            player.sendMessage(ChatColor.GREEN + "X: " + ChatColor.YELLOW + x + ChatColor.GREEN + " Z: " + ChatColor.YELLOW + z);
            String dynmapLink = HelpersPlugin.get().getDynmapLink()
                    .replace("[X]", "" + x)
                    .replace("[Z]", "" + z)
                    .replace("[ZOOM]", "" + 8);
            player.sendMessage(ChatColor.GRAY + dynmapLink);
        });

        this.recents.add(player);
        Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
            this.recents.remove(player);
        }, 20 * 10); // 12 sec
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // GotoTabCompletion handles this
        // if GotoTabCompletion doesn't complete, it checks here
        return new ArrayList<>();
    }

}
