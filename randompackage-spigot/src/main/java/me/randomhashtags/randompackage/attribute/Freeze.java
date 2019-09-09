package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.util.obj.TObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class Freeze extends AbstractEventAttribute {
    private static HashMap<Player, TObject> tasks;
    @Override
    public void load() {
        super.load();
        tasks = new HashMap<>();
    }
    @Override
    public void unload() {
        for(Player p : tasks.keySet()) {
            final TObject t = tasks.get(p);
            scheduler.cancelTask((int) t.first());
            p.setWalkSpeed((float) t.second());
        }
        tasks = null;
    }
    @Override
    public void execute(Event event, String value) {
        final PlayerQuitEvent q = event instanceof PlayerQuitEvent ? (PlayerQuitEvent) event : null;
        if(q != null) {
            final Player player = q.getPlayer();
            final TObject t = tasks.getOrDefault(player, null);
            if(t != null) {
                scheduler.cancelTask((int) t.first());
                player.setWalkSpeed((float) t.second());
                tasks.remove(player);
            }
        }
    }
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Player player = e instanceof Player ? (Player) e : null;
            if(player != null) {
                final float ws = player.getWalkSpeed();
                player.setWalkSpeed(0);
                final TObject t = new TObject(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                    player.setWalkSpeed(ws);
                    tasks.remove(player);
                }, (int) evaluate(recipientValues.get(e))), ws, null);
                tasks.put(player, t);
            }
        }
    }
}
