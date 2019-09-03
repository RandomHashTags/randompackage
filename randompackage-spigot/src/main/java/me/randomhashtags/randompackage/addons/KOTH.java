package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Captureable;
import me.randomhashtags.randompackage.addons.utils.Scheduleable;
import me.randomhashtags.randompackage.addons.utils.Scoreboardable;
import me.randomhashtags.randompackage.dev.KOTHMonster;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public interface KOTH extends Captureable, Scheduleable, Scoreboardable {
    String getName();
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
