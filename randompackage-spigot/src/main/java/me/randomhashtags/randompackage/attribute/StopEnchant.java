package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.event.customenchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.util.obj.TObject;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public class StopEnchant extends AbstractEventAttribute {
    private static HashMap<UUID, HashMap<CustomEnchant, TObject>> stoppedEnchants;
    @Override
    public void load() {
        super.load();
        stoppedEnchants = new HashMap<>();
    }
    @Override
    public void unload() {
        for(UUID u : stoppedEnchants.keySet()) {
            stopTasks(u);
        }
        stoppedEnchants = null;
    }
    @Override
    public void execute(Event event) {
        if(event instanceof CustomEnchantProcEvent) {
            final CustomEnchantProcEvent c = (CustomEnchantProcEvent) event;
            final UUID u = c.player.getUniqueId();
            if(stoppedEnchants.containsKey(u)) {
                final CustomEnchant e = c.getEnchant();
                final HashMap<CustomEnchant, TObject> t = stoppedEnchants.get(u);
                if(t.containsKey(e)) {
                    final TObject o = t.get(e);
                    if(c.getEnchantLevel() <= (int) o.first()) {
                        c.setCancelled(true);
                    }
                }
            }
        }
    }
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final CustomEnchant enchant = valueOfCustomEnchant(recipientValues.get(e));
            if(enchant != null && enchant.isEnabled()) {
                final String[] v = recipientValues.get(e).split(":");
                final int level = (int) evaluate(v[0].replace("max", Integer.toString(enchant.getMaxLevel())));
                final UUID u = e.getUniqueId();
                if(!stoppedEnchants.containsKey(u)) stoppedEnchants.put(u, new HashMap<>());
                final HashMap<CustomEnchant, TObject> a = stoppedEnchants.get(u);
                final int task = resumeEnchant(u, enchant, (int) evaluate(v[1]));
                a.put(enchant, new TObject(level, task, null));
            }
        }
    }
    private void stopTasks(UUID uuid) {
        for(TObject t : stoppedEnchants.get(uuid).values()) {
            scheduler.cancelTask((int) t.second());
        }
    }
    private int resumeEnchant(UUID player, CustomEnchant enchant, int ticks) {
        return scheduler.scheduleSyncDelayedTask(randompackage, () -> {
            stoppedEnchants.get(player).remove(enchant);
        }, ticks);
    }
}
