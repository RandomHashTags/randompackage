package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.addons.objects.ConquestMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class LivingConquestMob {
    public static HashMap<UUID, LivingConquestMob> living;
    private static boolean isLegacy;

    public LivingEntity entity;
    public ConquestMob type;
    public LivingConquestMob(LivingEntity entity, ConquestMob type) {
        if(living == null) {
            living = new HashMap<>();
            isLegacy = api.version.contains("1.8");
        }

        this.entity = entity;
        this.type = type;
        entity.setCustomName(type.name);
        final ItemStack air = new ItemStack(Material.AIR);
        final EntityEquipment e = entity.getEquipment();
        e.setHelmet(air);
        e.setChestplate(air);
        e.setLeggings(air);
        e.setBoots(air);

        if(isLegacy) {
            e.setItemInHand(air);
        } else {
            e.setItemInMainHand(air);
            e.setItemInOffHand(air);
        }
        for(String a : type.equipment) {
            final String A = a;
            a = a.toLowerCase();
            if(a.startsWith("helmet=")) {
                e.setHelmet(api.d(null, A.substring(7)));
            } else if(a.startsWith("chestplate=")) {
                e.setChestplate(api.d(null, A.substring(11)));
            } else if(a.startsWith("leggings=")) {
                e.setLeggings(api.d(null, A.substring(9)));
            } else if(a.startsWith("boots=")) {
                e.setBoots(api.d(null, A.substring(6)));
            } else if(a.startsWith("weapon=")) {
                e.setItemInHand(api.d(null, A.substring(7)));
            }
        }
        for(String a : type.attributes) {
            a = a.toLowerCase();
            if(a.startsWith("health=")) {
                entity.setMaxHealth(Double.parseDouble(a.split("=")[1]));
                entity.setHealth(entity.getMaxHealth());
            } else if(a.startsWith("pe=")) {
                final String[] b = a.split("=")[1].split(":");
                entity.addPotionEffect(new PotionEffect(api.getPotionEffectType(b[0]), Integer.parseInt(b[2]), Integer.parseInt(b[1]), false, false));
            }
        }
        living.put(entity.getUniqueId(), this);
    }
    public void kill(EntityDeathEvent event) {
        if(event != null) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            final Random r = new Random();
            final Entity e = event.getEntity();
            final Location l = e.getLocation();
            final World w = e.getWorld();
            for(String s : type.drops) {
                final int chance = s.contains(";chance=") ? api.getRemainingInt(s.split(";chance=")[1].split(";")[0]) : 100;
                if(chance == 100 || chance <= r.nextInt(100)) {
                    s = s.split(";chance")[0];
                    final ItemStack i = api.d(null, s);
                    if(i != null) w.dropItem(l, i);
                }
            }
        }
        living.remove(entity.getUniqueId());
        entity = null;
        type = null;
    }

    public static void deleteAll() {
        living = null;
        isLegacy = false;
    }
}
