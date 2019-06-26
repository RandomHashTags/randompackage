package me.randomhashtags.randompackage;

import com.google.inject.Inject;
import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.nearFinished.PlayerQuests;
import me.randomhashtags.randompackage.api.needsRecode.FactionAdditions;
import me.randomhashtags.randompackage.api.unfinished.*;
import me.randomhashtags.randompackage.utils.RPEvents;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bstats.sponge.Metrics2;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

@Plugin(
        id = "randompackage",
        name = "RandomPackage",
        version = "16.2.0",
        authors = {"RandomHashTags", "GMatrixGames"}
)
public class RandomPackage {

    public static RandomPackage getPlugin;

    @Inject private Logger logger;
    @Inject private Metrics2 metrics;

    private Callable<Map<String, Integer>> settings;

    public static Optional<PluginContainer> rpg;

    private AuctionHouse auctionhouse;
    private ChatEvents chatevents;
    private CoinFlip coinflip;
    private CollectionFilter collectionfilter;
    private Conquest conquest;
    private CustomArmor customarmor;
    private CustomBosses custombosses;
    private CustomEnchants customenchants;
    private CustomExplosions customexplosions;
    private Duels duels;
    private Envoy envoy;
    private FactionAdditions factionadditions;
    private Fund fund;
    private GlobalChallenges globalchallenges;
    private Homes homes;
    private ItemFilter itemfilter;
    private Jackpot jackpot;
    private Kits kits;
    private KOTH koth;
    private LastManStanding lastmanstanding;
    private Lootboxes lootboxes;
    private Masks masks;
    private MobStacker mobstacker;
    private MonthlyCrates monthlycrates;
    private Outposts outposts;
    private Pets pets;
    private PlayerQuests playerquests;
    private ServerCrates servercrates;
    private Shop shop;
    private Showcase showcase;
    private Titles titles;
    private Trade trade;
    private Trinkets trinkets;
    private WildPvP wildpvp;

    private RandomPackageAPI api;
    private RPEvents rpevents;
    private SecondaryEvents secondaryevents;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        enable();
    }

    public void enable() {
        getPlugin = this;
        final UVersion uv = UVersion.getUVersion();
        enableMetrics();

        auctionhouse = AuctionHouse.getAuctionHouse();

        enableSoftDepends();
    }
    private void enableSoftDepends() {

    }
    private Optional<PluginContainer> getPlugin(String id) {
        final PluginManager pm = Sponge.getPluginManager();
        final boolean enabled = pm.isLoaded(id);
        return enabled ? pm.getPlugin(id) : Optional.empty();
    }


    private void enableMetrics() {
        metrics.addCustomChart(new Metrics2.SimplePie("sponge_server_version", () -> Sponge.getPlatform().getMinecraftVersion().getName()));
        metrics.addCustomChart(new Metrics2.AdvancedPie("sponge_features_used", settings));
        metrics.addCustomChart(new Metrics2.SimplePie("sponge_players", () -> Integer.toString(Sponge.getServer().getOnlinePlayers().size())));
    }
}
