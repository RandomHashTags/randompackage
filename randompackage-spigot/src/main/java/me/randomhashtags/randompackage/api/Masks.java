package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Mask;
import me.randomhashtags.randompackage.addon.file.FileMask;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.event.armor.ArmorUnequipEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import me.randomhashtags.randompackage.util.obj.EquippedCustomEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Masks implements RPFeatureSpigot {
    INSTANCE;

    public YamlConfiguration config;
    private HashMap<Player, ItemStack> equippedMasks;
    public ItemStack maskgenerator;
    private List<String> maskCanObtain;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.MASK;
    }

    @Override
    public void load() {
        save("masks", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "masks", "_settings.yml"));

        equippedMasks = new HashMap<>();
        maskgenerator = createItemStack(config, "items.generator");
        GivedpItem.INSTANCE.items.put("maskgenerator", maskgenerator);
        maskCanObtain = config.getStringList("items.generator.can obtain");

        if(!OTHER_YML.getBoolean("saved default masks")) {
            generateDefaultMasks();
            OTHER_YML.set("saved default masks", true);
            saveOtherData();
        }
        final List<ItemStack> list = new ArrayList<>();
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "masks")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileMask m = new FileMask(f);
                list.add(m.getItem());
            }
        }
        addGivedpCategory(list, UMaterial.PLAYER_HEAD_ITEM, "Masks", "Givedp: Masks");
    }
    @Override
    public void unload() {
        for(Player p : equippedMasks.keySet()) {
            p.getInventory().setHelmet(equippedMasks.get(p));
            p.updateInventory();
        }
    }

    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if(equippedMasks.containsKey(player)) {
            final PlayerInventory i = player.getInventory();
            final Mask m = valueOfMask(i.getHelmet());
            if(m != null) {
                CustomEnchants.INSTANCE.trigger(event, m.getAttributes());
                i.setHelmet(equippedMasks.get(player));
                player.updateInventory();
                equippedMasks.remove(player);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final int slot = event.getRawSlot();
        final String click = event.getClick().name();
        if(slot < 0 || !click.equals("RIGHT") && !event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
            return;
        }
        final ItemStack current = event.getCurrentItem();
        if(current == null || !current.getType().name().endsWith("HELMET")) {
            return;
        }
        final ItemStack cursor_item = event.getCursor();
        final Mask mask = valueOfMask(cursor_item), mask_on_item = getMaskOnItem(current);
        final Player player = (Player) event.getWhoClicked();
        ItemStack item = null;
        if(mask != null && mask_on_item == null) {
            event.setCancelled(true);
            final MaskApplyEvent e = new MaskApplyEvent(player, mask, current);
            PLUGIN_MANAGER.callEvent(e);
            apply(mask, current);
            item = mask.getItem();
            final int amount = item.getAmount() - cursor_item.getAmount();
            if(amount <= 0) {
                item = new ItemStack(Material.AIR);
            } else {
                item.setAmount(amount);
            }
            event.setCursor(item);
        } else if(click.equals("RIGHT") && mask_on_item != null) {
            item = current;
            final ItemMeta itemMeta = item.getItemMeta();
            final List<String> lore = itemMeta.getLore();
            lore.remove(mask_on_item.getAppliedString());
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            event.setCancelled(true);
            event.setCurrentItem(item);
            event.setCursor(mask_on_item.getItem());
        } else {
            return;
        }
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
    @EventHandler(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        tryToProcMask(event.getDamager(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void isDamagedEvent(isDamagedEvent event) {
        tryToProcMask(event.getEntity(), event);
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        if(player != null) {
            tryToProcMask(player, event);
        }
    }
    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        if(player != null) {
            tryToProcMask(player, event);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        tryToProcMask(event.getPlayer(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Mask mask = valueOfMask(event.getItemInHand());
        if(mask != null) {
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
        tryToProcMask(player, event);
        final ItemStack is = event.getItem();
        if(is != null && is.isSimilar(maskgenerator)) {
            event.setCancelled(true);
            removeItem(player, is, 1);
            final ItemStack mask = getMask(maskCanObtain.get(RANDOM.nextInt(maskCanObtain.size()))).getItem();
            final String playerName = player.getName(), maskName = mask.getItemMeta().getDisplayName();
            for(String s : getStringList(config, "items.generator.received msg")) {
                Bukkit.broadcastMessage(s.replace("{PLAYER}", playerName).replace("{MASK}", maskName));
            }
            giveItem(player, mask);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        final LivingEntity e = event.getEntity();
        if(e instanceof Player) {
            tryToProcMask((Player) e, event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void armorEquipEvent(ArmorEquipEvent event) {
        final Player player = event.getPlayer();
        if(!equippedMasks.containsKey(player)) {
            final ItemStack item = event.getItem();
            final Mask mask = getMaskOnItem(item);
            if(mask != null) {
                final MaskEquipEvent equip_event = new MaskEquipEvent(player, mask, item, event.getReason());
                PLUGIN_MANAGER.callEvent(equip_event);
                CustomEnchants.INSTANCE.trigger(equip_event, mask.getAttributes());
                if(!equip_event.isCancelled()) {
                    equippedMasks.put(player, item.clone());
                    SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                        player.getInventory().setHelmet(mask.getItem().clone());
                        player.updateInventory();
                    }, 0);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void armorUnequipEvent(ArmorUnequipEvent event) {
        final Player player = event.getPlayer();
        if(equippedMasks.containsKey(player)) {
            final ItemStack i = event.getItem();
            final Mask m = valueOfMask(i);
            if(m != null) {
                final MaskUnequipEvent e = new MaskUnequipEvent(player, m, equippedMasks.get(player), event.getReason());
                PLUGIN_MANAGER.callEvent(e);
                final CustomEnchants custom_enchants = CustomEnchants.INSTANCE;
                custom_enchants.trigger(e, m.getAttributes());
                if(!e.isCancelled()) {
                    final ItemStack h = e.helmet;
                    event.setCurrentItem(h);
                    equippedMasks.remove(player);
                    final EquippedCustomEnchants enchants = custom_enchants.getEnchants(player);
                    custom_enchants.triggerCustomEnchants(event, enchants, CustomEnchants.CUSTOM_ENCHANT_GLOBAL_ATTRIBUTES);
                }
            }
        }
    }

    public void apply(@NotNull Mask m, @NotNull ItemStack is) {
        final ItemMeta itemMeta = is.getItemMeta();
        final List<String> lore = new ArrayList<>();
        if(itemMeta.hasLore()) {
            lore.addAll(itemMeta.getLore());
        }
        lore.add(m.getAppliedString());
        itemMeta.setLore(lore); lore.clear();
        is.setItemMeta(itemMeta);
    }

    public void tryToProcMask(@NotNull Player player, Event event) {
        final ItemStack hel = player.getInventory().getHelmet();
        final Mask m = valueOfMask(hel);
        if(m != null) {
            CustomEnchants.INSTANCE.trigger(event, m.getAttributes());
        }
    }
}
