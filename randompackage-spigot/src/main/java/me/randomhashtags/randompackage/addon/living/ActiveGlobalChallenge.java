package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.api.GlobalChallenges;
import me.randomhashtags.randompackage.event.GlobalChallengeEndEvent;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class ActiveGlobalChallenge extends RPStorage {
    public static HashMap<GlobalChallenge, ActiveGlobalChallenge> active;
    private static GlobalChallenges gc;
    private GlobalChallenge type;
    private HashMap<UUID, BigDecimal> participants;
    private int task;
    private long started;

    public ActiveGlobalChallenge(long started, GlobalChallenge type, HashMap<UUID, BigDecimal> participants) {
        if(active == null) {
            active = new HashMap<>();
            gc = GlobalChallenges.getChallenges();
        }
        this.started = started;
        this.type = type;
        this.participants = participants;
        long remainingTime = getRemainingTime();
        if(remainingTime < 0) remainingTime = 0;
        task = scheduler.scheduleSyncDelayedTask(getPlugin, () -> end(true, 3), remainingTime);
        active.put(type, this);
    }

    public long getStartedTime() { return started; }
    public GlobalChallenge getType() { return type; }
    public HashMap<UUID, BigDecimal> getParticipants() { return participants; }
    public void setParticipants(HashMap<UUID, BigDecimal> participants) { this.participants = participants; }

    public long getRemainingTime() { return started+type.getDuration()*1000-System.currentTimeMillis(); }
    public void increaseValue(UUID player, BigDecimal value) {
        final Map<UUID, BigDecimal> a = gc.getPlacing(participants, 1);
        final BigDecimal before = participants.getOrDefault(player, BigDecimal.ZERO), after = before.add(value);
        if(!a.isEmpty()) {
            final UUID first = (UUID) a.keySet().toArray()[0];
            if(!first.equals(player)) {
                final double v = ((BigDecimal) a.values().toArray()[0]).doubleValue();
                if(before.doubleValue() <= v && after.doubleValue() > v) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    final String time = getRemainingTime(getRemainingTime());
                    replacements.put("{TIME}", time);
                    replacements.put("{PLAYER}", Bukkit.getOfflinePlayer(player).getName());
                    replacements.put("{CHALLENGE}", type.getItem().getItemMeta().getDisplayName());
                }
            }
        }
        participants.put(player, after);
    }
    public BigDecimal getValue(UUID player) {
        return participants.getOrDefault(player, BigDecimal.ZERO);
    }
    public void setValue(UUID player, BigDecimal value) {
        participants.put(player, value);
    }

    public void end(boolean giveRewards, int recordPlacements) {
        final GlobalChallengeEndEvent e = new GlobalChallengeEndEvent(this, giveRewards);
        pluginmanager.callEvent(e);
        gc.reloadInventory();
        final Map<UUID, BigDecimal> placements = gc.getPlacing(participants);
        if(task != -1) scheduler.cancelTask(task);
        active.remove(type);
        if(giveRewards) {
            int i = 1;
            for(UUID p : placements.keySet()) {
                final RPPlayer pdata = RPPlayer.get(p);
                if(i <= recordPlacements) {
                    final GlobalChallengePrize prize = valueOfGlobalChallengePrize(i);
                    pdata.addGlobalChallengePrize(prize);
                    i += 1;
                }
            }
        }
    }

    public static ActiveGlobalChallenge valueOf(ItemStack display) {
        if(active !=  null && display != null && display.hasItemMeta() && display.getItemMeta().hasDisplayName()) {
            final String d = display.getItemMeta().getDisplayName();
            for(ActiveGlobalChallenge g : active.values()) {
                if(g.getType().getItem().getItemMeta().getDisplayName().equals(d)) {
                    return g;
                }
            }
        }
        return null;
    }

    public static void deleteAll() {
        active = null;
        globalchallenges = null;
    }
}
