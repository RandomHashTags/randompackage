package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.api.Kits;
import me.randomhashtags.randompackage.api.events.FallenHeroSlainEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class LivingFallenHero {
    public static HashMap<UUID, LivingFallenHero> living;
    private static Kits kits;
    private static Random random;
    private static PluginManager pluginmanager;
    private Object kit;
    private FallenHero type;
    private LivingEntity fallenhero;
    private UUID summoner, fallenherouuid;
    private Location spawnedLocation;
    public LivingFallenHero(Object kit, FallenHero type, UUID summoner, Location spawnedLocation) {
        if(living == null) {
            living = new HashMap<>();
            kits = Kits.getKits();
            random = kits.random;
            pluginmanager = kits.pluginmanager;
        }
        this.kit = kit;
        this.type = type;
        this.summoner = summoner;
        this.spawnedLocation = spawnedLocation;
        fallenhero = kits.getEntity(type.getType(), spawnedLocation, true);
        final String N = kit instanceof GlobalKit ? ((GlobalKit) kit).getItem().getItemMeta().getDisplayName() : ((EvolutionKit) kit).getItem().getItemMeta().getDisplayName();
        fallenhero.setCustomName(type.getName().replace("{NAME}", N));
        fallenherouuid = fallenhero.getUniqueId();
        fallenhero.addPotionEffects(type.getPotionEffects());
        living.put(fallenherouuid, this);
    }
    public Object getKit() { return kit; }
    public FallenHero getType() { return type; }
    public LivingEntity getFallenHero() { return fallenhero; }
    public UUID getSummoner() { return summoner; }
    public Location getSpawnedLocation() { return spawnedLocation; }

    public void delete() {
        fallenhero.remove();
        killed(null);
    }
    public void killed(EntityDeathEvent event) {
        if(event != null) {
            event.setDroppedExp(0);
            event.getDrops().clear();

            final World w = fallenhero.getWorld();
            final boolean droppedGem = random.nextInt(100) <= type.getGemDropChance();
            if(droppedGem) {
                final HashMap<String, String> r = new HashMap<>();
                r.put("{PLAYER}", event.getEntity().getKiller().getName());
                r.put("{NAME}", fallenhero.getCustomName());
                for(String s : type.getReceiveKitMsg()) {
                    for(String re : r.keySet()) s = s.replace(re, r.get(re));
                    Bukkit.broadcastMessage(s);
                }
                w.dropItem(fallenhero.getLocation(), kit instanceof GlobalKit ? (((GlobalKit) kit).getFallenHeroGem()) : ((EvolutionKit) kit).getFallenHeroGem());
            } else {
                final GlobalKit K = kit instanceof GlobalKit ? (GlobalKit) kit : null;
                final EvolutionKit E = K == null && kit instanceof EvolutionKit ? (EvolutionKit) kit : null;
                final List<KitItem> items = new ArrayList<>(K != null? K.getItems() : E.getItems());
                final YamlConfiguration yml = K != null ? K.getYaml() : E.getYaml();
                w.dropItem(fallenhero.getLocation(), kits.d(yml, "items." + items.get(random.nextInt(items.size())).path, random.nextInt(K != null ? K.getMaxTier() : E.getMaxLevel())));
            }
            final FallenHeroSlainEvent e = new FallenHeroSlainEvent(event.getEntity().getKiller(), this, droppedGem);
            pluginmanager.callEvent(e);
        }

        living.remove(fallenherouuid);
        kit = null;
        type = null;
        fallenhero = null;
        fallenherouuid = null;
        summoner = null;
        spawnedLocation = null;
        if(living.isEmpty()) {
            living = null;
            kits = null;
            random = null;
        }
    }

    public static void deleteAll() {
        living = null;
        kits = null;
        random = null;
        pluginmanager = null;
    }
}
