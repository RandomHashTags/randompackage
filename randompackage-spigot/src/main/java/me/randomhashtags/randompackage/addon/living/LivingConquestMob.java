package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.universal.UVersionable;
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

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public class LivingConquestMob implements UVersionable {
    public static HashMap<UUID, LivingConquestMob> living;

    private LivingEntity entity;
    private ConquestMob type;
    public LivingConquestMob(LivingEntity entity, ConquestMob type) {
        if(living == null) {
            living = new HashMap<>();
        }

        this.entity = entity;
        this.type = type;
        entity.setCustomName(type.getName());
        final ItemStack air = new ItemStack(Material.AIR);
        final EntityEquipment e = entity.getEquipment();
        e.setHelmet(air);
        e.setChestplate(air);
        e.setLeggings(air);
        e.setBoots(air);

        if(EIGHT) {
            e.setItemInHand(air);
        } else {
            e.setItemInMainHand(air);
            e.setItemInOffHand(air);
        }
        for(String a : type.getEquipment()) {
            final String A = a;
            a = a.toLowerCase().split("=")[0];
            switch (a) {
                case "helmet":
                    e.setHelmet(API.createItemStack(null, A.substring(7)));
                    break;
                case "chestplate":
                    e.setChestplate(API.createItemStack(null, A.substring(11)));
                    break;
                case "leggings":
                    e.setLeggings(API.createItemStack(null, A.substring(9)));
                    break;
                case "boots":
                    e.setBoots(API.createItemStack(null, A.substring(6)));
                    break;
                case "weapon":
                    e.setItemInHand(API.createItemStack(null, A.substring(7)));
                    break;
            }
        }
        for(String a : type.getAttributes()) {
            switch (a.toLowerCase().split("=")[0]) {
                case "health":
                    entity.setMaxHealth(Double.parseDouble(a.split("=")[1]));
                    entity.setHealth(entity.getMaxHealth());
                    break;
                case "pe":
                    final String[] b = a.split("=")[1].split(":");
                    entity.addPotionEffect(new PotionEffect(getPotionEffectType(b[0]), Integer.parseInt(b[2]), Integer.parseInt(b[1]), false, false));
                    break;
            }
        }
        living.put(entity.getUniqueId(), this);
    }
    public LivingEntity getEntity() { return entity; }
    public UUID getUniqueId() { return entity.getUniqueId(); }
    public ConquestMob getType() { return type; }

    public void kill(EntityDeathEvent event) {
        if(event != null) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            final Random r = new Random();
            final Entity e = event.getEntity();
            final Location l = e.getLocation();
            final World w = e.getWorld();
            for(String s : type.getDrops()) {
                final int chance = s.contains(";chance=") ? getRemainingInt(s.split(";chance=")[1].split(";")[0]) : 100;
                if(chance == 100 || chance <= r.nextInt(100)) {
                    s = s.split(";chance")[0];
                    final ItemStack i = API.createItemStack(null, s);
                    if(i != null) {
                        w.dropItem(l, i);
                    }
                }
            }
        }
        living.remove(entity.getUniqueId());
    }

    public static void deleteAll() {
        living = null;
    }
}
