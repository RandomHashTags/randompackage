package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.util.obj.TObject;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public final class StopEnchant extends AbstractEventAttribute {
    private static HashMap<UUID, HashMap<CustomEnchantSpigot, TObject>> STOPPED_ENCHANTS;

    @Override
    public void load() {
        super.load();
        STOPPED_ENCHANTS = new HashMap<>();
    }
    @Override
    public void unload() {
        for(UUID u : STOPPED_ENCHANTS.keySet()) {
            stopTasks(u);
        }
        STOPPED_ENCHANTS = null;
    }

    @Override
    public void execute(PendingEventAttribute pending) {
        final Event event = pending.getEvent();
        if(event instanceof CustomEnchantProcEvent) {
            final CustomEnchantProcEvent c = (CustomEnchantProcEvent) event;
            final UUID u = c.getEntities().get("Player").getUniqueId();
            if(STOPPED_ENCHANTS.containsKey(u)) {
                final CustomEnchantSpigot e = c.getEnchant();
                final HashMap<CustomEnchantSpigot, TObject> t = STOPPED_ENCHANTS.get(u);
                if(t.containsKey(e)) {
                    final TObject o = t.get(e);
                    if(c.getEnchantLevel() <= (int) o.getFirst()) {
                        c.setCancelled(true);
                    }
                }
            }
        } else {
            final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
            for(Entity e : recipientValues.keySet()) {
                final CustomEnchantSpigot enchant = valueOfCustomEnchant(recipientValues.get(e));
                if(enchant != null && enchant.isEnabled()) {
                    final String[] v = recipientValues.get(e).split(":");
                    final int level = (int) evaluate(v[0].replace("max", Integer.toString(enchant.getMaxLevel())));
                    final UUID u = e.getUniqueId();
                    if(!STOPPED_ENCHANTS.containsKey(u)) STOPPED_ENCHANTS.put(u, new HashMap<>());
                    final HashMap<CustomEnchantSpigot, TObject> a = STOPPED_ENCHANTS.get(u);
                    final int task = resumeEnchant(u, enchant, (int) evaluate(v[1]));
                    a.put(enchant, new TObject(level, task, null));
                }
            }
        }
    }
    private void stopTasks(UUID uuid) {
        for(TObject t : STOPPED_ENCHANTS.get(uuid).values()) {
            SCHEDULER.cancelTask((int) t.getSecond());
        }
    }
    private int resumeEnchant(UUID player, CustomEnchantSpigot enchant, int ticks) {
        return SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
            STOPPED_ENCHANTS.get(player).remove(enchant);
        }, ticks);
    }
}
