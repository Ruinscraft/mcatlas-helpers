package net.mcatlas.helpers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock() == null) return;
        if (event.getBlock().getBlockData().getMaterial() != Material.GRAVEL) return;

        if (event.isDropItems()) {
            Block block = event.getBlock();
            if (!block.getBiome().name().contains("OCEAN")) return;
            if (block.getY() > 52) return;
            Block aboveBlock = block.getWorld().getBlockAt(block.getLocation().clone().add(0, 1, 0));
            if (aboveBlock == null) return;
            if (aboveBlock.getBlockData() instanceof Waterlogged ||
                    aboveBlock.getBlockData().getMaterial() == Material.WATER) {
                if (HelpersPlugin.get().chance(50)) {
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.PRISMARINE_SHARD, 1));
                }
                if (HelpersPlugin.get().chance(50)) {
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.PRISMARINE_CRYSTALS, 1));
                }
                if (HelpersPlugin.get().chance(15)) {
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.PRISMARINE_SHARD, 3));
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.PRISMARINE_CRYSTALS, 2));
                }
            }
        }
    }

    /*
     *  Bedrock/Barrier checks
     */
    @EventHandler
    public void onBedrockBarrierPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        if (block.getType() == Material.BEDROCK
                || block.getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedrockBarrierBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        if (block.getType() == Material.BEDROCK
                || block.getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopperMoveEvent(InventoryMoveItemEvent event) {
        if (HelpersPlugin.get().getAverageTPS() > 16) {
            return;
        }
        Location loc = event.getDestination().getLocation();
        String formattedX = "" + (int) (loc.getBlockX() / 64) * 64;
        String formattedZ = "" + (int) (loc.getBlockZ() / 64) * 64;
        String formattedLoc = formattedX + ", " + formattedZ;
        HelpersPlugin.get().logHopperAction(formattedLoc);
    }

    @EventHandler
    public void onHopperPickupEvent(InventoryPickupItemEvent event) {
        if (HelpersPlugin.get().getAverageTPS() > 16) {
            return;
        }
        Location loc = event.getInventory().getLocation();
        String formattedX = "" + (int) (loc.getBlockX() / 64) * 64;
        String formattedZ = "" + (int) (loc.getBlockZ() / 64) * 64;
        String formattedLoc = formattedX + ", " + formattedZ;
        HelpersPlugin.get().logHopperAction(formattedLoc);
    }

}
