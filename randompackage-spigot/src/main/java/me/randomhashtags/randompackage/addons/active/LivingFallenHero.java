package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.api.Kits;
import me.randomhashtags.randompackage.events.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.usingfile.FileKitGlobal;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.addons.objects.KitItem;
import me.randomhashtags.randompackage.utils.interfaces.ILivingFallenHero;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LivingFallenHero implements ILivingFallenHero {
    public static HashMap<UUID, LivingFallenHero> living;
    private static Kits kits;

    private CustomKit kit;
    private FallenHero type;
    private UUID summoner;
    private LivingEntity fallenhero;
    private Location spawnedLocation;
    public LivingFallenHero(CustomKit kit, FallenHero type, UUID summoner, Location spawnedLocation) {
        if(living == null) {
            living = new HashMap<>();
            kits = Kits.getKits();
        }
        this.kit = kit;
        this.type = type;
        this.summoner = summoner;
        this.spawnedLocation = spawnedLocation;
        fallenhero = kits.getEntity(getFallenHero().getType(), getSpawnedLocation(), true);
        fallenhero.setCustomName(kit instanceof GlobalKit ? ((GlobalKit) kit).getFallenHeroName() : ((EvolutionKit) kit).getFallenHeroName());
        living.put(fallenhero.getUniqueId(), this);
    }
    public CustomKit getKit() { return kit; }
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
            final Player killer = event.getEntity().getKiller();
            if(droppedGem) {
                final HashMap<String, String> r = new HashMap<>();
                r.put("{PLAYER}", killer.getName());
                r.put("{NAME}", fallenhero.getCustomName());
                for(String s : type.getReceiveKitMsg()) {
                    for(String re : r.keySet()) {
                        s = s.replace(re, r.get(re));
                    }
                    Bukkit.broadcastMessage(s);
                }
                w.dropItem(fallenhero.getLocation(), kit instanceof FileKitGlobal ? (((GlobalKit) kit).getFallenHeroGem()) : ((EvolutionKit) kit).getFallenHeroGem());
            } else {
                final List<KitItem> items = kit.getItems();
                final boolean g = kit instanceof GlobalKit;
                final RPPlayer pdata = RPPlayer.get(killer.getUniqueId());
                final int lvl = g ? pdata.getKitLevel((GlobalKit) kit) : pdata.getKitLevel((EvolutionKit) kit);
                final YamlConfiguration yml = g ? kits.gkits : kits.vkits;
                final boolean enabled = yml.getBoolean("gui.settings.use tiers");
                final ItemStack is = kits.d(kit.getYaml(), "items." + items.get(random.nextInt(items.size())).path, enabled ? yml.getDouble("gui.settings.tier custom enchant multiplier." + lvl) : 0.00);
                if(is != null && !is.getType().equals(Material.AIR)) {
                    w.dropItem(fallenhero.getLocation(), is);
                }
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
