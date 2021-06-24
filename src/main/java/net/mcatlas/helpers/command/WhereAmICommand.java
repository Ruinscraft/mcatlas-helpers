package net.mcatlas.helpers.command;

import net.mcatlas.helpers.Coordinate;
import net.mcatlas.helpers.HelpersPlugin;
import net.mcatlas.helpers.geonames.Destination;
import net.mcatlas.helpers.geonames.LocationAccuracy;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhereAmICommand implements CommandExecutor {

    private List<Player> recents = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!HelpersPlugin.get().hasStorage()) {
            // haha say it's disabled instead of just disabling it onEnable
            sender.sendMessage(ChatColor.RED + "This command is disabled.");
            return false;
        }
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (recents.contains(player)) {
            player.sendMessage(ChatColor.RED + "You've used this command too recently!");
            return false;
        }

        Location loc = player.getLocation();

        Block highestBlock = loc.getWorld().getHighestBlockAt(loc).getLocation().clone().add(0, -1, 0).getBlock();

        if (loc.getBlock().getBiome().name().contains("OCEAN") &&
                highestBlock.getBlockData().getMaterial() == Material.WATER &&
                highestBlock.getLocation().clone().add(0, -3, 0).getBlock().getBlockData().getMaterial() == Material.WATER) {
            player.sendMessage(ChatColor.RED + "Get out of the ocean before trying to type this!");
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(HelpersPlugin.get(), () -> {
            List<Destination> destinations =
                    HelpersPlugin.get().getStorage()
                            .getNearbyDestinations(loc.clone().getBlockX(), loc.clone().getBlockZ(), LocationAccuracy.VERY_HIGH);

            if (destinations.size() == 0) {
                Bukkit.getScheduler().runTask(HelpersPlugin.get(), () -> {
                    player.sendMessage(ChatColor.RED + "You're in the middle of nowhere!");
                });
                return;
            }

            Destination best = destinations.get(0);

            double lat = best.getLat();
            double lon = best.getLong();
            Coordinate coord = Coordinate.getMCFromLife(lat, lon);
            int x = (int) coord.getX();
            int z = (int) coord.getY();

            Bukkit.getScheduler().runTask(HelpersPlugin.get(), () -> {
                Location bestLocation = player.getLocation().clone();
                bestLocation.setX(x);
                bestLocation.setZ(z);
                int distance = (int) bestLocation.distance(loc);

                player.sendMessage(ChatColor.YELLOW + "You're near " + ChatColor.GREEN + best.getFormattedName() + " " + ChatColor.DARK_GRAY + "(" + distance + " blocks away");
            });
        });

        this.recents.add(player);
        Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
            this.recents.remove(player);
        }, 20 * 6);

        return false;
    }

}
