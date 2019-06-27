package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.api.Kits;
import me.randomhashtags.randompackage.api.events.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomKit;
import me.randomhashtags.randompackage.utils.interfaces.ILivingFallenHero;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.*;

public class LivingFallenHero implements ILivingFallenHero {
    public static HashMap<UUID, LivingFallenHero> living;
    private static Kits kits;

    private AbstractCustomKit kit;
    private FallenHero type;
    private UUID summoner;
    private LivingEntity fallenhero;
    private Location spawnedLocation;
    public LivingFallenHero(AbstractCustomKit kit, FallenHero type, UUID summoner, Location spawnedLocation) {
        if(living == null) {
            living = new HashMap<>();
            kits = Kits.getKits();
        }
        this.kit = kit;
        this.type = type;
        this.summoner = summoner;
        this.spawnedLocation = spawnedLocation;
        fallenhero = kits.getEntity(getFallenHero().getType(), getSpawnedLocation(), true);
        living.put(fallenhero.getUniqueId(), this);
    }
    public AbstractCustomKit getKit() { return kit; }
    public FallenHero getFallenHero() { return type; }
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
            final Random random = new Random();
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
                w.dropItem(fallenhero.getLocation(), kits.d(yml, "items." + items.get(random.nextInt(items.size())).path, random.nextInt(K != null ? K.getMaxTier() : E.getMaxTier())));
            }
            final FallenHeroSlainEvent e = new FallenHeroSlainEvent(event.getEntity().getKiller(), this, droppedGem);
            kits.pluginmanager.callEvent(e);
        }
        living.remove(fallenhero.getUniqueId());
        if(living.isEmpty()) {
            living = null;
            kits = null;
        }
    }

    public static void deleteAll() {
        living = null;
        kits = null;
    }
}
