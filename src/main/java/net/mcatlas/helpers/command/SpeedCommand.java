package net.mcatlas.helpers.command;

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

        if (args.length > 0) {
            float speed = 0;
            try {
                speed = Float.valueOf(args[0]);
            } catch (Exception e) {
            }
            if (speed > 1F) {
                speed = 1;
                player.sendMessage("Can't be higher than 1! Speed set to 1");
            }
            player.setFlySpeed(speed);
        }

        player.sendMessage("Flight speed toggled");

        return true;
    }

}
