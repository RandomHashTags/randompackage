package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.api.events.MobStackDepleteEvent;
import me.randomhashtags.randompackage.api.events.MobStackMergeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StackedEntity {
    public static final List<StackedEntity> stackedEntities = new ArrayList<>();
    private final PluginManager pm = Bukkit.getPluginManager();

    public final long creationTime;
    public final LivingEntity entity;
    public final UUID uuid;
    public final String customname;
    public int size = 1;
    public StackedEntity(long creationTime, LivingEntity entity, String customname, int size) {
        this.creationTime = creationTime;
        this.entity = entity;
        this.uuid = entity.getUniqueId();
        this.customname = ChatColor.translateAlternateColorCodes('&', customname);
        this.size = size;
        entity.setCustomName(customname.replace("{SS}", Integer.toString(size)));
        stackedEntities.add(this);
    }
    public void setSize(int size) {
        final MobStackMergeEvent e = new MobStackMergeEvent(this, size);
        pm.callEvent(e);
        if(!e.isCancelled()) {
            this.size = size;
            entity.setCustomName(customname.replace("{SS}", Integer.toString(size)));
        }
    }
    public void kill(LivingEntity killer, int amount) {
        final MobStackDepleteEvent e = new MobStackDepleteEvent(this, killer, amount);
        pm.callEvent(e);
        if(!e.isCancelled()) {
            amount = e.amount;
            final World w = entity.getWorld();
            final Location lo = entity.getLocation();
            final EntityType t = entity.getType();
            if(size-amount <= 0) {
                stackedEntities.remove(this);
                entity.setHealth(0.0001);
                entity.damage(entity.getMaxHealth(), killer);
            } else {
                size -= amount;
            }
            if(stackedEntities.contains(this)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(RandomPackage.getPlugin, () -> entity.setNoDamageTicks(0), 0);
                entity.setCustomName(customname.replace("{SS}", Integer.toString(size)));
                for(int i = 1; i <= amount; i++) {
                    final LivingEntity d = (LivingEntity) w.spawnEntity(lo, t);
                    final EntityEquipment ee = d.getEquipment();
                    ee.setHelmet(null);
                    ee.setChestplate(null);
                    ee.setLeggings(null);
                    ee.setBoots(null);
                    d.setCustomNameVisible(false);
                    d.setCustomName("");
                    d.setHealth(0.0001);
                    d.damage(d.getMaxHealth(), killer);
                }
            }
        }
    }
    public void merge(LivingEntity target) {
        final StackedEntity se = valueOf(target.getUniqueId());
        final MobStackMergeEvent e = new MobStackMergeEvent(this, se != null ? size+se.size : size+1);
        pm.callEvent(e);
        if(!e.isCancelled()) {
            size += se != null ? se.size : 1;
            if(se != null && creationTime > se.creationTime) {
                remove(false, null);
                se.size = size;
                se.entity.setCustomName(customname.replace("{SS}", Integer.toString(size)));
            } else {
                entity.setCustomName(customname.replace("{SS}", Integer.toString(size)));
                target.remove();
                if(se != null) se.remove(false, null);
            }

        }
    }
    public void remove(boolean dropLoot, LivingEntity killer) {
        if(dropLoot) kill(killer, size);
        else entity.remove();
        stackedEntities.remove(this);
    }

    public static StackedEntity valueOf(UUID uuid) {
        for(StackedEntity s : stackedEntities)
            if(s.uuid.equals(uuid))
                return s;
        return null;
    }
    public static List<StackedEntity> valueOf(EntityType type) {
        final List<StackedEntity> list = new ArrayList<>();
        for(StackedEntity s : stackedEntities)
            if(s.entity.getType().equals(type))
                list.add(s);
        return list;
    }

}
