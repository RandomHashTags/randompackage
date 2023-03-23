package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.event.mob.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.addon.CustomKit;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class LivingFallenHero implements ILivingFallenHero {
    public static HashMap<UUID, LivingFallenHero> LIVING;

    private final CustomKit kit;
    private final Kits kitclass;
    private final FallenHero type;
    private final UUID summoner;
    private final LivingEntity fallenhero;
    private final Location spawnedLocation;

    public LivingFallenHero(CustomKit kit, FallenHero type, UUID summoner, Location spawnedLocation) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }
        this.kit = kit;
        kitclass = kit.getKitClass();
        this.type = type;
        this.summoner = summoner;
        this.spawnedLocation = spawnedLocation;
        fallenhero = kitclass.getEntity(getFallenHero().getType(), getSpawnedLocation(), true);
        fallenhero.setCustomName(kit.getFallenHeroName());
        int player_kit_level = FileRPPlayer.get(summoner).getKitData().getLevel(kit);
        player_kit_level = player_kit_level <= 0 ? 1 : player_kit_level;
        final EntityEquipment fallen_hero_equipment = fallenhero.getEquipment();
        for(KitItem i : kit.getItems()) {
            final ItemStack is = i.getItemStack(player_kit_level);
            final String materialName = is.getType().name();
            if(materialName.contains("HELMET")) {
                fallen_hero_equipment.setHelmet(is);
            } else if(materialName.contains("CHESTPLATE")) {
                fallen_hero_equipment.setChestplate(is);
            } else if(materialName.contains("LEGGINGS")) {
                fallen_hero_equipment.setLeggings(is);
            } else if(materialName.contains("BOOTS")) {
                fallen_hero_equipment.setBoots(is);
            } else if(materialName.contains("SWORD") || materialName.contains("_AXE")) {
                fallen_hero_equipment.setItemInHand(is);
            }
        }
        LIVING.put(fallenhero.getUniqueId(), this);
    }
    public LivingEntity getEntity() {
        return fallenhero;
    }
    public CustomKit getKit() {
        return kit;
    }
    public Kits getKitClass() {
        return kitclass;
    }
    public FallenHero getFallenHero() {
        return type;
    }
    public UUID getSummoner() {
        return summoner;
    }
    public Location getSpawnedLocation() {
        return spawnedLocation;
    }

    public void delete() {
        fallenhero.remove();
        killed(null);
    }
    public void killed(@Nullable EntityDeathEvent event) {
        if(event != null) {
            event.setDroppedExp(0);
            event.getDrops().clear();

            final World w = fallenhero.getWorld();
            final Random random = new Random();
            final boolean droppedGem = random.nextInt(100) <= type.getGemDropChance();
            final Player killer = event.getEntity().getKiller();
            if(droppedGem) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", killer.getName());
                replacements.put("{NAME}", kit.getItem().getItemMeta().getDisplayName());
                for(String string : type.getReceiveKitMsg()) {
                    for(String re : replacements.keySet()) {
                        string = string.replace(re, replacements.get(re));
                    }
                    Bukkit.broadcastMessage(string);
                }
                w.dropItem(fallenhero.getLocation(), kit.getFallenHeroItem(kit, false));
            } else {
                final List<KitItem> items = kit.getItems();
                final FileRPPlayer pdata = FileRPPlayer.get(killer.getUniqueId());
                int lvl = pdata.getKitData().getLevel(kit);
                lvl = lvl <= 0 ? 1 : lvl;
                final KitItem ki = items.get(random.nextInt(items.size()));
                final ItemStack is = ki.getItemStack(killer.getName(), lvl, kit.getKitClass().getCustomEnchantLevelMultipliers().getOrDefault(lvl, 0f));
                if(is != null && !is.getType().equals(Material.AIR)) {
                    w.dropItem(fallenhero.getLocation(), is);
                }
            }
            final FallenHeroSlainEvent slainEvent = new FallenHeroSlainEvent(event.getEntity().getKiller(), this, droppedGem);
            kitclass.PLUGIN_MANAGER.callEvent(slainEvent);
        }
        LIVING.remove(fallenhero.getUniqueId());
        if(LIVING.isEmpty()) {
            LIVING = null;
        }
    }

    public static void deleteAll() {
        LIVING = null;
    }
}
