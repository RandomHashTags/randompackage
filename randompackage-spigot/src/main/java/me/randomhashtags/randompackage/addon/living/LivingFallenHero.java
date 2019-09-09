package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.event.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.addon.obj.KitItem;
import me.randomhashtags.randompackage.addon.util.ILivingFallenHero;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
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
        int pl = RPPlayer.get(summoner).getKitLevel(kit);
        pl = pl <= 0 ? 1 : pl;
        final EntityEquipment eq = fallenhero.getEquipment();
        for(KitItem i : kit.getItems()) {
            final ItemStack is = i.getItemStack(pl);
            final String n = is.getType().name();
            if(n.contains("HELMET")) {
                eq.setHelmet(is);
            } else if(n.contains("CHESTPLATE")) {
                eq.setChestplate(is);
            } else if(n.contains("LEGGINGS")) {
                eq.setLeggings(is);
            } else if(n.contains("BOOTS")) {
                eq.setBoots(is);
            } else if(n.contains("SWORD") || n.contains("_AXE")) {
                eq.setItemInHand(is);
            }
        }
        living.put(fallenhero.getUniqueId(), this);
    }
    public LivingEntity getEntity() { return fallenhero; }
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
                w.dropItem(fallenhero.getLocation(), kit.getFallenHeroItem(kit, false));
            } else {
                final List<KitItem> items = kit.getItems();
                final RPPlayer pdata = RPPlayer.get(killer.getUniqueId());
                int lvl = pdata.getKitLevel(kit);
                lvl = lvl <= 0 ? 1 : lvl;
                final KitItem ki = items.get(random.nextInt(items.size()));
                final ItemStack is = ki.getItemStack(killer.getName(), lvl, kit.getKitClass().getCustomEnchantLevelMultipliers().getOrDefault(lvl, 0f));
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
