package me.randomhashtags.randompackage;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "randompackage",
        name = "RandomPackage",
        version = "16.2.0",
        authors = "RandomHashTags"
)
public class RandomPackage {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
