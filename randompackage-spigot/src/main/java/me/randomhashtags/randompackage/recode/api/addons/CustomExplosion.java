package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.recode.RPAddon;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.spawnerchance;

public abstract class CustomExplosion extends RPAddon {

    public abstract ItemStack getItem();
    public abstract List<String> getAttributes();
    public abstract void didExplode(UUID uuid, List<Block> blockList);

    // https://minecraft.gamepedia.com/TNT
    public void explode(EntityExplodeEvent event, Location l) {
        final UUID u = event.getEntity().getUniqueId();
        final List<Block> bl = getBlockList(event);
        didExplode(u, bl);
        for(String string : getAttributes()) {
            if(!string.contains("&&") && !string.contains("||")) {
                doExplosion(string, l, event);
            } else if(string.contains("&&") && !string.contains("||")) {
                for(String s : string.split("&&")) {
                    doExplosion(s, l, event);
                }
            } else if(string.contains("&&") && string.contains("||")) {
                for(String s : random.nextInt(100) < 50 ? string.split("\\|\\|")[0].split("&&") : string.split("\\|\\|")[1].split("&&")) {
                    doExplosion(s, l, event);
                }
            }
        }
    }
    private List<Block> getBlockList(EntityExplodeEvent event) {
        for(String string : getAttributes()) {
            if(!string.contains("&&") && !string.contains("||") && string.toLowerCase().startsWith("affects_only;")) {
                return getAffectedBlocks(event, string);
            } else if(string.contains("&&") && !string.contains("||")) {
                for(String s : string.split("&&")) {
                    if(s.toLowerCase().startsWith("affects_only;"))
                        return getAffectedBlocks(event, s);
                }
            } else if(string.contains("&&") && string.contains("||")) {
                for(String s : random.nextInt(100) < 50 ? string.split("\\|\\|")[0].split("&&") : string.split("\\|\\|")[1].split("&&"))
                    if(s.toLowerCase().startsWith("affects_only;"))
                        return getAffectedBlocks(event, s);
            }
        }
        return event.blockList();
    }
    private List<Block> getAffectedBlocks(EntityExplodeEvent event, String input) {
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
    private void doExplosion(String input, Location loc, EntityExplodeEvent event) {
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
            final BukkitScheduler s = api.scheduler;
            final Plugin rp = api.randompackage;

            for(int i = 1; i <= Integer.parseInt(input.split(";")[2]); i++) {
                s.scheduleSyncDelayedTask(rp, () -> {
                    final TNTPrimed a = w.spawn(new Location(w, x, y, z), TNTPrimed.class);
                    a.setFuseTicks(0);
                }, 20 * delay + (delay * 20 * (i - 1)));
            }
        } else if(input.startsWith("increase_spawner_drop;")) {
            final UVersion uv = UVersion.getUVersion();
            final Random r = random;
            final int sc = spawnerchance;
            final Plugin spawner = RandomPackage.spawnerPlugin;
            final Material m = UMaterial.SPAWNER.getMaterial();
            final double d = sc * Double.parseDouble(input.split(";")[1]);
            for(Block block : event.blockList()) {
                boolean mobspawner = false;
                if(block.getType().equals(m) && d <= r.nextInt(100)) {
                    mobspawner = true;
                    if(spawner != null) {
                        w.dropItemNaturally(block.getLocation(), uv.getSpawner(((CreatureSpawner) block).getSpawnedType().name()));
                    }
                    block.setType(Material.AIR);
                }
                if(!mobspawner) block.breakNaturally();
            }
        }
    }
    public static CustomExplosion valueOf(ItemStack is) {
        if(explosions != null) {
            for(CustomExplosion c : explosions.values()) {
                if(c.getItem().isSimilar(is)) {
                    return c;
                }
            }
        }
        return null;
    }
}
