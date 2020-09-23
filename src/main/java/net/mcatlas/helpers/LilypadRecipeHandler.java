package net.mcatlas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
            for (Entity entity : world.getEntitiesByClass(Item.class)) {
                Item vine = (Item) entity;
                ItemStack stack = vine.getItemStack();

                // stack of vines
                if (stack.getAmount() != 64 || stack.getType() != Material.VINE) {
                    continue;
                }

                // has to be in water!
                Location location = vine.getLocation();
                if (location.getBlock().getType() != Material.WATER ||
                        location.add(0, -1, 0).getBlock().getType() != Material.WATER) {
                    continue;
                }

                Collection<? extends Item> nearbyItems = location.getNearbyEntitiesByType(Item.class, 2);
                for (Item sugarCane : nearbyItems) {
                    ItemStack sugarCaneItemStack = sugarCane.getItemStack();

                    // stack of sugar cane
                    if (sugarCaneItemStack.getAmount() != 64 || sugarCaneItemStack.getType() != Material.SUGAR_CANE) {
                        continue;
                    }

                    UUID vineUUID = vine.getUniqueId();
                    UUID sugarCaneUUID = sugarCane.getUniqueId();

                    // extra delay before growth
                    int delay = plugin.getRandom().nextInt(120);
                    // spawn 1 or 2
                    int randAmntOfLillies = plugin.getRandom().nextInt(2) + 1;

                    // run task later to check if the entities are there
                    // if entities are there, remove them, and spawn the lilypad
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
