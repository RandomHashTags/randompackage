package me.randomhashtags.randompackage.util.listener;

import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.event.FoodLevelLostEvent;
import me.randomhashtags.randompackage.event.PlayerExpGainEvent;
import me.randomhashtags.randompackage.event.armor.*;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum RPEvents implements RPFeature {
    INSTANCE;

    @Override
    public String getIdentifier() {
        return "RP_EVENTS";
    }
    @Override
    public void load() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            FileRPPlayer.get(p.getUniqueId());
        }
    }
    @Override
    public void unload() {
        backup();
    }

    public void backup() {
        for(FileRPPlayer p : FileRPPlayer.PLAYERS.values()) {
            p.backup();
        }
    }

    @EventHandler
    private void playerJoinEvent(PlayerJoinEvent event) {
        FileRPPlayer.get(event.getPlayer().getUniqueId());
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        FileRPPlayer.get(event.getPlayer().getUniqueId()).unload();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack cursoritem = event.getCursor(), currentitem = event.getCurrentItem();
        if(!event.getClick().equals(ClickType.DOUBLE_CLICK) && currentitem != null && cursoritem != null && event.getInventory().getType().equals(InventoryType.CRAFTING)) {
            final Player player = (Player) event.getWhoClicked();
            final InventoryType.SlotType slotType = event.getSlotType();
            final ClickType clickType = event.getClick();
            final PlayerInventory inv = player.getInventory();
            if((slotType.equals(InventoryType.SlotType.QUICKBAR) || slotType.equals(InventoryType.SlotType.CONTAINER)) && clickType.equals(ClickType.CONTROL_DROP)) return;

            ArmorUnequipEvent unequip = null;
            ArmorEquipEvent equip = null;
            final String cursor = cursoritem.getType().name(), current = currentitem.getType().name();
            final int rawslot = event.getRawSlot(), targetSlot = getTargetSlot(current);
            final EquipmentSlot equipmentSlot = getRespectiveSlot(current);
            final boolean slotTypeIsArmor = slotType.equals(InventoryType.SlotType.ARMOR);

            if(slotTypeIsArmor) {
                if(clickType == ClickType.NUMBER_KEY) {
                    final ItemStack prev = inv.getItem(event.getSlot()), hotbarItem = inv.getItem(event.getHotbarButton());
                    final String t = hotbarItem != null ? hotbarItem.getType().name() : "AIR";
                    if(prev != null && !prev.getType().name().equals("AIR")) {
                        unequip = new ArmorUnequipEvent(player, equipmentSlot, ArmorEventReason.NUMBER_KEY_UNEQUIP, prev);
                    }
                    if(canBeUsed(rawslot, t)) {
                        equip = new ArmorEquipEvent(player, equipmentSlot, ArmorEventReason.NUMBER_KEY_EQUIP, hotbarItem);
                    }
                } else if(clickType.name().contains("DROP") && !current.equals("AIR")) {
                    unequip = new ArmorUnequipEvent(player, equipmentSlot, ArmorEventReason.DROP, currentitem);
                } else if(clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
                    final int cursorTargetSlot = getTargetSlot(cursor);
                    if(!current.equals("AIR") && (targetSlot == cursorTargetSlot || rawslot == targetSlot)) {
                        unequip = new ArmorUnequipEvent(player, equipmentSlot, ArmorEventReason.INVENTORY_UNEQUIP, currentitem);
                    }
                    if(!cursor.equals("AIR") && (targetSlot == cursorTargetSlot || rawslot == cursorTargetSlot)) {
                        equip = new ArmorEquipEvent(player, equipmentSlot, ArmorEventReason.INVENTORY_EQUIP, cursoritem);
                    }
                }
            }
            final boolean alreadyDid = unequip != null || equip != null;

            if(targetSlot == -1 && !alreadyDid) {
                return;
            }

            if(!alreadyDid) {
                if(event.isShiftClick()) {
                    if(slotTypeIsArmor) {
                        unequip = new ArmorUnequipEvent(player, equipmentSlot, ArmorEventReason.SHIFT_UNEQUIP, currentitem);
                    } else {
                        final ItemStack prevArmor = inv.getArmorContents()[targetSlot == 5 ? 3 : targetSlot == 6 ? 2 : targetSlot == 7 ? 1 : 0];
                        if((prevArmor == null || prevArmor.getType().equals(Material.AIR)) && canBeUsed(targetSlot, current)) {
                            equip = new ArmorEquipEvent(player, equipmentSlot, ArmorEventReason.SHIFT_EQUIP, currentitem);
                        }
                    }
                } else {
                    return;
                }
            }

            boolean update = false;
            if(unequip != null) {
                PLUGIN_MANAGER.callEvent(unequip);
                if(!unequip.isCancelled()) {
                    update = true;
                    final ItemStack x = unequip.getCurrentItem(), y = unequip.getCursor();
                    if(x != null) event.setCurrentItem(x);
                    if(y != null) event.setCursor(y);
                }
            }
            if(equip != null) {
                PLUGIN_MANAGER.callEvent(equip);
                if(!equip.isCancelled()) {
                    update = true;
                    final ItemStack x = equip.getCurrentItem(), y = equip.getCursor();
                    if(x != null) event.setCurrentItem(x);
                    if(y != null) event.setCursor(y);
                }
            }
            if(update) {
                SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                    player.updateInventory();
                });
            }
        }
    }
    private int getTargetSlot(String target) {
        return target.contains("HELMET") || target.contains("SKULL") || target.contains("HEAD") ? 5
                : target.contains("CHESTPLATE") || target.contains("ELYTRA") ? 6
                : target.contains("LEGGINGS") ? 7
                : target.contains("BOOTS") ? 8
                : -1;
    }
    private boolean canBeUsed(int rawslot, String target) {
        return rawslot == 5 && (target.contains("HELMET") || target.contains("SKULL") || target.contains("HEAD"))
                || rawslot == 6 && (target.contains("CHESTPLATE") || target.contains("ELYTRA"))
                || rawslot == 7 && target.contains("LEGGINGS")
                || rawslot == 8 && target.contains("BOOTS");
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && event.getAction().name().contains("RIGHT")) {
            final String item = is.getType().name();
            final EquipmentSlot slot = getRespectiveSlot(item);
            final boolean helmet = item.endsWith("HELMET"), chestplate = item.endsWith("CHESTPLATE"), leggings = item.endsWith("LEGGINGS"), boots = item.endsWith("BOOTS");
            if(!helmet && !chestplate && !leggings && !boots) return;
            final Player player = event.getPlayer();
            final PlayerInventory PI = player.getInventory();
            final ItemStack  h = PI.getHelmet(), c = PI.getChestplate(), l = PI.getLeggings(), b = PI.getBoots();
            if(helmet && h == null || chestplate && c == null || leggings && l == null || boots && b == null) {
                final Block block = event.getClickedBlock();
                if(block == null || !isInteractable(block.getType())) {
                    final ArmorEquipEvent e = new ArmorEquipEvent(player, slot, ArmorEventReason.HOTBAR_EQUIP, is);
                    PLUGIN_MANAGER.callEvent(e);
                }
            } else {
                final ArmorEvent e = new ArmorSwapEvent(player, slot, ArmorEventReason.HOTBAR_SWAP, is, helmet ? h : chestplate ? c : leggings ? l : b);
                PLUGIN_MANAGER.callEvent(e);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerItemBreakEvent(PlayerItemBreakEvent event) {
        final ItemStack is = event.getBrokenItem();
        final String i = is.getType().name();
        if(i.endsWith("HELMET") || i.endsWith("CHESTPLATE") || i.endsWith("LEGGINGS") || i.endsWith("BOOTS")) {
            final ArmorPieceBreakEvent e = new ArmorPieceBreakEvent(event.getPlayer(), getRespectiveSlot(i), is);
            PLUGIN_MANAGER.callEvent(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerExpChangeEvent(PlayerExpChangeEvent event) {
        final int amount = event.getAmount();
        if(amount > 0) {
            final PlayerExpGainEvent e = new PlayerExpGainEvent(event.getPlayer(), amount);
            PLUGIN_MANAGER.callEvent(e);
            if(!e.isCancelled()) {
                event.setAmount(e.getAmount());
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        final int l = player.getFoodLevel(), lvl = event.getFoodLevel();
        if(l > lvl) {
            final FoodLevelLostEvent e = new FoodLevelLostEvent(player, player.getFoodLevel(), lvl);
            e.setCancelled(event.isCancelled());
            PLUGIN_MANAGER.callEvent(e);
        }
    }
}
