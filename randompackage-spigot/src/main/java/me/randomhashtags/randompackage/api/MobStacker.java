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
            final Entity e = getEntity(UUID.fromString(s));
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
    public void stackEntities(World world) {
        final List<EntityType> types = new ArrayList<>();
        for(Entity entity : world.getEntities()) {
            if(entity instanceof LivingEntity) {
                final EntityType type = entity.getType();
                if(!types.contains(type)) {
                    stackEntities(world, type);
                    types.add(type);
                }
            }
        }
    }
    public void stackEntities(World w, EntityType type) {
        if(!stackable.contains(type)) return;
        final List<Entity> entities = new ArrayList<>();
        for(Entity e : w.getEntities()) {
            if(e.getType().equals(type)) {
                entities.add(e);
            }
        }
        final String n = w.getName();
        final int max = maxStackSize.getOrDefault(n, 0);
        final double radius = stackRadius.getOrDefault(n, 0.00);
        if(max == 0 || radius <= 0) return;
        final String name = customNames.get(type);
        for(Entity entity : entities) {
            final UUID u = entity.getUniqueId();
            StackedEntity stackedEntity = StackedEntity.valueOf(u);
            final EntityType t = entity.getType();
            final List<Entity> nearby = entity.getNearbyEntities(radius, radius, radius);
            for(Entity nearbyEntity : nearby) {
                final StackedEntity se = StackedEntity.valueOf(nearbyEntity.getUniqueId());
                if(!entity.isDead() && !nearbyEntity.isDead() && nearbyEntity.getType().equals(t) && (se != null || nearbyEntity.getCustomName() == null)) {
                    if(se != null) {
                        if(max == -1 || se.size+(stackedEntity == null ? 1 : stackedEntity.size) <= max) {
                            se.merge((LivingEntity) entity);
                        }
                    } else if(stackedEntity != null) {
                        if(max == -1 || stackedEntity.size+1 <= max) {
                            stackedEntity.merge((LivingEntity) nearbyEntity);
                        }
                    } else {
                        stackedEntity = new StackedEntity(System.currentTimeMillis(), (LivingEntity) entity, name, 1);
                        stackedEntity.merge((LivingEntity) nearbyEntity);
                    }
                }
            }
        }
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final UUID u = event.getEntity().getUniqueId();
        final StackedEntity s = StackedEntity.valueOf(u);
        if(s != null) {
            s.kill(null, 1);
        }
    }
    @EventHandler
    private void entityDamageEvent(EntityDamageEvent event) {
        final String cause = event.getCause().name();
        final Entity e = event.getEntity();
        final UUID u = e.getUniqueId();
        final StackedEntity s = StackedEntity.valueOf(u);
        if(s != null && (cause.contains("LAVA") || cause.contains("FIRE"))) {
            final LivingEntity le = (LivingEntity) e;
            if(le.getHealth() - event.getFinalDamage() <= 0.000) {
                le.setHealth(le.getMaxHealth());
                le.setFireTicks(0);
                s.kill(null, 1);
            }
            if(s.size == 1) {
                StackedEntity.STACKED_ENTITIES.remove(s);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity(), dam = event.getDamager();
        final LivingEntity e = entity instanceof LivingEntity ? (LivingEntity) entity : null, damager = e != null && dam instanceof LivingEntity ? (LivingEntity) dam : null;
        if(e != null && !event.getEntityType().equals(EntityType.PLAYER)) {
            final StackedEntity s = StackedEntity.valueOf(e.getUniqueId());
            if(s != null && e.getHealth()-event.getFinalDamage() <= 0.000) {
                event.setDamage(0);
                e.setHealth(e.getMaxHealth());
                s.kill(damager, 1);
            }
            if(s != null && s.size == 1) {
                StackedEntity.STACKED_ENTITIES.remove(s);
            }
        }
    }
}
