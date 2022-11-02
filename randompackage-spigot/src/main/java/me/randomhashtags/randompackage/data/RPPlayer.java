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
    @Nullable
    default Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }
    @NotNull
    default OfflinePlayer getOfflinePlayer() {
        final UUID uuid = getUUID();
        return Bukkit.getOfflinePlayer(uuid);
    }

    HashMap<String, Integer> getUnclaimedLootboxes();
    List<ItemStack> getUnclaimedPurchases();
    @NotNull SecondaryData getSecondaryData();
    @NotNull CoinFlipData getCoinFlipData();
    @NotNull CustomEnchantData getCustomEnchantData();
    @NotNull DisguiseData getDisguiseData();
    @NotNull DuelData getDuelData();
    @NotNull GlobalChallengeData getGlobalChallengeData();
    @NotNull HomeData getHomeData();
    @NotNull ItemFilterData getItemFilterData();
    @NotNull JackpotData getJackpotData();
    @NotNull KitData getKitData();
    @NotNull MonthlyCrateData getMonthlyCrateData();
    @NotNull PlayerQuestData getPlayerQuestData();
    @NotNull RarityGemData getRarityGemData();
    @NotNull ReputationData getReputationData();
    @NotNull ShowcaseData getShowcaseData();
    @NotNull SlotBotData getSlotBotData();
    @NotNull TitleData getTitleData();
}
