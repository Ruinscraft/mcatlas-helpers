package net.mcatlas.helpers.command;

import net.mcatlas.helpers.Coordinate;
import net.mcatlas.helpers.HelpersPlugin;
import net.mcatlas.helpers.geonames.Destination;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GotoCommand implements CommandExecutor, TabCompleter {

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
            player.sendMessage(ChatColor.RED + "/goto <place>, example: /goto New York City");
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("random")) {
            if (recents.contains(player)) {
                player.sendMessage(ChatColor.RED + "You've used this command too recently!");
                return false;
            }

            HelpersPlugin.get().getStorage().getRandomLocationFuture().thenAccept(destination -> {
                double lat = destination.getLat();
                double lon = destination.getLong();
                Coordinate coord = Coordinate.getMCFromLife(lat, lon);
                double x = (int) coord.getX() + .5;
                double z = (int) coord.getY() + .5;
                System.out.println(destination.getFormattedName() + " " + x + " " + z);

                Bukkit.getScheduler().runTask(HelpersPlugin.get(), () -> {
                    double y = player.getWorld().getHighestBlockYAt((int) (x - .5), (int) (z - .5)) + 1;
                    Location teleportTo = new Location(player.getWorld(), x, y, z);
                    if (teleportTo.getBlock().getBlockData().getMaterial() == Material.LAVA ||
                            teleportTo.getBlock().getBlockData().getMaterial() == Material.CACTUS) {
                        player.sendMessage(ChatColor.RED + "There is lava or cactus at this location! Too unsafe!");
                        return;
                    }

                    player.sendMessage(ChatColor.YELLOW + "Teleporting in 3 seconds...");

                    Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
                        if (HelpersPlugin.get().hasRecentlyPVPed(player)) {
                            player.sendMessage(ChatColor.RED + "You're in combat. Wait a little bit!");
                            return;
                        }
                        player.teleportAsync(new Location(player.getWorld(), x, y, z));
                        player.sendMessage(ChatColor.YELLOW + "You've been teleported to " +
                                ChatColor.GREEN + destination.getFormattedName());

                        String dynmapLink = HelpersPlugin.get().getDynmapLink()
                                .replace("[X]", "" + x)
                                .replace("[Z]", "" + z)
                                .replace("[ZOOM]", "" + 8);
                        TextComponent dynmapMsg = new TextComponent("[Click to view in Dynmap]");
                        dynmapMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, dynmapLink));
                        dynmapMsg.setColor(ChatColor.GRAY);
                        dynmapMsg.setUnderlined(true);
                        player.sendMessage(dynmapMsg);
                    }, 20 * 3);

                    this.recents.add(player);
                    Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
                        this.recents.remove(player);
                    }, 20 * 12); // 12 sec
                });
            });

            return false;
        }

        if (recents.contains(player)) {
            player.sendMessage(ChatColor.RED + "You've used this command too recently!");
            return false;
        }

        if (HelpersPlugin.get().hasRecentlyPVPed(player)) {
            player.sendMessage(ChatColor.RED + "You're in combat. Wait a little bit!");
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
            double x = (int) coord.getX() + .5;
            double z = (int) coord.getY() + .5;
            System.out.println(best.getFormattedName() + " " + x + " " + z);

            Bukkit.getScheduler().runTask(HelpersPlugin.get(), () -> {
                double y = player.getWorld().getHighestBlockYAt((int) (x - .5), (int) (z - .5)) + 1;
                Location teleportTo = new Location(player.getWorld(), x, y, z);
                if (teleportTo.getBlock().getBlockData().getMaterial() == Material.LAVA ||
                        teleportTo.getBlock().getBlockData().getMaterial() == Material.CACTUS) {
                    player.sendMessage(ChatColor.RED + "There is lava or cactus at this location! Too unsafe!");
                    return;
                }

                player.sendMessage(ChatColor.YELLOW + "Teleporting in 3 seconds...");

                Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
                    if (HelpersPlugin.get().hasRecentlyPVPed(player)) {
                        player.sendMessage(ChatColor.RED + "You're in combat. Wait a little bit!");
                        return;
                    }
                    player.teleportAsync(new Location(player.getWorld(), x, y, z));
                    player.sendMessage(ChatColor.YELLOW + "You've been teleported to " +
                            ChatColor.GREEN + best.getFormattedName());

                    String dynmapLink = HelpersPlugin.get().getDynmapLink()
                            .replace("[X]", "" + x)
                            .replace("[Z]", "" + z)
                            .replace("[ZOOM]", "" + 8);
                    TextComponent dynmapMsg = new TextComponent("[Click to view in Dynmap]");
                    dynmapMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, dynmapLink));
                    dynmapMsg.setColor(ChatColor.GRAY);
                    dynmapMsg.setUnderlined(true);
                    player.sendMessage(dynmapMsg);
                }, 20 * 3);
            });
        });

        this.recents.add(player);
        Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
            this.recents.remove(player);
        }, 20 * 12); // 12 sec
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // GotoTabCompletion handles this
        // if GotoTabCompletion doesn't complete, it checks here
        return new ArrayList<>();
    }

}
