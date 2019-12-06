package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.addon.SoulTracker;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.addon.file.PathSoulTracker;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SoulTrackers extends RPFeature implements CommandExecutor {
    private static SoulTrackers instance;
    public static SoulTrackers getSoulTrackers() {
        if(instance == null) instance = new SoulTrackers();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "SOUL_TRACKERS"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && hasPermission(sender, "RandomPackage.splitsouls", true)) {
            splitsouls(player, args.length == 0 ? -1 : getRemainingInt(args[0]));
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "soul trackers.yml");
        config = getAddonConfig("soul trackers.yml");
        final ConfigurationSection cs = config.getConfigurationSection("soul trackers");
        if(cs != null) {
            final List<ItemStack> z = new ArrayList<>();
            for(String s : cs.getKeys(false)) {
                z.add(new PathSoulTracker(s).getItem());
            }
            addGivedpCategory(z, UMaterial.PAPER, "Soul Trackers", "Givedp: Soul Trackers");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " +  getAll(Feature.SOUL_TRACKER).size() + " Soul Trackers &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.SOUL_TRACKER);
    }

    public void applySoulTracker(Player player, ItemStack is, SoulTracker soultracker) {
        if(is != null && !is.getType().equals(Material.AIR)) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            boolean did = false;
            final String a = soultracker.getApplied(), ist = is.getType().name(), istl = ist.toLowerCase();
            final Collection<SoulTracker> trackers = getAllSoulTrackers().values();
            for(String s : soultracker.getAppliesTo()) {
                if(istl.endsWith(s.toLowerCase())) {
                    if(!lore.isEmpty()) {
                        for(int i = 0; i < lore.size(); i++) {
                            final String targetLore = lore.get(i);
                            for(SoulTracker st : trackers) {
                                if(!did && targetLore.startsWith(st.getApplied().replace("{SOULS}", ""))) {
                                    did = true;
                                    lore.set(i, a.replace("{SOULS}", "0"));
                                    break;
                                }
                            }
                        }
                        if(!did) {
                            did = true;
                            lore.add(a.replace("{SOULS}", "0"));
                        }
                    } else {
                        lore.add(a.replace("{SOULS}", "0"));
                        did = true;
                    }
                    break;
                }
            }
            if(did) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{ITEM}", ist);
                sendStringListMessage(player, soultracker.getApplyMsg(), replacements);
                itemMeta.setLore(lore); lore.clear();
                is.setItemMeta(itemMeta);
            }
        }
    }
    public void splitsouls(Player player, int amount) {
        item = getItemInHand(player);
        RarityGem g = valueOfRarityGem(item);
        int collectedsouls = 0, gems = 0;
        List<String> msg = null;
        SoulTracker appliedTracker = null;
        if(g != null) {
            msg = g.getSplitMsg();
            collectedsouls = getRemainingInt(item.getItemMeta().getDisplayName());
            if(collectedsouls <= 0 || amount > collectedsouls) {
                sendStringListMessage(player, getMessage(config, "messages.need to collect souls"), null);
                return;
            } else {
                gems = amount;
                item.setItemMeta(g.getItem(collectedsouls-gems).getItemMeta());
            }
        } else if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            sendStringListMessage(player, getMessage(config, "messages.need item with soul tracker"), null);
        } else {
            itemMeta = item.getItemMeta();
            lore.clear();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            boolean did = false;
            int appliedSlot = -1, totalsouls = -1;
            list: for(SoulTracker st : getAllSoulTrackers().values()) {
                int slot = -1;
                for(String s : lore) {
                    slot += 1;
                    final String a = st.getApplied();
                    if(s.startsWith(a.replace("{SOULS}", ""))) {
                        appliedTracker = st;
                        appliedSlot = slot;
                        msg = st.getSplitMsg();
                        collectedsouls = getRemainingInt(s);
                        totalsouls = collectedsouls;
                        if(amount == -1) {
                            amount = collectedsouls;
                        } else if(collectedsouls <= 0) {
                            sendStringListMessage(player, getMessage(config, "messages.need to collect souls"), null);
                            return;
                        } else {
                            collectedsouls = amount;
                        }
                        if(amount == 0)  {
                            sendStringListMessage(player, getMessage(config, "messages.need to collect souls"), null);
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
                    sendStringListMessage(player, getMessage(config, "messages.need to collect more souls"), null);
                    return;
                } else {
                    lore.set(appliedSlot, appliedTracker.getApplied().replace("{SOULS}", Integer.toString(totalsouls-amount)));
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                }
            }
        }
        if(msg != null) {
            if(g == null) g = appliedTracker.getConvertsTo();
            item = g.getItem(); itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(item.getItemMeta().getDisplayName().replace("{SOULS}", colorize(g.getColors(gems)) + gems));
            if(gems != 0) item.setAmount(1);
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
        final SoulTracker soultracker = valueOf(cursor);
        if(soultracker != null && current != null && !current.getType().equals(Material.AIR)) {
            final String n = current.getType().name();
            for(String s : soultracker.getAppliesTo()) {
                if(n.endsWith(s.toUpperCase())) {
                    item = current;
                    final Player player = (Player) event.getWhoClicked();
                    applySoulTracker(player, current, soultracker);
                    //playSuccess((Player) event.getWhoClicked());
                    event.setCancelled(true);
                    event.setCurrentItem(item);
                    final int a = cursor.getAmount();
                    if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                    else       cursor.setAmount(a-1);
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
            if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                final HashMap<Integer, SoulTracker> s = valueOfApplied(is);
                if(s != null) {
                    final SoulTracker tracker = (SoulTracker) s.values().toArray()[0];
                    final String t = tracker.getTracks();
                    if(t.equals("PLAYERS") && victim instanceof Player || t.equals("MOBS") && !(victim instanceof Player)) {
                        addSouls(killer, is, (int) s.keySet().toArray()[0], tracker);
                    }
                }
            }
        }
    }
    public void addSouls(Player player, ItemStack is, int loreSlot, SoulTracker tracker) {
        itemMeta = is.getItemMeta(); lore.clear();
        lore.addAll(itemMeta.getLore());
        lore.set(loreSlot, tracker.getApplied().replace("{SOULS}", Integer.toString(getRemainingInt(ChatColor.stripColor(lore.get(loreSlot)))+1)));
        itemMeta.setLore(lore); lore.clear();
        is.setItemMeta(itemMeta);
        player.updateInventory();
    }

    public SoulTracker valueOf(RarityGem gem) {
        for(SoulTracker st : getAllSoulTrackers().values()) {
            if(st.getConvertsTo().equals(gem)) {
                return st;
            }
        }
        return null;
    }
    public SoulTracker valueOf(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(SoulTracker s : getAllSoulTrackers().values()) {
                if(s.getItem().getItemMeta().equals(m)) {
                    return s;
                }
            }
        }
        return null;
    }
    public HashMap<Integer, SoulTracker> valueOfApplied(ItemStack is) {
        if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            final Collection<SoulTracker> trackers = getAllSoulTrackers().values();
            int slot = 0;
            for(String s : l) {
                for(SoulTracker t : trackers) {
                    final String a = t.getApplied().replace("{SOULS}", "");
                    if(s.startsWith(a)) {
                        final HashMap<Integer, SoulTracker> h = new HashMap<>();
                        h.put(slot, t);
                        return h;
                    }
                }
                slot++;
            }
        }
        return null;
    }
    public SoulTracker valueOf(String appliedlore) {
        for(SoulTracker st : getAllSoulTrackers().values()) {
            if(appliedlore.startsWith(st.getApplied().replace("{SOULS}", ""))) {
                return st;
            }
        }
        return null;
    }
}
