package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class PlaySound extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            playsound(e, recipientValues.get(e));
        }
    }
    @Override
    public void executeAt(@NotNull HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            playsound(l, locations.get(l));
        }
    }
    private void playsound(Object obj, String value) {
        final boolean isPlayer = obj instanceof Player;
        final Player p = isPlayer ? (Player) obj : null;
        final Location l = isPlayer ? p.getLocation() : (Location) obj;

        final String[] a = value.split(":");
        final int count = a.length, plays = count >= 4 ? (int) evaluate(a[3]) : 1;
        final boolean world = !isPlayer || count >= 5 && Boolean.parseBoolean(a[4]);
        final World w = l.getWorld();
        final Sound s = Sound.valueOf(a[0].toUpperCase());
        final float f1 = (float) evaluate(a[1]), f2 = (float) evaluate(a[2]);
        for(int i = 1; i <= plays; i++) {
            if(world) {
                w.playSound(l, s, f1, f2);
            } else {
                p.playSound(l, s, f1, f2);
            }
        }
    }
}
