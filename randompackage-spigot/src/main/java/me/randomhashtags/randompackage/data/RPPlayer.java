package me.randomhashtags.randompackage.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface RPPlayer {

    boolean isLoaded();
    RPPlayer load();
    void unload();
    void backup();

    UUID getUUID();
    default Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }
    default OfflinePlayer getOfflinePlayer() {
        final UUID uuid = getUUID();
        return uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
    }

    HashMap<String, Integer> getUnclaimedLootboxes();
    List<ItemStack> getUnclaimedPurchases();
    SecondaryData getSecondaryData();
    CoinFlipData getCoinFlipData();
    CustomEnchantData getCustomEnchantData();
    DisguiseData getDisguiseData();
    DuelData getDuelData();
    GlobalChallengeData getGlobalChallengeData();
    HomeData getHomeData();
    ItemFilterData getItemFilterData();
    JackpotData getJackpotData();
    KitData getKitData();
    MonthlyCrateData getMonthlyCrateData();
    PlayerQuestData getPlayerQuestData();
    RarityGemData getRarityGemData();
    ReputationData getReputationData();
    ShowcaseData getShowcaseData();
    SlotBotData getSlotBotData();
    TitleData getTitleData();
}
