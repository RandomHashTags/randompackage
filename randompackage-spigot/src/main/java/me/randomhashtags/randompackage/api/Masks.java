package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.Mask;
import me.randomhashtags.randompackage.utils.EventAttributes;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.addons.FileMask;
import me.randomhashtags.randompackage.events.*;
import me.randomhashtags.randompackage.events.customenchant.*;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.utils.listeners.GivedpItem.givedpitem;

public class Masks extends EventAttributes implements Listener {
    private static Masks instance;
    public static Masks getMasks() {
        if(instance == null) instance = new Masks();
        return instance;
    }

    public YamlConfiguration config;
    private HashMap<Player, ItemStack> equippedMasks;
    public ItemStack maskgenerator;
    private List<String> maskCanObtain;

    public String getIdentifier() { return "MASKS"; }
    protected RPFeature getFeature() { return getMasks(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "masks.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "masks.yml"));

        equippedMasks = new HashMap<>();
        maskgenerator = d(config, "items.generator");
        givedpitem.items.put("maskgenerator", maskgenerator);
        maskCanObtain = config.getStringList("items.generator.can obtain");

        final ArrayList<ItemStack> ms = new ArrayList<>();
        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default masks")) {
            final String[] m = new String[] {
                    "BUNNY", "DEATH_KNIGHT", "DRAGON", "DUNGEON", "GHOST", "GLITCH", "HEADLESS", "JOKER",
                    "LOVER", "MONOPOLY", "NECROMANCER", "PARTY_HAT", "PILGRIM", "PUMPKIN_MONSTER",
                    "PURGE", "REINDEER", "RIFT", "SANTA", "SCARECROW", "SPECTRAL", "TURKEY", "ZEUS"
            };
            for(String s : m) save("masks", s + ".yml");
            a.set("saved default masks", true);
            saveOtherData();
        }
        final File folder = new File(rpd + separator + "masks");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final FileMask m = new FileMask(f);
                ms.add(m.getItem());
            }
        }
        addGivedpCategory(ms, UMaterial.PLAYER_HEAD_ITEM, "Masks", "Givedp: Masks");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (masks != null ? masks.size() : 0) + " Masks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player p : equippedMasks.keySet()) {
            p.getInventory().setHelmet(equippedMasks.get(p));
            p.updateInventory();
        }
        masks = null;
    }

    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if(equippedMasks.containsKey(player)) {
            player.getInventory().setHelmet(equippedMasks.get(player));
            player.updateInventory();
            equippedMasks.remove(player);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final int r = event.getRawSlot();
        final String click = event.getClick().name();
        if(r < 0 || !click.equals("RIGHT") && !event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
        final ItemStack current = event.getCurrentItem();
        if(current == null || !current.getType().name().endsWith("HELMET")) return;
        final ItemStack mask = event.getCursor();
        final Mask m = valueOfMask(mask), onitem = getMaskOnItem(current);
        final Player player = (Player) event.getWhoClicked();
        if(m != null && onitem == null) {
            event.setCancelled(true);
            final MaskApplyEvent e = new MaskApplyEvent(player, m, current);
            pluginmanager.callEvent(e);
            apply(m, current);
            item = m.getItem();
            final int a = item.getAmount()-mask.getAmount();
            if(a <= 0) item = new ItemStack(Material.AIR);
            else       item.setAmount(a);
            event.setCursor(item);
        } else if(click.equals("RIGHT") && onitem != null) {
            item = current; itemMeta = item.getItemMeta(); lore.clear();
            lore.addAll(itemMeta.getLore());
            lore.remove(onitem.getApplied());
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            event.setCancelled(true);
            event.setCurrentItem(item);
            event.setCursor(onitem.getItem());
        } else return;
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageEvent(EntityDamageEvent event) {
        final Player victim = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        final ItemStack i = equippedMasks.getOrDefault(victim, null);
        if(victim != null && i != null) {
            final PlayerInventory vi = victim.getInventory();
            final Mask m = valueOfMask(vi.getHelmet());
            if(m != null) {
                vi.setHelmet(i);
                victim.updateInventory();
                equippedMasks.remove(victim);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void pvAnyEvent(PvAnyEvent event) {
        tryToProcMask(event.damager, event);
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void isDamagedEvent(isDamagedEvent event) {
        tryToProcMask(event.victim, event);
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        if(!(e instanceof Player)) tryToProcMask(e.getKiller(), event);
    }
    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
        tryToProcMask(event.getEntity().getKiller(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        tryToProcMask(event.getPlayer(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Mask m = valueOfMask(event.getItemInHand());
        if(m != null) {
            event.setCancelled(true);
            player.updateInventory();
        } else {
            tryToProcMask(player, event);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        tryToProcMask((Player) event.getEntity(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if(!event.isCancelled()) tryToProcMask(player, event);
        final ItemStack is = event.getItem();
        if(is != null && is.isSimilar(maskgenerator)) {
            event.setCancelled(true);
            removeItem(player, is, 1);
            final ItemStack mask = getMask(maskCanObtain.get(random.nextInt(maskCanObtain.size()))).getItem();
            for(String s : config.getStringList("items.generator.received msg")) {
                Bukkit.broadcastMessage(s.replace("{PLAYER}", player.getName()).replace("{MASK}", ChatColor.stripColor(mask.getItemMeta().getDisplayName())));
            }
            giveItem(player, mask);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        final Entity e = event.getEntity();
        if(e instanceof Player)
            tryToProcMask((Player) e, event);
    }

    public void tryToProcMask(Player player, Event event) {
        if(player != null && event != null) {
            final ItemStack hel = player.getInventory().getHelmet();
            final Mask m = valueOfMask(hel), mm = m == null ? getMaskOnItem(hel) : null;
            if(m != null) procMaskAttributes(player, event, m);
            else if(mm != null) procMaskAttributes(player, event, mm);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerArmorEvent(PlayerArmorEvent event) {
        final PlayerArmorEvent.ArmorEventReason reason = event.reason;
        final String r = reason.name();
        final Player player = event.player;
        final boolean contains = equippedMasks.containsKey(player);
        final ItemStack i = event.getItem().clone(), o = contains ? equippedMasks.get(player) : new ItemStack(Material.AIR);
        if(!contains && r.contains("_EQUIP")) {
            final Mask m = getMaskOnItem(i);
            if(m != null) {
                final MaskEquipEvent e = new MaskEquipEvent(player, m, o, reason);
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    equippedMasks.put(player, i);
                    procMaskAttributes(player, e, m);
                    scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                        player.getInventory().setHelmet(m.getItem().clone());
                        player.updateInventory();
                    }, 0);
                }
            } else return;
        } else if(contains && r.contains("_UNEQUIP")) {
            final Mask m = valueOfMask(i);
            if(m != null) {
                final MaskUnequipEvent e = new MaskUnequipEvent(player, m, o, reason);
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    final ItemStack h = e.helmet;
                    event.setCurrentItem(h);
                    equippedMasks.remove(player);
                    procMaskAttributes(player, e, e.mask);
                    procPlayerItem(event, player, h);
                }
            } else return;
        } else return;
        player.updateInventory();
    }

    public void apply(Mask m, ItemStack is) {
        if(m != null && is != null) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            lore.add(m.getApplied());
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
        }
    }
}
