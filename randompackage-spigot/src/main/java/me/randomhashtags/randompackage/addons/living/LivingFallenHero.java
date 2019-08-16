package me.randomhashtags.randompackage.addons.living;

import me.randomhashtags.randompackage.addons.FallenHero;
import me.randomhashtags.randompackage.addons.Kits;
import me.randomhashtags.randompackage.events.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.addons.objects.KitItem;
import me.randomhashtags.randompackage.addons.utils.ILivingFallenHero;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LivingFallenHero implements ILivingFallenHero {
    public static HashMap<UUID, LivingFallenHero> living;

    private CustomKit kit;
    private Kits kitclass;
    private FallenHero type;
    private UUID summoner;
    private LivingEntity fallenhero;
    private Location spawnedLocation;
    public LivingFallenHero(CustomKit kit, FallenHero type, UUID summoner, Location spawnedLocation) {
        if(living == null) {
            living = new HashMap<>();
        }
        this.kit = kit;
        kitclass = kit.getKitClass();
        this.type = type;
        this.summoner = summoner;
        this.spawnedLocation = spawnedLocation;
        fallenhero = kitclass.getEntity(getFallenHero().getType(), getSpawnedLocation(), true);
        fallenhero.setCustomName(kit.getFallenHeroName());
        living.put(fallenhero.getUniqueId(), this);
    }
    public CustomKit getKit() { return kit; }
    public Kits getKitClass() { return kitclass; }
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
                r.put("{NAME}", kit.getItem().getItemMeta().getDisplayName());
                for(String s : type.getReceiveKitMsg()) {
                    for(String re : r.keySet()) {
                        s = s.replace(re, r.get(re));
                    }
                    Bukkit.broadcastMessage(s);
                }
                w.dropItem(fallenhero.getLocation(), kit.getFallenHeroGemItem(kit));
            } else {
                final List<KitItem> items = kit.getItems();
                final RPPlayer pdata = RPPlayer.get(killer.getUniqueId());
                final int lvl = pdata.getKitLevel(kit);
                final ItemStack is = kitclass.d(kit.getYaml(), "items." + items.get(random.nextInt(items.size())).path, kitclass.usesTiers() ? kitclass.getTierCustomEnchantMultiplier().getOrDefault(lvl, 0.00) : 0.00);
                if(is != null && !is.getType().equals(Material.AIR)) {
                    w.dropItem(fallenhero.getLocation(), is);
                }
            }
            final FallenHeroSlainEvent e = new FallenHeroSlainEvent(event.getEntity().getKiller(), this, droppedGem);
            kitclass.pluginmanager.callEvent(e);
        }
        living.remove(fallenhero.getUniqueId());
        if(living.isEmpty()) {
            living = null;
        }
    }

    public static void deleteAll() {
        living = null;
    }
}
