package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
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

public final class LivingConquestMob implements UVersionableSpigot {
    public static HashMap<UUID, LivingConquestMob> LIVING;

    private final LivingEntity entity;
    private final ConquestMob type;
    public LivingConquestMob(LivingEntity entity, ConquestMob type) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }

        this.entity = entity;
        this.type = type;
        entity.setCustomName(type.getName());
        final ItemStack air = new ItemStack(Material.AIR);
        final EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(air);
        equipment.setChestplate(air);
        equipment.setLeggings(air);
        equipment.setBoots(air);

        if(EIGHT) {
            equipment.setItemInHand(air);
        } else {
            equipment.setItemInMainHand(air);
            equipment.setItemInOffHand(air);
        }
        final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
        for(String string : type.getEquipment()) {
            final String originalString = string;
            string = string.toLowerCase().split("=")[0];
            switch (string) {
                case "helmet":
                    equipment.setHelmet(api.createItemStack(null, originalString.substring(7)));
                    break;
                case "chestplate":
                    equipment.setChestplate(api.createItemStack(null, originalString.substring(11)));
                    break;
                case "leggings":
                    equipment.setLeggings(api.createItemStack(null, originalString.substring(9)));
                    break;
                case "boots":
                    equipment.setBoots(api.createItemStack(null, originalString.substring(6)));
                    break;
                case "weapon":
                    equipment.setItemInHand(api.createItemStack(null, originalString.substring(7)));
                    break;
            }
        }
        for(String attribute : type.getAttributes()) {
            switch (attribute.toLowerCase().split("=")[0]) {
                case "health":
                    entity.setMaxHealth(Double.parseDouble(attribute.split("=")[1]));
                    entity.setHealth(entity.getMaxHealth());
                    break;
                case "pe":
                    final String[] b = attribute.split("=")[1].split(":");
                    entity.addPotionEffect(new PotionEffect(get_potion_effect_type(b[0]), Integer.parseInt(b[2]), Integer.parseInt(b[1]), false, false));
                    break;
            }
        }
        LIVING.put(entity.getUniqueId(), this);
    }
    public LivingEntity getEntity() {
        return entity;
    }
    public UUID getUniqueId() {
        return entity.getUniqueId();
    }
    public ConquestMob getType() {
        return type;
    }

    public void kill(EntityDeathEvent event) {
        if(event != null) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            final Random random = new Random();
            final Entity entity = event.getEntity();
            final Location location = entity.getLocation();
            final World world = entity.getWorld();
            final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
            for(String drop : type.getDrops()) {
                final int chance = drop.contains(";chance=") ? getRemainingInt(drop.split(";chance=")[1].split(";")[0]) : 100;
                if(chance == 100 || chance <= random.nextInt(100)) {
                    drop = drop.split(";chance")[0];
                    final ItemStack item = api.createItemStack(null, drop);
                    if(item != null) {
                        world.dropItem(location, item);
                    }
                }
            }
        }
        LIVING.remove(entity.getUniqueId());
    }

    public static void deleteAll() {
        LIVING = null;
    }
}
