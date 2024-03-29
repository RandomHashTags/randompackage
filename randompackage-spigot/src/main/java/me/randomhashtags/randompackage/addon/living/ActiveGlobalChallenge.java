package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.api.GlobalChallenges;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.event.GlobalChallengeEndEvent;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ActiveGlobalChallenge implements RPStorage {
    public static HashMap<GlobalChallenge, ActiveGlobalChallenge> ACTIVE;
    private final GlobalChallenge type;
    private HashMap<UUID, BigDecimal> participants;
    private final int task;
    private final long started;

    public ActiveGlobalChallenge(long started, GlobalChallenge type, HashMap<UUID, BigDecimal> participants) {
        if(ACTIVE == null) {
            ACTIVE = new HashMap<>();
        }
        this.started = started;
        this.type = type;
        this.participants = participants;
        long remainingTime = getRemainingTime();
        if(remainingTime < 0) {
            remainingTime = 0;
        }
        task = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> end(true, 3), remainingTime);
        ACTIVE.put(type, this);
    }

    public long getStartedTime() {
        return started;
    }
    public GlobalChallenge getType() {
        return type;
    }
    public HashMap<UUID, BigDecimal> getParticipants() {
        return participants;
    }
    public void setParticipants(HashMap<UUID, BigDecimal> participants) {
        this.participants = participants;
    }

    public long getRemainingTime() {
        return (started + type.getDuration() * 1000) - System.currentTimeMillis();
    }
    public void increaseValue(UUID player, BigDecimal value) {
        final GlobalChallenges global_challenges = GlobalChallenges.INSTANCE;
        final Map<UUID, BigDecimal> placing = global_challenges.getPlacing(participants, 1);
        final BigDecimal before = participants.getOrDefault(player, BigDecimal.ZERO), after = before.add(value);
        if(!placing.isEmpty()) {
            final UUID first = (UUID) placing.keySet().toArray()[0];
            if(!first.equals(player)) {
                final double value_double = ((BigDecimal) placing.values().toArray()[0]).doubleValue();
                if(before.doubleValue() <= value_double && after.doubleValue() > value_double) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    final String time = global_challenges.getRemainingTime(getRemainingTime());
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
        final GlobalChallengeEndEvent event = new GlobalChallengeEndEvent(this, giveRewards);
        PLUGIN_MANAGER.callEvent(event);
        final GlobalChallenges global_challenges = GlobalChallenges.INSTANCE;
        global_challenges.reloadInventory();
        final Map<UUID, BigDecimal> placements = global_challenges.getPlacing(participants);
        if(task != -1) {
            SCHEDULER.cancelTask(task);
        }
        ACTIVE.remove(type);
        if(giveRewards) {
            int i = 1;
            for(UUID participant_uuid : placements.keySet()) {
                final FileRPPlayer pdata = FileRPPlayer.get(participant_uuid);
                if(i <= recordPlacements) {
                    final GlobalChallengePrize prize = valueOfGlobalChallengePrize(i);
                    pdata.getGlobalChallengeData().addPrize(prize);
                    i += 1;
                }
            }
        }
    }

    @Nullable
    public static ActiveGlobalChallenge valueOf(@Nullable ItemStack display) {
        if(ACTIVE != null && display != null && display.hasItemMeta() && display.getItemMeta().hasDisplayName()) {
            final String name = display.getItemMeta().getDisplayName();
            for(ActiveGlobalChallenge challenge : ACTIVE.values()) {
                if(challenge.getType().getItem().getItemMeta().getDisplayName().equals(name)) {
                    return challenge;
                }
            }
        }
        return null;
    }

    public static void deleteAll() {
        ACTIVE = null;
    }
}
