package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Captureable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Scheduleable;
import me.randomhashtags.randompackage.addon.util.Scoreboardable;
import me.randomhashtags.randompackage.dev.KOTHMonster;
import me.randomhashtags.randompackage.util.universal.UInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public interface KOTH extends Captureable, Nameable, Scheduleable, Scoreboardable {
    List<String> getAllowedCommands();

    String getFlag();
    HashMap<String, String> getFlags();

    Scoreboard getCapturedScoreboard();

    List<KOTHMonster> getMonsters();

    ItemStack getLootbag();
    UInventory getLootbagGUI();
    List<String> getLootbagRewards();

    List<String> getJoinMsg();
    List<String> getInfoMsg();
    List<String> getNoEventRunningMsg();
    List<String> getAlreadyCapturedMsg();
    List<String> getEventRunningMsg();
    List<String> getStartingInMsg();
    List<String> getMonstersSpawnedMsg();
    List<String> getStartCapturingMsg();
    List<String> getNoLongerCapturingMsg();
    List<String> getBlockedCommandMsg();
    List<String> getTeleportMsg();
    List<String> getCapturingCountdowns();
    List<String> getCapturingMsg();
    List<String> getSetCenterMsg();

    String getRewardFormat();
    List<String> getOpenLootbagMsg();
}
