package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.classes.StackedEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.*;

public class MobStacker extends RandomPackageAPI {

    private static MobStacker instance;
    public static final MobStacker getMobStacker() {
        if(instance == null) instance = new MobStacker();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;

    private String defaultName;
    public List<EntityType> stackable;
    public HashMap<EntityType, String> customNames;
    private HashMap<UUID, Living> lastDamager;
    private boolean stacksViaNatural = false, stacksViaSpawner = false, stacksViaEgg = false;
    private List<Integer> tasks;
    private HashMap<String, Integer> maxStackSize;
    private HashMap<String, Double> stackRadius;
    private HashMap<String, Boolean> slaysStack;

    public void enable() {
        if(isEnabled) return;
        save(null, "mob stacker.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "mob stacker.yml"));
        eventmanager.registerListeners(randompackage, this);
        isEnabled = true;

        stackable = new ArrayList<>();
        customNames = new HashMap<>();
        lastDamager = new HashMap<>();

        defaultName = translateColorCodes(config.getString("names.default"));
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
                tasks.add(scheduler.scheduleSyncRepeatingTask(randompackage, () -> stackEntities(w), 0, Integer.parseInt(a[1])));
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
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        for(int i : tasks) scheduler.cancelTask(i);

        config = null;
        stackable = null;
        tasks = null;
        maxStackSize = null;
        stackRadius = null;
        slaysStack = null;
        defaultName = null;
        customNames = null;
        lastDamager = null;

        backup();
        eventmanager.unregisterListeners(this);
    }

    public void backup() {
        final List<StackedEntity> se = StackedEntity.stackedEntities;
        final YamlConfiguration a = otherdata;
        a.set("stacked mobs", null);
        for(StackedEntity e : se) {
            final long c = e.creationTime;
            if(c != 0) {
                final UUID u = e.uuid;
                a.set("stacked mobs." + u + ".creation", c);
                a.set("stacked mobs." + u + ".size", e.size);
            }
        }
        saveOtherData();
        se.clear();
    }
    public void loadBackup() {
        final long started = System.currentTimeMillis();
        int loaded = 0;
        final YamlConfiguration a = otherdata;
        final ConfigurationSection o = a.getConfigurationSection("stacked mobs");
        if(o != null) {
            for(String s : o.getKeys(false)) {
                final Entity e = getEntity(UUID.fromString(s));
                if(e != null && !e.isRemoved() && e instanceof Living) {
                    new StackedEntity(a.getLong("stacked mobs." + s + ".creation"), (LivingEntity) e, customNames.get(e.getType()), a.getInt("stacked mobs." + s + ".size"));
                    loaded += 1;
                }
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " stacked mobs &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }

    @Listener(priority = EventPriority.HIGHEST)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(!event.isCancelled()) {
            final String sr = event.getSpawnReason().name();
            if(sr.contains("NATURAL") || sr.contains("CHUNK_GEN")) {
            } else if(sr.contains("EGG")) {
            } else if(sr.contains("SPAWNER")) {
            }
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
        final List<EntityType> T = new ArrayList<>();
        for(Entity e : world.getEntities()) {
            if(e instanceof Living) {
                final EntityType t = e.getType();
                if(!T.contains(t)) {
                    stackEntities(world, t);
                    T.add(t);
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
        for(int i = 0; i < entities.size(); i++) {
            final Entity e = entities.get(i);
            final UUID u = e.getUniqueId();
            StackedEntity s = StackedEntity.valueOf(u);
            final EntityType t = e.getType();
            final Collection<Entity> nearby = e.getNearbyEntities(radius);
            for(int o = 0; o < nearby.size(); o++) {
                final Entity E = nearby.get(o);
                final StackedEntity se = StackedEntity.valueOf(E.getUniqueId());
                if(!e.isDead() && !E.isDead() && E.getType().equals(t) && (se != null || E.getCustomName() == null)) {
                    if(se != null) {
                        if(max == -1 || se.size+(s == null ? 1 : s.size) <= max) {
                            se.merge((Living) e);
                        }
                    } else if(s != null) {
                        if(max == -1 || s.size+1 <= max) {
                            s.merge((Living) E);
                        }
                    } else {
                        s = new StackedEntity(System.currentTimeMillis(), (Living) e, name, 1);
                        s.merge((Living) E);
                    }
                }
            }
        }
    }
    @Listener
    private void entityDeathEvent(EntityDeathEvent event) {
        final UUID u = event.getEntity().getUniqueId();
        final StackedEntity s = StackedEntity.valueOf(u);
        if(s != null)
            s.kill(null, 1);
    }
    @Listener
    private void entityDamageEvent(EntityDamageEvent event) {
        final String cause = event.getCause().name();
        final Entity e = event.getEntity();
        final UUID u = e.getUniqueId();
        final StackedEntity s = StackedEntity.valueOf(u);
        if(s != null && (cause.contains("LAVA") || cause.contains("FIRE"))) {
            final Living le = (Living) e;
            if(le.getHealth() - event.getFinalDamage() <= 0.000) {
                le.setHealth(le.getMaxHealth());
                le.setFireTicks(0);
                s.kill(null, 1);
            }
            if(s.size == 1) StackedEntity.stackedEntities.remove(s);
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(!event.isCancelled()) {
            final Entity entity = event.getEntity(), dam = event.getDamager();
            final Living e = entity instanceof Living ? (Living) entity : null, damager = e != null && dam instanceof Living ? (Living) dam : null;
            if(e != null && !event.getEntityType().equals(EntityType.PLAYER)) {
                final StackedEntity s = StackedEntity.valueOf(e.getUniqueId());
                if(s != null && e.getHealth()-event.getFinalDamage() <= 0.000) {
                    event.setDamage(0);
                    e.setHealth(e.getMaxHealth());
                    s.kill(damager, 1);
                }
                if(s != null && s.size == 1) StackedEntity.stackedEntities.remove(s);
            }
        }
    }
}
