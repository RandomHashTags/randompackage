package me.randomhashtags.randompackage;

import com.google.inject.Inject;
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

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        enable();
    }

    public void enable() {
        getPlugin = this;
        final UVersion uv = UVersion.getUVersion();
        enableMetrics();

        enableSoftDepends();
    }
    private void enableSoftDepends() {

    }
    private PluginContainer getPlugin(String id) {
        final PluginManager pm = Sponge.getPluginManager();
        final boolean enabled = pm.isLoaded(id) && pm.getPlugin(id).isPresent();
        return enabled ? pm.getPlugin(id).get() : null;
    }


    private void enableMetrics() {
        final PluginContainer p = getPlugin("Metrics");
        if(p != null) {
            metrics.addCustomChart(new Metrics2.SimplePie("sponge_server_version", () -> Sponge.getPlatform().getMinecraftVersion().getName()));
            metrics.addCustomChart(new Metrics2.AdvancedPie("sponge_features_used", settings));
            metrics.addCustomChart(new Metrics2.SimplePie("sponge_players", () -> Integer.toString(Sponge.getServer().getOnlinePlayers().size())));
        }
    }
}
