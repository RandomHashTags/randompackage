package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.obj.StackedEntity;
import me.randomhashtags.randompackage.attribute.DepleteStackSize;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum MobStacker implements RPFeatureSpigot {
    INSTANCE;

    public YamlConfiguration config;
    public List<EntityType> stackable;
    public HashMap<EntityType, String> customNames;
    private HashMap<UUID, LivingEntity> lastDamager;
    private final boolean stacksViaNatural = false;
    private final boolean stacksViaSpawner = false;
    private final boolean stacksViaEgg = false;
    private List<Integer> tasks;
    private HashMap<String, Integer> maxStackSize;
    private HashMap<String, Double> stackRadius;
    private HashMap<String, Boolean> slaysStack;

    @Override
    public void load() {
        save(null, "mob stacker.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "mob stacker.yml"));

        new DepleteStackSize().load();
        stackable = new ArrayList<>();
        customNames = new HashMap<>();
        lastDamager = new HashMap<>();

        final String defaultName = colorize(config.getString("names.default"));
        for(String s : config.getStringList("settings.stackable")) {
            stackable.add(EntityType.valueOf(s.toUpperCase()));
        }
        for(EntityType t : EntityType.values()) {
            final String s = config.getString("names." + t.name());
            customNames.put(t, s != null ? s : defaultName.replace("{TYPE}", t.name().replace("_", " ")));
        }
        tasks = new ArrayList<>();
        for(String s : config.getStringList("settings.tick rate check")) {
            final String[] a = s.split("=");
            final World w = Bukkit.getWorld(a[0]);
            if(w != null) {
                tasks.add(SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> stackEntities(w), 0, Integer.parseInt(a[1])));
            }
        }
        maxStackSize = new HashMap<>();
        for(String s : config.getStringList("settings.max stack size")) {
            final String[] a = s.split("=");
            maxStackSize.put(a[0], Integer.parseInt(a[1]));
        }
        stackRadius = new HashMap<>();
        for(String s : config.getStringList("settings.stack radius")) {
            final String[] a = s.split("=");
            stackRadius.put(a[0], Double.parseDouble(a[1]));
        }
        slaysStack = new HashMap<>();
        for(String s : config.getStringList("settings.slays stack")) {
            final String[] a = s.split("=");
            slaysStack.put(a[0], Boolean.parseBoolean(a[1]));
        }
        loadBackup();
    }
    @Override
    public void unload() {
        for(int i : tasks) {
            SCHEDULER.cancelTask(i);
        }
        backup();
    }

    public void backup() {
        final List<StackedEntity> se = StackedEntity.STACKED_ENTITIES;
        OTHER_YML.set("stacked mobs", null);
        for(StackedEntity e : se) {
            final long c = e.creationTime;
            if(c != 0) {
                final String u = "stacked mobs." + e.uuid + ".";
                OTHER_YML.set(u + "creation", c);
                OTHER_YML.set(u + "size", e.size);
            }
        }
        saveOtherData();
        se.clear();
    }
    public void loadBackup() {
        final long started = System.currentTimeMillis();
        int loaded = 0;
        for(String s : getConfigurationSectionKeys(OTHER_YML, "stacked mobs", false)) {
            final Entity e = get_entity_from_uuid(UUID.fromString(s));
            if(e != null && !e.isDead() && e instanceof LivingEntity) {
                new StackedEntity(OTHER_YML.getLong("stacked mobs." + s + ".creation"), (LivingEntity) e, customNames.get(e.getType()), OTHER_YML.getInt("stacked mobs." + s + ".size"));
                loaded += 1;
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " stacked mobs &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        final String sr = event.getSpawnReason().name();
        if(sr.contains("NATURAL") || sr.contains("CHUNK_GEN")) {
        } else if(sr.contains("EGG")) {
        } else if(sr.contains("SPAWNER")) {
        }
    }
    public void stackEntities() {
        final EntityType[] e = EntityType.values();
        for(World w : Bukkit.getWorlds()) {
            for(EntityType t : e) {
                stackEntities(w, t);
            }
        }
    }
    public void stackEntities(@NotNull World world) {
        final List<EntityType> types = new ArrayList<>();
        for(LivingEntity entity : world.getLivingEntities()) {
            final EntityType type = entity.getType();
            if(!types.contains(type)) {
                stackEntities(world, type);
                types.add(type);
            }
        }
    }
    public void stackEntities(@NotNull World world, @NotNull EntityType type) {
        if(!stackable.contains(type)) {
            return;
        }
        final List<Entity> entities = new ArrayList<>();
        for(Entity e : world.getEntities()) {
            if(e.getType().equals(type)) {
                entities.add(e);
            }
        }
        final String world_name = world.getName();
        final int max = maxStackSize.getOrDefault(world_name, 0);
        final double radius = stackRadius.getOrDefault(world_name, 0.00);
        if(max == 0 || radius <= 0) {
            return;
        }
        final String name = customNames.get(type);
        for(Entity entity : entities) {
            final UUID entity_uuid = entity.getUniqueId();
            StackedEntity stacked_entity = StackedEntity.valueOf(entity_uuid);
            final int stacked_entity_size = stacked_entity == null ? 1 : stacked_entity.size;
            final EntityType entity_type = entity.getType();
            final List<Entity> nearby_entities = entity.getNearbyEntities(radius, radius, radius);
            for(Entity nearby_entity : nearby_entities) {
                final StackedEntity nearby_stacked_entity = StackedEntity.valueOf(nearby_entity.getUniqueId());
                if(!entity.isDead() && !nearby_entity.isDead() && nearby_entity.getType().equals(entity_type) && (nearby_stacked_entity != null || nearby_entity.getCustomName() == null)) {
                    if(nearby_stacked_entity != null) {
                        if(max == -1 || nearby_stacked_entity.size + stacked_entity_size <= max) {
                            nearby_stacked_entity.merge((LivingEntity) entity);
                        }
                    } else if(stacked_entity != null) {
                        if(max == -1 || stacked_entity_size + 1 <= max) {
                            stacked_entity.merge((LivingEntity) nearby_entity);
                        }
                    } else {
                        stacked_entity = new StackedEntity(System.currentTimeMillis(), (LivingEntity) entity, name, 1);
                        stacked_entity.merge((LivingEntity) nearby_entity);
                    }
                }
            }
        }
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final StackedEntity stacked_entity = StackedEntity.valueOf(event.getEntity().getUniqueId());
        if(stacked_entity != null) {
            stacked_entity.kill(null, 1);
        }
    }
    @EventHandler
    private void entityDamageEvent(EntityDamageEvent event) {
        final String cause = event.getCause().name();
        final Entity entity = event.getEntity();
        final StackedEntity stacked_entity = StackedEntity.valueOf(entity.getUniqueId());
        if(stacked_entity != null && (cause.contains("LAVA") || cause.contains("FIRE"))) {
            final LivingEntity living_entity = (LivingEntity) entity;
            if(living_entity.getHealth() - event.getFinalDamage() <= 0.000) {
                living_entity.setHealth(living_entity.getMaxHealth());
                living_entity.setFireTicks(0);
                stacked_entity.kill(null, 1);
            }
            if(stacked_entity.size == 1) {
                StackedEntity.STACKED_ENTITIES.remove(stacked_entity);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity(), dam = event.getDamager();
        final LivingEntity living_entity = entity instanceof LivingEntity ? (LivingEntity) entity : null, damager = living_entity != null && dam instanceof LivingEntity ? (LivingEntity) dam : null;
        if(living_entity != null && !event.getEntityType().equals(EntityType.PLAYER)) {
            final StackedEntity stacked_entity = StackedEntity.valueOf(living_entity.getUniqueId());
            if(stacked_entity != null && living_entity.getHealth()-event.getFinalDamage() <= 0.000) {
                event.setDamage(0);
                living_entity.setHealth(living_entity.getMaxHealth());
                stacked_entity.kill(damager, 1);
            }
            if(stacked_entity != null && stacked_entity.size == 1) {
                StackedEntity.STACKED_ENTITIES.remove(stacked_entity);
            }
        }
    }
}
