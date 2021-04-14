package net.mcatlas.helpers;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityListener implements Listener {

    private Set<UUID> recentEntityInteractions = new HashSet<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (!player.hasPermission("mcatlas.tamedowner")) {
            return;
        }

        ItemStack mainItem = player.getActiveItem();

        if (mainItem == null) {
            return;
        }

        if (mainItem.getType() == Material.NAME_TAG) {
            String name = mainItem.getItemMeta().getDisplayName();
            Location loc = player.getLocation();

            Bukkit.getLogger().info(player.getName() + " named a creature \"" + name + "\" at " +
                    loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
        }

        if (recentEntityInteractions.contains(player.getUniqueId())) {
            return;
        } else {
            recentEntityInteractions.add(player.getUniqueId());

            Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
                recentEntityInteractions.remove(player.getUniqueId());
            }, 20);
        }

        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }

        if (entity instanceof Tameable) {
            Tameable tamed = (Tameable) entity;

            if (!tamed.isTamed()) {
                return;
            }

            if (tamed.getOwner().equals(player)) {
                return;
            }

            player.sendMessage(ChatColor.GREEN + "The owner of this creature is "
                    + ChatColor.GOLD + tamed.getOwner().getName());
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() == null) {
            return;
        }

        if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        LivingEntity child = event.getEntity();

        // abortion??
        if (child == null) {
            return;
        }

        if (!(child instanceof Pig)) {
            return;
        }

        Pig pig = (Pig) child;

        if (HelpersPlugin.get().chance(3)) {
            pig.setSaddle(true);

            event.setExperience(event.getExperience() * 5);

            if (event.getBreeder() == null) {
                return;
            }

            if (event.getBreeder().getType() != EntityType.PLAYER) {
                return;
            }

            event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 30, 1));

            event.getBreeder().sendMessage(ChatColor.YELLOW + "You've created a pig who was born to fly!");
        }
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        if (!(event.getEntity() instanceof Sheep)) {
            return;
        }

        if (HelpersPlugin.get().chance(33)) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.STRING, 1));
        } else if (HelpersPlugin.get().chance(33)) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.STRING, 2));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() == null) {
            return;
        }

        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return; // there was no Player killer
        }

        if (event.getEntity() instanceof Tameable) {
            Tameable tamed = (Tameable) event.getEntity();

            if (tamed == null) {
                return;
            }

            if (tamed.getOwner() == null) {
                return;
            }

            Bukkit.getLogger().info(killer.getName() + " killed "
                    + tamed.getOwner().getName() + "'s " + tamed.getType().name());
        }

        List<ItemStack> drops = event.getDrops();

        switch (event.getEntityType()) {
            case SQUID:
                if (drops.size() == 0) return;

                // mult by 15
                drops.add(new ItemStack(Material.INK_SAC, 15 * drops.size()));
                return;
            case SPIDER:
            case SKELETON:
                if (HelpersPlugin.get().chance(50)) drops.add(new ItemStack(Material.COBWEB, 1));
                return;
            case TURTLE:
                if (HelpersPlugin.get().chance(5)) {
                    drops.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                }
                return;
            case GHAST:
                if (drops.size() == 0) return;

                // multiply by 5
                for (int i = 0; i < 5; i++) {
                    drops.addAll(drops);
                }
                return;
            case ZOMBIE:
                if (HelpersPlugin.get().chance(10)) drops.add(new ItemStack(Material.SLIME_BALL, 1));
                if (HelpersPlugin.get().chance(4)) drops.add(new ItemStack(Material.BEETROOT_SEEDS, 2));
                return;
            case ENDERMAN:
                if (HelpersPlugin.get().chance(33)) drops.add(new ItemStack(Material.PHANTOM_MEMBRANE, 1));
                return;
            case PIGLIN:
            case ZOMBIFIED_PIGLIN:
            case PIGLIN_BRUTE:
                if (HelpersPlugin.get().chance(20)) drops.add(new ItemStack(Material.BLAZE_ROD, 1));
                return;
            case WITHER:
                Bukkit.broadcastMessage(ChatColor.GREEN + killer.getName() + " successfully killed the Wither!");
                return;
            default:
                return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.CRAMMING) {
            event.setCancelled(true);
            return;
        }

        int y = entity.getLocation().getBlockY();

        if (y < 0) {
            final Location respawnLocation;

            if (player.getBedSpawnLocation() != null) {
                respawnLocation = player.getBedSpawnLocation();
            } else {
                respawnLocation = player.getLocation().getWorld().getSpawnLocation();
            }

            // teleport the player away
            player.teleport(respawnLocation);

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        HelpersPlugin.get().addPVPedPlayer((Player) event.getEntity());
        HelpersPlugin.get().addPVPedPlayer((Player) event.getDamager());
    }

}
