package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.SPAWNER_CHANCE;

public interface CustomExplosion extends Attributable, Itemable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "customexplosion" };
    }
    @Nullable
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final CustomExplosion explosion = getCustomExplosion(originalInput.split(":")[1]);
        return explosion != null ? explosion.getItem() : null;
    }

    void didExplode(UUID uuid, List<Block> blockList);

    default void explode(@NotNull EntityExplodeEvent event, @NotNull Location location) {
        event.setCancelled(true);
        final UUID uuid = event.getEntity().getUniqueId();
        final List<Block> blockList = getBlockList(event);
        didExplode(uuid, blockList);
        for(String string : getAttributes()) {
            if(!string.contains("&&") && !string.contains("||")) {
                doExplosion(string, location, event);
            } else if(string.contains("&&") && !string.contains("||")) {
                for(String s : string.split("&&")) {
                    doExplosion(s, location, event);
                }
            } else if(string.contains("&&") && string.contains("||")) {
                for(String s : RANDOM.nextInt(100) < 50 ? string.split("\\|\\|")[0].split("&&") : string.split("\\|\\|")[1].split("&&")) {
                    doExplosion(s, location, event);
                }
            }
        }
    }
    @NotNull
    default List<Block> getBlockList(@NotNull EntityExplodeEvent event) {
        for(String string : getAttributes()) {
            if(!string.contains("&&") && !string.contains("||") && string.toLowerCase().startsWith("affects_only;")) {
                return getAffectedBlocks(event, string);
            } else if(string.contains("&&") && !string.contains("||")) {
                for(String s : string.split("&&")) {
                    if(s.toLowerCase().startsWith("affects_only;")) {
                        return getAffectedBlocks(event, s);
                    }
                }
            } else if(string.contains("&&") && string.contains("||")) {
                for(String s : RANDOM.nextInt(100) < 50 ? string.split("\\|\\|")[0].split("&&") : string.split("\\|\\|")[1].split("&&")) {
                    if(s.toLowerCase().startsWith("affects_only;")) {
                        return getAffectedBlocks(event, s);
                    }
                }
            }
        }
        return event.blockList();
    }
    @NotNull
    default List<Block> getAffectedBlocks(@NotNull EntityExplodeEvent event, @NotNull String input) {
        final Material material = UMaterial.match(input.split(";")[1].toUpperCase()).getMaterial();
        final List<Block> bl = event.blockList();
        for(int i = 0; i < bl.size(); i++) {
            if(!bl.get(i).getType().equals(material)) {
                bl.remove(i);
                i--;
            }
        }
        return bl;
    }
    // Only meant for this class usage
    default void doExplosion(String input, Location loc, EntityExplodeEvent event) {
        final World w = loc.getWorld();
        input = input.replace(" ", "").toLowerCase();
        if(input.startsWith("affects_only;")) {
            getAffectedBlocks(event, input);
        } else if(input.startsWith("instant_explode;")) {
            double x = loc.getX(), y = loc.getY(), z = loc.getZ();
            String d = input.split(";")[1].toLowerCase();
            if(d.contains("-")) {
                if(d.contains("x"))      x = x - Integer.parseInt(d.split("-")[1]);
                else if(d.contains("y")) y = y - Integer.parseInt(d.split("-")[1]);
                else if(d.contains("z")) z = z - Integer.parseInt(d.split("-")[1]);
            } else if(d.contains("+")) {
                if(d.contains("x"))      x = x + Integer.parseInt(d.split("\\+")[1]);
                else if(d.contains("y")) y = y + Integer.parseInt(d.split("\\+")[1]);
                else if(d.contains("z")) z = z + Integer.parseInt(d.split("\\+")[1]);
            }
            for(int i = 1; i <= Integer.parseInt(input.split(";")[2]); i++) {
                final TNTPrimed a = w.spawn(new Location(w, x, y, z), TNTPrimed.class);
                a.setFuseTicks(0);
            }
        } else if(input.startsWith("repeat_explode;")) {
            final double x = loc.getX(), y = loc.getY(), z = loc.getZ();
            final int delay = Integer.parseInt(input.split(";")[1]);

            for(int i = 1; i <= Integer.parseInt(input.split(";")[2]); i++) {
                SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                    final TNTPrimed a = w.spawn(new Location(w, x, y, z), TNTPrimed.class);
                    a.setFuseTicks(0);
                }, 20 * delay + (delay * 20 * (i - 1)));
            }
        } else if(input.startsWith("increase_spawner_drop;")) {
            final Plugin spawner = RandomPackage.SPAWNER_PLUGIN;
            final Material m = UMaterial.SPAWNER.getMaterial();
            final double chance = SPAWNER_CHANCE * Double.parseDouble(input.split(";")[1]);
            for(Block block : event.blockList()) {
                boolean mobspawner = false;
                if(block.getType().equals(m) && chance < RANDOM.nextInt(100)) {
                    mobspawner = true;
                    if(spawner != null) {
                        w.dropItemNaturally(block.getLocation(), getSpawner(((CreatureSpawner) block).getSpawnedType().name()));
                    }
                    block.setType(Material.AIR);
                }
                if(!mobspawner) {
                    block.breakNaturally();
                }
            }
        }
    }
}
