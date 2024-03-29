package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.addon.SoulTracker;
import me.randomhashtags.randompackage.addon.file.PathSoulTracker;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.perms.SoulTrackerPermission;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum SoulTrackers implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.SOUL_TRACKER;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && hasPermission(sender, SoulTrackerPermission.COMMAND_SPLITSOULS, true)) {
            splitsouls(player, args.length == 0 ? -1 : getRemainingInt(args[0]));
        }
        return true;
    }

    @Override
    public void load() {
        save("addons", "soul trackers.yml");
        config = getAddonConfig("soul trackers.yml");
        final List<ItemStack> list = new ArrayList<>();
        for(String s : getConfigurationSectionKeys(config, "soul trackers", false)) {
            list.add(new PathSoulTracker(s).getItem());
        }
        addGivedpCategory(list, UMaterial.PAPER, "Soul Trackers", "Givedp: Soul Trackers");
    }
    @Override
    public void unload() {
    }

    public void applySoulTracker(@NotNull Player player, @NotNull ItemStack is, @NotNull SoulTracker soultracker) {
        if(!is.getType().equals(Material.AIR)) {
            final ItemMeta itemMeta = is.getItemMeta();
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            boolean did = false;
            final String applied = soultracker.getAppliedString(), material = is.getType().name();
            final Collection<SoulTracker> trackers = getAllSoulTrackers().values();
            for(String targetMaterial : soultracker.getAppliesTo()) {
                if(material.endsWith(targetMaterial)) {
                    if(!lore.isEmpty()) {
                        for(int i = 0; i < lore.size(); i++) {
                            final String targetLore = lore.get(i);
                            for(SoulTracker tracker : trackers) {
                                if(targetLore.startsWith(tracker.getAppliedString().replace("{SOULS}", ""))) {
                                    lore.set(i, applied.replace("{SOULS}", "0"));
                                    itemMeta.setLore(lore);
                                    is.setItemMeta(itemMeta);
                                    return;
                                }
                            }
                        }
                    }
                    did = true;
                    lore.add(applied.replace("{SOULS}", "0"));
                    break;
                }
            }
            if(did) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{ITEM}", material);
                sendStringListMessage(player, soultracker.getApplyMsg(), replacements);
                itemMeta.setLore(lore);
                is.setItemMeta(itemMeta);
            }
        }
    }
    public void splitsouls(@NotNull Player player, int amount) {
        ItemStack item = getItemInHand(player);
        ItemMeta itemMeta = null;
        RarityGem gem = valueOfRarityGem(item);
        int collectedsouls = 0, gems = 0;
        List<String> msg = null;
        SoulTracker appliedTracker = null;
        if(gem != null) {
            msg = gem.getSplitMsg();
            collectedsouls = getRemainingInt(item.getItemMeta().getDisplayName());
            if(collectedsouls <= 0 || amount > collectedsouls) {
                sendStringListMessage(player, getStringList(config, "messages.need to collect souls"), null);
                return;
            } else {
                gems = amount;
                item.setItemMeta(gem.getItem(collectedsouls-gems).getItemMeta());
            }
        } else if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            sendStringListMessage(player, getStringList(config, "messages.need item with soul tracker"), null);
        } else {
            itemMeta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            boolean did = false;
            int appliedSlot = -1, totalsouls = -1;
            list: for(SoulTracker st : getAllSoulTrackers().values()) {
                int slot = -1;
                for(String string : lore) {
                    slot += 1;
                    final String a = st.getAppliedString();
                    if(string.startsWith(a.replace("{SOULS}", ""))) {
                        appliedTracker = st;
                        appliedSlot = slot;
                        msg = st.getSplitMsg();
                        collectedsouls = getRemainingInt(string);
                        totalsouls = collectedsouls;
                        if(amount == -1) {
                            amount = collectedsouls;
                        } else if(collectedsouls <= 0) {
                            sendStringListMessage(player, getStringList(config, "messages.need to collect souls"), null);
                            return;
                        } else {
                            collectedsouls = amount;
                        }
                        if(amount == 0)  {
                            sendStringListMessage(player, getStringList(config, "messages.need to collect souls"), null);
                            return;
                        }
                        gems = (int) (collectedsouls * st.getSoulsCollected());
                        did = true;
                        break list;
                    }
                }
            }
            if(did) {
                if(totalsouls-amount < 0) {
                    sendStringListMessage(player, getStringList(config, "messages.need to collect more souls"), null);
                    return;
                } else {
                    lore.set(appliedSlot, appliedTracker.getAppliedString().replace("{SOULS}", Integer.toString(totalsouls-amount)));
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                }
            }
        }
        if(msg != null) {
            if(gem == null) {
                gem = appliedTracker.getConvertsTo();
            }
            item = gem.getItem();
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(item.getItemMeta().getDisplayName().replace("{SOULS}", colorize(gem.getColors(gems)) + gems));
            if(gems != 0) {
                item.setAmount(1);
            }
            item.setItemMeta(itemMeta);
            giveItem(player, item);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{SOULS}", Integer.toString(collectedsouls));
            replacements.put("{GEMS}", Integer.toString(gems));
            replacements.put("{AMOUNT}", Integer.toString(gems));
            sendStringListMessage(player, msg, replacements);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        final SoulTracker soultracker = valueOfSoulTracker(cursor);
        if(soultracker != null && current != null && !current.getType().equals(Material.AIR)) {
            final String material = current.getType().name();
            for(String allowedMaterial : soultracker.getAppliesTo()) {
                if(material.endsWith(allowedMaterial.toUpperCase())) {
                    final Player player = (Player) event.getWhoClicked();
                    applySoulTracker(player, current, soultracker);
                    //playSuccess((Player) event.getWhoClicked());
                    event.setCancelled(true);
                    event.setCurrentItem(current);
                    final int amount = cursor.getAmount();
                    if(amount == 1) {
                        event.setCursor(new ItemStack(Material.AIR));
                    } else {
                        cursor.setAmount(amount-1);
                    }
                    player.updateInventory();
                    break;
                }
            }
        }
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity victim = event.getEntity();
        final Player killer = victim.getKiller();
        if(killer != null) {
            final ItemStack is = killer.getItemInHand();
            if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
                final HashMap<Integer, SoulTracker> appliedTracker = valueOfSoulTrackerApplied(is);
                if(appliedTracker != null) {
                    final SoulTracker tracker = (SoulTracker) appliedTracker.values().toArray()[0];
                    final String tracks = tracker.getTracks();
                    if(tracks.equals("PLAYERS") && victim instanceof Player
                            || tracks.equals("MOBS") && !(victim instanceof Player)) {
                        addSouls(killer, is, (int) appliedTracker.keySet().toArray()[0], tracker);
                    }
                }
            }
        }
    }
    public void addSouls(@NotNull Player player, @NotNull ItemStack is, int loreSlot, @NotNull SoulTracker tracker) {
        final ItemMeta itemMeta = is.getItemMeta();
        final List<String> lore = new ArrayList<>(itemMeta.getLore());
        lore.set(loreSlot, tracker.getAppliedString().replace("{SOULS}", Integer.toString(getRemainingInt(ChatColor.stripColor(lore.get(loreSlot)))+1)));
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        player.updateInventory();
    }
}
