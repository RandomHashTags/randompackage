package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import org.bukkit.Bukkit;
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
            isLegacy = Bukkit.getVersion().contains("1.8");
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
            a = a.toLowerCase().split("=")[0];
            switch (a.toLowerCase().split("=")[0]) {
                case "helmet":
                    e.setHelmet(api.d(null, A.substring(7)));
                    break;
                case "chestplate":
                    e.setChestplate(api.d(null, A.substring(11)));
                    break;
                case "leggings":
                    e.setLeggings(api.d(null, A.substring(9)));
                    break;
                case "boots":
                    e.setBoots(api.d(null, A.substring(6)));
                    break;
                case "weapon":
                    e.setItemInHand(api.d(null, A.substring(7)));
                    break;
            }
        }
        for(String a : type.attributes) {
            switch (a.toLowerCase().split("=")[0]) {
                case "health":
                    entity.setMaxHealth(Double.parseDouble(a.split("=")[1]));
                    entity.setHealth(entity.getMaxHealth());
                    break;
                case "pe":
                    final String[] b = a.split("=")[1].split(":");
                    entity.addPotionEffect(new PotionEffect(api.getPotionEffectType(b[0]), Integer.parseInt(b[2]), Integer.parseInt(b[1]), false, false));
                    break;
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
    }

    public static void deleteAll() {
        living = null;
        isLegacy = false;
    }
}
