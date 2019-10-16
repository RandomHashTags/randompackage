package me.randomhashtags.randompackage;

import com.google.inject.Inject;
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
        version = "16.5.0",
        authors = {"RandomHashTags"}
)
public class RandomPackage {

    public static RandomPackage getPlugin;

    @Inject private Logger logger;

    private Callable<Map<String, Integer>> settings;

    public static Optional<PluginContainer> rpg;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        enable();
    }

    public void enable() {
        getPlugin = this;

        enableSoftDepends();
    }
    private void enableSoftDepends() {

    }
    private PluginContainer getPlugin(String id) {
        final PluginManager pm = Sponge.getPluginManager();
        final boolean enabled = pm.isLoaded(id) && pm.getPlugin(id).isPresent();
        return enabled ? pm.getPlugin(id).get() : null;
    }
}
