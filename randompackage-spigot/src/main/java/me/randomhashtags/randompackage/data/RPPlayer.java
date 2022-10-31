package me.randomhashtags.randompackage.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface RPPlayer {

    boolean isLoaded();
    RPPlayer load();
    void unload();
    void backup();

    @NotNull UUID getUUID();
    default Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }
    default OfflinePlayer getOfflinePlayer() {
        final UUID uuid = getUUID();
        return Bukkit.getOfflinePlayer(uuid);
    }

    HashMap<String, Integer> getUnclaimedLootboxes();
    List<ItemStack> getUnclaimedPurchases();
    @Nullable SecondaryData getSecondaryData();
    @Nullable CoinFlipData getCoinFlipData();
    @Nullable CustomEnchantData getCustomEnchantData();
    @Nullable DisguiseData getDisguiseData();
    @Nullable DuelData getDuelData();
    @Nullable GlobalChallengeData getGlobalChallengeData();
    @Nullable HomeData getHomeData();
    @Nullable ItemFilterData getItemFilterData();
    @Nullable JackpotData getJackpotData();
    @Nullable KitData getKitData();
    @Nullable MonthlyCrateData getMonthlyCrateData();
    @Nullable PlayerQuestData getPlayerQuestData();
    @Nullable RarityGemData getRarityGemData();
    @Nullable ReputationData getReputationData();
    @Nullable ShowcaseData getShowcaseData();
    @Nullable SlotBotData getSlotBotData();
    @Nullable TitleData getTitleData();
}
