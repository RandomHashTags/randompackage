package me.randomhashtags.randompackage.recode.api.addons.active;

import me.randomhashtags.randompackage.api.GlobalChallenges;
import me.randomhashtags.randompackage.api.events.GlobalChallengeEndEvent;
import me.randomhashtags.randompackage.recode.api.addons.GlobalChallenge;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.recode.utils.GlobalChallengePrize;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.math.BigDecimal;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class ActiveGlobalChallenge {
    public static HashMap<GlobalChallenge, ActiveGlobalChallenge> active;
    private static PluginManager pm;
    private static GlobalChallenges globalchallenges;
    private GlobalChallenge type;
    private HashMap<UUID, BigDecimal> participants;
    private int task;
    private long started;

    public ActiveGlobalChallenge(long started, GlobalChallenge type, HashMap<UUID, BigDecimal> participants) {
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
    public HashMap<UUID, BigDecimal> getParticipants() { return participants; }

    public long getRemainingTime() {
        return started+(type.getDuration()*1000)-System.currentTimeMillis();
    }
    public void increaseValue(UUID player, BigDecimal value) {
        final Map<UUID, BigDecimal> a = globalchallenges.getPlacing(participants, 1);
        final BigDecimal before = participants.getOrDefault(player, BigDecimal.ZERO), after = before.add(value);
        if(!a.isEmpty()) {
            final UUID first = (UUID) a.keySet().toArray()[0];
            if(!first.equals(player)) {
                final double v = ((BigDecimal) a.values().toArray()[0]).doubleValue();
                if(before.doubleValue() <= v && after.doubleValue() > v) {
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
    public BigDecimal getValue(UUID player) {
        return participants.getOrDefault(player, BigDecimal.ZERO);
    }
    public void setValue(UUID player, BigDecimal value) {
        participants.put(player, value);
    }

    public void end(boolean giveRewards, int recordPlacements) {
        final GlobalChallengeEndEvent e = new GlobalChallengeEndEvent(this, giveRewards);
        pm.callEvent(e);
        globalchallenges.reloadInventory();
        final Map<UUID, BigDecimal> placements = globalchallenges.getPlacing(participants);
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
