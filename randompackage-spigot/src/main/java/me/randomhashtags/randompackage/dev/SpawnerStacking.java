package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.obj.StackedSpawner;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.mechanics.SpawnerAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum SpawnerStacking implements RPFeatureSpigot {
    INSTANCE;

    private File dataF;
    public YamlConfiguration config, data;

    private Material spawnerMaterial;
    private boolean onlyStackableInClaimFactionLand;
    private int defaultMaxStackSize;
    private HashMap<String, HashMap<String, Integer>> maxStackSizes;
    private HashMap<Location, StackedSpawner> stacks;

    @Override
    public String getIdentifier() {
        return "SPAWNER_STACKING";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("_Data", "spawner stacking.yml");
        dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "spawner stacking.yml");
        data = YamlConfiguration.loadConfiguration(dataF);

        save(null, "spawner stacking.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "spawner stacking.yml"));

        spawnerMaterial = UMaterial.SPAWNER.getMaterial();
        onlyStackableInClaimFactionLand = config.getBoolean("only stackable in.claimed faction land");
        defaultMaxStackSize = config.getInt("max stacking.default");

        maxStackSizes = new HashMap<>();
        for(String entity : config.getConfigurationSection("max stacking").getKeys(false)) {
            if(!entity.equals("default")) {
                maxStackSizes.put(entity, new HashMap<>());
                final HashMap<String, Integer> sizes = maxStackSizes.get(entity);
                for(String s : config.getStringList("max stacking." + entity)) {
                    final String[] values = s.split("=");
                    sizes.put(values[0], Integer.parseInt(values[1]));
                }
            }
        }

        stacks = new HashMap<>();
        loadBackup(true);

        sendConsoleMessage("&6[RandomPackage] &aLoaded Spawner Stacking &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    public void loadBackup(boolean async) {
        if(async) {
            SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> loadbackup(true));
        } else {
            loadbackup(false);
        }
    }
    private void loadbackup(boolean async) {
        final long started = System.currentTimeMillis();
        stacks.clear();
        final ConfigurationSection cs = data.getConfigurationSection("locations");
        if(cs != null) {
            for(String s : cs.getKeys(false)) {
                final String[] values = data.getString("locations." + s).split(";");
                stacks.put(toLocation(s), new StackedSpawner(EntityType.valueOf(values[0]), Integer.parseInt(values[1])));
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + stacks.size() + " Stacked Spawners " + (async ? "&6[async]" : "&e(took " + (System.currentTimeMillis()-started) + "ms)"));
    }
    public void backup() {
        data.set("locations", null);
        for(Location l : stacks.keySet()) {
            final StackedSpawner ss = stacks.get(l);
            data.set("locations." + toString(l), ss.getType().name() + ";" + ss.getStack());
        }
        try {
            data.save(dataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "spawner stacking.yml");
        data = YamlConfiguration.loadConfiguration(dataF);
    }

    private boolean isSpawner(Block b) {
        return b != null && b.getType().equals(spawnerMaterial);
    }
    public int getMaxAllowedStackSize(@NotNull World world, @NotNull EntityType type) {
        final HashMap<String, Integer> sizes = maxStackSizes.getOrDefault(world.getName(), null);
        return sizes != null ? sizes.getOrDefault(type.name(), defaultMaxStackSize) : defaultMaxStackSize;
    }
    private void didPlaceSpawner(Location l, EntityType type) {
        stacks.put(l, new StackedSpawner(type, 1));
    }
    private boolean canPlaceAt(Player player, Location l) {
        final List<Boolean> checks = new ArrayList<>();
        final Chunk c = l.getChunk();
        final RegionalAPI regions = RegionalAPI.INSTANCE;
        if(regions.hookedFactionsUUID()) {
            final String fac = regions.getFactionTag(player.getUniqueId());
            checks.add(onlyStackableInClaimFactionLand && fac != null && FactionsUUID.INSTANCE.getRegionalChunks(fac).contains(c));
        }
        return !checks.contains(false);
    }

    public void viewAmount(@NotNull Player player, @NotNull Location spawnerLocation) {
        if(hasPermission(player, "RandomPackage.spawnerstacking.view", true) && stacks.containsKey(spawnerLocation)) {
            final CreatureSpawner c = (CreatureSpawner) spawnerLocation.getBlock();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TYPE}", c.getSpawnedType().name());
            replacements.put("{AMOUNT}", formatInt(stacks.get(spawnerLocation).getStack()));
            sendStringListMessage(player, getStringList(config, "messages.view amount"), replacements);
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Block b = event.getClickedBlock();
        if(isSpawner(b)) {
            final Location l = b.getLocation();
            if(stacks.containsKey(l)) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                viewAmount(player, l);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Block b = event.getBlock();
        if(isSpawner(b)) {
            final Player player = event.getPlayer();
            final Location l = b.getLocation();
            if(canPlaceAt(player, l)) {
                final ItemStack is = event.getItemInHand();
                final EntityType first = SpawnerAPI.INSTANCE.getType(is);
                final Block target = event.getBlockAgainst();
                if(!player.isSneaking() && isSpawner(target)) {
                    final EntityType second = ((CreatureSpawner) target).getSpawnedType();
                    if(first == second) {
                        final StackedSpawner stack = stacks.getOrDefault(target.getLocation(), null);
                        if(stack != null) {
                            event.setCancelled(true);
                            player.updateInventory();
                            final int size = stack.getStack(), newSize = size+1, max = getMaxAllowedStackSize(target.getWorld(), second);
                            if(newSize <= max) {
                                stack.setStack(newSize);
                                removeItem(player, is, 1);
                                final HashMap<String, String> replacements = new HashMap<>();
                                replacements.put("{TYPE}", first.name());
                                replacements.put("{TOTAL}", Integer.toString(newSize));
                                sendStringListMessage(player, getStringList(config, "messages.added spawner"), replacements);
                            } else {
                                final HashMap<String, String> replacements = new HashMap<String, String>(){{ put("{MAX}", Integer.toString(max)); }};
                                sendStringListMessage(player, getStringList(config, "messages.only stack up to max"), replacements);
                            }
                        }
                    } else {
                        didPlaceSpawner(l, first);
                    }
                } else {
                    didPlaceSpawner(l, first);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Block b = event.getBlock();
        final Location l = b.getLocation();
        if(isSpawner(b) && stacks.containsKey(l)) {
            final Player player = event.getPlayer();
            final StackedSpawner spawner = stacks.get(l);
            final int stack = spawner.getStack();
            if(stack == 1) {
                stacks.remove(l);
            } else {
                event.setCancelled(true);
                player.updateInventory();
                spawner.setStack(stack-1);
                final ItemStack is = SpawnerAPI.INSTANCE.getItem(spawner.getType().name());
                if(is != null) {
                    l.getWorld().dropItem(l.clone().add(0, 1, 0), is);
                }
            }
        }
    }
}
