package net.mcatlas.helpers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (player.getFlySpeed() != 0) {
            player.setFlySpeed(0);
        } else {
            player.setFlySpeed(0.99F);
        }

        player.sendMessage("Flight speed toggled");

        return true;
    }

}
