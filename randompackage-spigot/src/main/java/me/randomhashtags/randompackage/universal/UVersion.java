package me.randomhashtags.randompackage.universal;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.supported.mechanics.SpawnerAPI;
import me.randomhashtags.randompackage.util.DefaultConfiguration;
import me.randomhashtags.randompackage.util.Versionable;
import me.randomhashtags.randompackage.util.YamlUpdater;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class UVersion extends YamlUpdater implements Versionable, UVersionable, DefaultConfiguration {
    private static UVersion instance;
    public static UVersion getUVersion() {
        if(instance == null) instance = new UVersion();
        return instance;
    }

    public ItemStack item = new ItemStack(Material.APPLE);
    public ItemMeta itemMeta = item.getItemMeta();
    public List<String> lore = new ArrayList<>();

    public final void didApply(InventoryClickEvent event, Player player, ItemStack current, ItemStack cursor) {
        event.setCancelled(true);
        final int a = cursor.getAmount();
        if(a == 1) event.setCursor(new ItemStack(Material.AIR));
        else {
            cursor.setAmount(a-1);
            event.setCursor(cursor);
        }
        player.updateInventory();
    }

    public final void spawnFirework(Firework firework, Location loc) {
        if(firework != null) {
            final Firework fw = loc.getWorld().spawn(new Location(loc.getWorld(), loc.getX()+0.5, loc.getY(), loc.getZ()+0.5), Firework.class);
            fw.setFireworkMeta(firework.getFireworkMeta());
        }
    }
    public final Firework createFirework(FireworkEffect.Type explosionType, Color trailColor, Color explodeColor, int power) {
        final World w = Bukkit.getWorlds().get(0);
        final Firework fw = w.spawn(w.getSpawnLocation(), Firework.class);
        final FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwm.addEffect(FireworkEffect.builder().trail(true).flicker(true).with(explosionType).withColor(trailColor).withFade(explodeColor).withFlicker().withTrail().build());
        fw.setFireworkMeta(fwm);
        return fw;
    }

    public final String toMaterial(String input, boolean realitem) {
        if(input.contains(":")) input = input.split(":")[0];
        if(input.contains(" ")) input = input.replace(" ", "");
        if(input.contains("_")) input = input.replace("_", " ");
        String e = "";
        if(input.contains(" ")) {
            final String[] spaces = input.split(" ");
            final int l = spaces.length;
            for(int i = 0; i < l; i++) {
                e = e + spaces[i].substring(0, 1).toUpperCase() + spaces[i].substring(1).toLowerCase() + (i != l-1 ? (realitem ? "_" : " ") : "");
            }
        } else e = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        return e;
    }
    public final long getTime(String fromString) {
        long time = 0;
        if(fromString != null) {
            fromString = ChatColor.stripColor(fromString);
            final boolean hasDays = fromString.contains("d"), hasHrs = fromString.contains("h"), hasMins = fromString.contains("m"), hasSecs = fromString.contains("s");
            if(hasDays) {
                time += getRemainingDouble(fromString.split("d")[0])*24*60*60;
                if(hasHrs || hasMins || hasSecs) fromString = fromString.split("d")[1];
            }
            if(hasHrs) {
                time += getRemainingDouble(fromString.split("h")[0])*60*60;
                if(hasMins || hasSecs) fromString = fromString.split("h")[1];
            }
            if(hasMins) {
                time += getRemainingDouble(fromString.split("m")[0])*60;
                if(hasSecs) fromString = fromString.split("m")[1];
            }
            if(hasSecs) {
                time += getRemainingDouble(fromString.split("s")[0]);
                //fromString = fromString.split("s")[0];
            }
        }
        return time*1000;
    }
    public final Enchantment getEnchantment(String string) {
        if(string != null) {
            string = string.toLowerCase().replace("_", "");
            for(Enchantment enchant : Enchantment.values()) {
                final String name = enchant != null ? enchant.getName() : null;
                if(name != null && string.startsWith(name.toLowerCase().replace("_", ""))) {
                    return enchant;
                }
            }
            if(string.startsWith("po")) { return Enchantment.ARROW_DAMAGE; // Power
            } else if(string.startsWith("fl")) { return Enchantment.ARROW_FIRE; // Flame
            } else if(string.startsWith("i")) { return Enchantment.ARROW_INFINITE; // Infinity
            } else if(string.startsWith("pu")) { return Enchantment.ARROW_KNOCKBACK; // Punch
            } else if(string.startsWith("bi") && !EIGHT && !NINE && !TEN) { return Enchantment.getByName("BINDING_CURSE"); // Binding Curse
            } else if(string.startsWith("sh")) { return Enchantment.DAMAGE_ALL; // Sharpness
            } else if(string.startsWith("ba")) { return Enchantment.DAMAGE_ARTHROPODS; // Bane of Arthropods
            } else if(string.startsWith("sm")) { return Enchantment.DAMAGE_UNDEAD; // Smite
            } else if(string.startsWith("de")) { return Enchantment.DEPTH_STRIDER; // Depth Strider
            } else if(string.startsWith("e")) { return Enchantment.DIG_SPEED; // Efficiency
            } else if(string.startsWith("u")) { return Enchantment.DURABILITY; // Unbreaking
            } else if(string.startsWith("firea")) { return Enchantment.FIRE_ASPECT; // Fire Aspect
            } else if(string.startsWith("fr") && !EIGHT) { return Enchantment.getByName("FROST_WALKER"); // Frost Walker
            } else if(string.startsWith("k")) { return Enchantment.KNOCKBACK; // Knockback
            } else if(string.startsWith("fo")) { return Enchantment.LOOT_BONUS_BLOCKS; // Fortune
            } else if(string.startsWith("lo")) { return Enchantment.LOOT_BONUS_MOBS; // Looting
            } else if(string.startsWith("luc")) { return Enchantment.LUCK; // Luck
            } else if(string.startsWith("lur")) { return Enchantment.LURE; // Lure
            } else if(string.startsWith("m") && !EIGHT) { return Enchantment.getByName("MENDING"); // Mending
            } else if(string.startsWith("r")) { return Enchantment.OXYGEN; // Respiration
            } else if(string.startsWith("prot")) { return Enchantment.PROTECTION_ENVIRONMENTAL; // Protection
            } else if(string.startsWith("bl") || string.startsWith("bp")) { return Enchantment.PROTECTION_EXPLOSIONS; // Blast Protection
            } else if(string.startsWith("ff") || string.startsWith("fe")) { return Enchantment.PROTECTION_FALL; // Feather Falling
            } else if(string.startsWith("fp") || string.startsWith("firep")) { return Enchantment.PROTECTION_FIRE; // Fire Protection
            } else if(string.startsWith("pp") || string.startsWith("proj")) { return Enchantment.PROTECTION_PROJECTILE; // Projectile Protection
            } else if(string.startsWith("si")) { return Enchantment.SILK_TOUCH; // Silk Touch
            } else if(string.startsWith("th")) { return Enchantment.THORNS; // Thorns
            } else if(string.startsWith("v") && !EIGHT && !NINE && !TEN) { return Enchantment.getByName("VANISHING_CURSE"); // Vanishing Curse
            } else if(string.startsWith("aa") || string.startsWith("aq")) { return Enchantment.WATER_WORKER; // Aqua Affinity
            } else { return null; }
        }
        return null;
    }

    public final int indexOf(Set<? extends Object> collection, Object value) {
        int i = 0;
        for(Object o : collection) {
            if(value.equals(o)) return i;
            i++;
        }
        return -1;
    }

    public final void removeItem(Player player, ItemStack itemstack, int amount) {
        final PlayerInventory inv = player.getInventory();
        int nextslot = getNextSlot(player, itemstack);
        for(int i = 1; i <= amount; i++) {
            if(nextslot >= 0) {
                final ItemStack is = inv.getItem(nextslot);
                final int a = is.getAmount();
                if(a == 1) {
                    inv.setItem(nextslot, new ItemStack(Material.AIR));
                    nextslot = getNextSlot(player, itemstack);
                } else {
                    is.setAmount(a-1);
                }
            }
        }
        player.updateInventory();
    }
    private boolean isSimilar(ItemStack is, ItemStack target) {
        return isSimilar(is, target, false);
    }
    private boolean isSimilar(ItemStack is, ItemStack target, boolean matchAbsoluteMeta) {
        if(matchAbsoluteMeta) {
            return is.isSimilar(target);
        } else if(is != null && target != null && is.getType().equals(target.getType()) && is.hasItemMeta() == target.hasItemMeta()
                && (!LEGACY || is.getData().getData() == target.getData().getData())
                && is.getDurability() == target.getDurability()) {
            final ItemMeta m1 = is.getItemMeta(), m2 = target.getItemMeta();

            if(m1 == m2
                    || m1 != null && m2 != null
                    && Objects.equals(m1.getLore(), m2.getLore())
                    && Objects.equals(m1.getDisplayName(), m2.getDisplayName())
                    && Objects.equals(m1.getEnchants(), m2.getEnchants())
                    && Objects.equals(m1.getItemFlags(), m2.getItemFlags())
                    && (EIGHT || m1.isUnbreakable() == m2.isUnbreakable())) {
                return true;
            }
        }
        return false;
    }
    private int getNextSlot(Player player, ItemStack itemstack) {
        final PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            item = inv.getItem(i);
            if(item != null && isSimilar(item, itemstack)) {
                return i;
            }
        }
        return -1;
    }
    public final int getTotalAmount(Inventory inventory, UMaterial umat) {
        final ItemStack i = umat.getItemStack();
        int amount = 0;
        for(ItemStack is : inventory.getContents()) {
            if(is != null && is.isSimilar(i)) {
                amount += is.getAmount();
            }
        }
        return amount;
    }

    public final ItemStack getSpawner(String input) {
        String pi = input.toLowerCase(), type = null;
        if(pi.equals("mysterymobspawner")) {
            return givedpitem.valueOf("mysterymobspawner").clone();
        } else {
            if(RandomPackage.spawnerPlugin != null) {
                for(EntityType entitytype : EntityType.values()) {
                    final String s = entitytype.name().toLowerCase().replace("_", "");
                    if(pi.startsWith(s + "spawner")) {
                        type = s;
                    }
                }
                if(type == null) {
                    if(pi.contains("pig") && pi.contains("zombie")) type = "pigzombie";
                }
                if(type == null) return null;
                final ItemStack is = SpawnerAPI.getSpawnerAPI().getItem(type);
                if(is != null) {
                    return is;
                } else {
                    CONSOLE.sendMessage("[RandomPackage] SilkSpawners or EpicSpawners is required to use this feature!");
                }
            }
        }
        return null;
    }

    public final void playParticle(FileConfiguration config, String path, Location location, int count) {
        if(config != null && config.get(path) != null) {
            final String target = config.getString(path);
            final UParticle up = UParticle.matchParticle(target.toUpperCase());
            if(up != null) up.play(location, count);
        }
    }
    public final void playSound(FileConfiguration config, String path, Player player, Location location, boolean globalsound) {
		if(config != null && config.get(path) != null) {
		    final String[] p = config.getString(path).split(":");
			final String s = p[0].toUpperCase();
			final int v = Integer.parseInt(p[1]), pp = Integer.parseInt(p[2]);
			try {
			    final Sound sound = USound.matchSound(s);
			    if(sound != null) {
                    if(player != null) {
                        if(!globalsound) {
                            player.playSound(location, sound, v, pp);
                        } else {
                            player.getWorld().playSound(location, sound, v, pp);
                        }
                    } else {
                        location.getWorld().playSound(location, sound, v, pp);
                    }
                } else {
			        sendConsoleMessage("&6[RandomPackage] &cERROR! Invalid sound name: &f" + s + "&c! Try using the actual sound name for your Server version! (" + VERSION + ")");
                }
            } catch (Exception e) {
			    sendConsoleMessage("&6[RandomPackage] &cERROR! Invalid sound name: &f" + s + "&c! Try using the actual sound name for your Server version! (" + VERSION + ")");
            }
		}
    }

    public final List<Location> getChunkLocations(Chunk chunk) {
        final List<Location> l = new ArrayList<>();
        final int x = chunk.getX()*16, z = chunk.getZ()*16;
        final World world = chunk.getWorld();
        for(int xx = x; xx < x+16; xx++) {
            for(int zz = z; zz < z+16; zz++) {
                l.add(new Location(world, xx, 0, zz));
            }
        }
        return l;
    }
    public final ItemStack getItemInHand(LivingEntity entity) {
        if(entity == null) return null;
        else {
            final EntityEquipment e = entity.getEquipment();
            return EIGHT ? e.getItemInHand() : e.getItemInMainHand();
        }
    }
}
