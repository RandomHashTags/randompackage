package me.randomhashtags.randompackage.utils.classes.customexplosions;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;
import static me.randomhashtags.randompackage.RandomPackageAPI.spawnerchance;

public class CustomTNT {
    public static HashMap<String, CustomTNT> tnt;
    public static HashMap<Location, CustomTNT> placed;
    public static HashMap<UUID, CustomTNT> primed;
    private static Random random;

    public YamlConfiguration yml;
    public String ymlName;
    private ItemStack item;
    public List<String> attributes;
    public CustomTNT(File f) {
        if(tnt == null) {
            tnt = new HashMap<>();
            placed = new HashMap<>();
            primed = new HashMap<>();
            random = new Random();
        }
        this.yml = YamlConfiguration.loadConfiguration(f);
        this.ymlName = f.getName().split("\\.yml")[0];
        this.item = api.d(yml, "item");
        this.attributes = yml.getStringList("attributes");
        tnt.put(ymlName, this);
    }
    public ItemStack getItem() { return item.clone(); }

    public void place(Location l) {
        final Block b = l.getWorld().getBlockAt(l);
        b.setType(Material.TNT);
        b.getState().update();
        placed.put(l, this);
    }
    public TNTPrimed spawn(Location l) {
        final TNTPrimed t = l.getWorld().spawn(l, TNTPrimed.class);
        primed.put(t.getUniqueId(), this);
        return t;
    }
    public TNTPrimed ignite(Location l) {
        placed.remove(l);
        return spawn(l);
    }
    // https://minecraft.gamepedia.com/TNT
    public void explode(EntityExplodeEvent event, Location l) {
        final UUID u = event.getEntity().getUniqueId();
        primed.remove(u);
        final List<Block> bl = getBlockList(event);
        for(Block b : bl) {
            final Location lo = b.getLocation();
            if(placed.containsKey(lo)) {
                lo.getWorld().getBlockAt(lo).setType(Material.AIR);
                placed.get(lo).ignite(lo).setFuseTicks(10+random.nextInt(21));
            }
        }
        for(String string : attributes) {
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
        for(String string : attributes) {
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

    public static CustomTNT valueOf(ItemStack is) {
        if(tnt != null) {
            for(CustomTNT t : tnt.values()) {
                if(t.getItem().isSimilar(is)) {
                    return t;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        tnt = null;
        placed = null;
        primed = null;
        random = null;
    }
}
