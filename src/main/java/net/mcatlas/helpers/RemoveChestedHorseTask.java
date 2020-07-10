package net.mcatlas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;

import java.util.logging.Logger;

public class RemoveChestedHorseTask implements Runnable {

    private Logger logger;

    public RemoveChestedHorseTask(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getLivingEntities()) {
                if (entity instanceof ChestedHorse) {
                    logger.info("Removed chested horse at " +
                            entity.getLocation().getBlockX() + " " + entity.getLocation().getBlockY() +
                            " " + entity.getLocation().getBlockZ());
                    entity.remove();
                }
            }
        }
    }

}
