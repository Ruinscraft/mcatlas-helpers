package net.mcatlas.helpers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        if (block.getType() == Material.BEDROCK
                || block.getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

}
