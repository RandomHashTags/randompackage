package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.data.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class FileRPPlayer implements RPPlayer {

    private boolean isLoaded;
    private UUID uuid;
    private File file;
    private YamlConfiguration yml;

    private CoinFlipData coinflip;

    public boolean isLoaded() {
        return isLoaded;
    }
    public FileRPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;
        }
        return this;
    }
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
        }
    }
    public void backup() {
    }

    public UUID getUUID() {
        return uuid;
    }
    public YamlConfiguration getConfig() {
        return yml;
    }

    public CoinFlipData getCoinFlipData() {
        if(coinflip == null) {
            if(yml.get("coinflip") != null) {
            }
        }
        return coinflip;
    }

    public DisguiseData getDisguiseData() {
        return null;
    }

    public DuelData getDuelData() {
        return null;
    }

    public GlobalChallengeData getGlobalChallengeData() {
        return null;
    }

    public HomeData getHomeData() {
        return null;
    }

    public ItemFilterData getItemFilterData() {
        return null;
    }

    public JackpotData getJackpotData() {
        return null;
    }

    public MonthlyCrateData getMonthlyCrateData() {
        return null;
    }

    public PlayerQuestData getPlayerQuestData() {
        return null;
    }

    public RarityGemData getRarityGemData() {
        return null;
    }

    public ReputationData getReputationData() {
        return null;
    }

    public ShowcaseData getShowcaseData() {
        return null;
    }

    public SlotBotData getSlotBotData() {
        return null;
    }

    public TitleData getTitleData() {
        return null;
    }
}
