package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Trinket;
import me.randomhashtags.randompackage.addon.file.FileTrinket;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trinkets extends EventAttributes implements RPItemStack {
    private static Trinkets instance;
    public static Trinkets getTrinkets() {
        if(instance == null) instance = new Trinkets();
        return instance;
    }

    public YamlConfiguration config;

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("trinkets", "_settings.yml");
        if(!OTHER_YML.getBoolean("saved default trinkets")) {
            generateDefaultTrinkets();
            OTHER_YML.set("saved default trinkets", true);
            saveOtherData();
        }

        final List<ItemStack> list = new ArrayList<>();
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "trinkets")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final Trinket trinket = new FileTrinket(f);
                if(trinket.isEnabled()) {
                    list.add(trinket.getItem(0));
                }
            }
        }
        addGivedpCategory(list, UMaterial.NETHER_STAR, "Trinkets", "Givedp: Trinkets");

        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "trinkets.yml"));
        sendConsoleDidLoadFeature(getAll(Feature.TRINKET).size() + " Trinkets", started);
    }
    @Override
    public void unload() {
        unregister(Feature.TRINKET);
    }

    public boolean isTrinket(ItemStack is) {
        return getRPItemStackValue(is, "TrinketInfo") != null;
    }
    @NotNull
    public HashMap<Trinket, String> getTrinketsApplied(ItemStack is) {
        final HashMap<Trinket, String> info = new HashMap<>();
        final String value = getRPItemStackValue(is, "TrinketInfo");
        if(value != null) {
            final String[] values = value.split(":");
            info.put(getTrinket(values[0]), value);
        }
        return info;
    }
    private void triggerPassive(Event event, Player player) {
        for(ItemStack is : player.getInventory().getContents()) {
            if(is != null) {
                final HashMap<Trinket, String> trinkets = getTrinketsApplied(is);
                for(Trinket trinket : trinkets.keySet()) {
                    final String s = trinket.getSetting("passive");
                    if(s != null && s.equalsIgnoreCase("true") && trigger(event, trinket.getAttributes())) {
                        trinket.didUse(is, trinket.getIdentifier());
                    }
                }
            }
        }
    }
    private byte didTriggerTrinket(Event event, ItemStack is, Player player) {
        final String info = getRPItemStackValue(is, "TrinketInfo");
        if(info != null) {
            final String[] values = info.split(":");
            final String identifier = values[0];
            final Trinket trinket = getTrinket(identifier);
            final long expiration = Long.parseLong(values[1]), time = System.currentTimeMillis(), remainingtime = expiration-time;

            if(remainingtime <= 0) {
                if(trigger(event, trinket.getAttributes())) {
                    trinket.didUse(is, identifier);
                    return 1;
                }
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TIME}", getRemainingTime(remainingtime));
                sendStringListMessage(player, getStringList(config, "messages.on cooldown"), replacements);
                return 0;
            }
        }
        return -1;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        triggerPassive(event, player);
        final int id = didTriggerTrinket(event, event.getItem(), player);
        if(id >= 0) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void isDamagedEvent(isDamagedEvent event) {
        triggerPassive(event, event.getEntity());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        triggerPassive(event, event.getDamager());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageEvent(EntityDamageEvent event) {
        final Entity e = event.getEntity();
        if(e instanceof Player) {
            triggerPassive(event, (Player) e);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void projectileLaunchEvent(ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        final ProjectileSource source = projectile.getShooter();
        if(source instanceof Player) {
            final Player player = (Player) source;
            triggerPassive(event, player);
            didTriggerTrinket(event, player.getItemInHand(), player);
        }
    }
}
