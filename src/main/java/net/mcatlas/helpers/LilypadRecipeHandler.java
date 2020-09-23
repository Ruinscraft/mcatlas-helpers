package net.mcatlas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

public class LilypadRecipeHandler implements Runnable {

    private HelpersPlugin plugin;

    public LilypadRecipeHandler(HelpersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.DROPPED_ITEM) {
                    continue;
                }

                Item vine = (Item) entity;
                ItemStack stack = vine.getItemStack();

                if (stack.getAmount() != 64 || stack.getType() != Material.VINE) {
                    continue;
                }

                Location location = vine.getLocation();
                Collection<? extends Item> nearbyItems = location.getNearbyEntitiesByType(vine.getClass(), 3);
                for (Item sugarCane : nearbyItems) {
                    if (sugarCane.getType() != EntityType.DROPPED_ITEM) {
                        continue;
                    }
                    ItemStack sugarCaneItemStack = sugarCane.getItemStack();

                    if (sugarCaneItemStack.getAmount() != 64 || sugarCaneItemStack.getType() != Material.SUGAR_CANE) {
                        continue;
                    }

                    UUID vineUUID = vine.getUniqueId();
                    UUID sugarCaneUUID = sugarCane.getUniqueId();

                    int delay = plugin.getRandom().nextInt(100);
                    int randAmntOfLillies = plugin.getRandom().nextInt(2) + 1;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Entity vineEntity = Bukkit.getEntity(vineUUID);
                        Entity sugarCaneEntity = Bukkit.getEntity(sugarCaneUUID);
                        if (vineEntity == null || sugarCaneEntity == null) return;

                        Location itemsSpot = vineEntity.getLocation();
                        vineEntity.remove();
                        sugarCaneEntity.remove();

                        world.dropItem(itemsSpot, new ItemStack(Material.LILY_PAD, randAmntOfLillies));
                    }, 20 + delay);
                }
            }
        }
    }

}
