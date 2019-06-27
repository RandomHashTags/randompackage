package me.randomhashtags.randompackage.utils.classes.globalchallenges;

import me.randomhashtags.randompackage.api.GlobalChallenges;
import me.randomhashtags.randompackage.api.events.globalchallenges.GlobalChallengeEndEvent;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class ActiveGlobalChallenge {
    public static HashMap<GlobalChallenge, ActiveGlobalChallenge> active;
    private static PluginManager pm;
    private static GlobalChallenges globalchallenges;
    private GlobalChallenge type;
    private HashMap<UUID, Double> participants;
    private int task;
    private long started;

    public ActiveGlobalChallenge(long started, GlobalChallenge type, HashMap<UUID, Double> participants) {
        if(active == null) {
            active = new HashMap<>();
            globalchallenges = GlobalChallenges.getChallenges();
            pm = globalchallenges.pluginmanager;
        }
        this.started = started;
        this.type = type;
        this.participants = participants;
        long remainingTime = getRemainingTime();
        if(remainingTime < 0) remainingTime = 0;
        task = Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin, () -> end(true, 3), remainingTime);
        active.put(type, this);
    }

    public long getStartedTime() { return started; }
    public GlobalChallenge getType() { return type; }
    public HashMap<UUID, Double> getParticipants() { return participants; }

    public long getRemainingTime() {
        return started+(type.getDuration()*1000)-System.currentTimeMillis();
    }
    public void increaseValue(UUID player, double value) {
        final Map<UUID, Double> a = globalchallenges.getPlacing(participants, 1);
        final double before = participants.getOrDefault(player, 0.00), after = before+value;
        if(!a.isEmpty()) {
            final UUID first = (UUID) a.keySet().toArray()[0];
            if(!first.equals(player)) {
                final double v = (Double) a.values().toArray()[0];
                if(before <= v && after > v) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    final String time = globalchallenges.getRemainingTime(getRemainingTime());
                    replacements.put("{TIME}", time);
                    replacements.put("{PLAYER}", Bukkit.getOfflinePlayer(player).getName());
                    replacements.put("{CHALLENGE}", type.getDisplayItem().getItemMeta().getDisplayName());
                }
            }
        }
        participants.put(player, after);
    }
    public double getValue(UUID player) {
        return participants.getOrDefault(player, 0.00);
    }
    public void setValue(UUID player, double value) {
        participants.put(player, value);
    }

    public void end(boolean giveRewards, int recordPlacements) {
        final GlobalChallengeEndEvent e = new GlobalChallengeEndEvent(this, giveRewards);
        pm.callEvent(e);
        globalchallenges.reloadInventory();
        final Map<UUID, Double> placements = globalchallenges.getPlacing(participants);
        if(task != -1) Bukkit.getScheduler().cancelTask(task);
        active.remove(type);
        if(giveRewards) {
            int i = 1;
            for(UUID p : placements.keySet()) {
                final RPPlayer pdata = RPPlayer.get(p);
                if(i <= recordPlacements) {
                    final GlobalChallengePrize prize = GlobalChallengePrize.valueOf(i);
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
                if(g.getType().getDisplayItem().getItemMeta().getDisplayName().equals(d)) {
                    return g;
                }
            }
        }
        return null;
    }

    public static void deleteAll() {
        active = null;
        pm = null;
        globalchallenges = null;
    }
}
