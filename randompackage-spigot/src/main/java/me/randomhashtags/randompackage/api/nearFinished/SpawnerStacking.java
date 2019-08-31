package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class SpawnerStacking extends RPFeature {
    private static SpawnerStacking instance;
    public static SpawnerStacking getSpawnerStacking() {
        if(instance == null) instance = new SpawnerStacking();
        return instance;
    }

    public YamlConfiguration config;

    private String spawnerMaterial;
    private HashMap<Location, Integer> stacks;

    public String getIdentifier() { return "SPAWNER_STACKING"; }
    protected RPFeature getFeature() { return getSpawnerStacking(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "spawner stacking.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "spawner stacking.yml"));

        spawnerMaterial = UMaterial.SPAWNER.getVersionName();
        stacks = new HashMap<>();

        sendConsoleMessage("&6[RandomPackage] &aLoaded Spawner Stacking &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    private boolean isSpawner(Block b) { return b != null && b.getType().name().equals(spawnerMaterial); }
    public int getMaxAllowedStackSize(World world, EntityType type) {
        final int d = config.getInt("max stacking.default");
        if(world == null) {
            return d;
        } else {
            final String n = world.getName();
            final List<String> sl = config.getStringList("max stacking." + type.name());
            if(!sl.isEmpty()) {
                for(String s : sl) {
                    if(s.startsWith(n + "=")) {
                        return Integer.parseInt(s.split("=")[1]);
                    }
                }
            }
            return d;
        }
    }

    public void viewAmount(Player player, Location spawnerLocation) {
        if(stacks.containsKey(spawnerLocation)) {
            final CreatureSpawner c = (CreatureSpawner) spawnerLocation.getBlock();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TYPE}", c.getSpawnedType().name());
            replacements.put("{AMOUNT}", formatInt(stacks.get(spawnerLocation)));
            sendStringListMessage(player, config.getStringList("messages.view amount"), replacements);
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Block b = event.getClickedBlock();
        if(b != null) {
            if(isSpawner(b)) {
                final Location l = b.getLocation();
                if(stacks.containsKey(b.getLocation())) {
                    final Player player = event.getPlayer();
                    event.setCancelled(true);
                    player.updateInventory();
                    viewAmount(player, l);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Block b = event.getBlock();
        if(isSpawner(b)) {
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Block b = event.getBlock();
        if(isSpawner(b)) {
        }
    }
}
