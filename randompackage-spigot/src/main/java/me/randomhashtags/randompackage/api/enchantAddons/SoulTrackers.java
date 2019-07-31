package me.randomhashtags.randompackage.api.enchantAddons;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.SoulTracker;
import me.randomhashtags.randompackage.addons.usingpath.PathSoulTracker;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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

public class SoulTrackers extends CustomEnchantUtils implements CommandExecutor {
    private static SoulTrackers instance;
    public static SoulTrackers getSoulTrackers() {
        if(instance == null) instance = new SoulTrackers();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && hasPermission(sender, "RandomPackage.splitsouls", true)) {
            splitsouls(player, args.length == 0 ? -1 : getRemainingInt(args[0]));
        }
        return true;
    }

    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
        save("addons", "soul trackers.yml");
        final ConfigurationSection cs = getAddonConfig("soul trackers.yml").getConfigurationSection("soul trackers");
        if(cs != null) {
            final List<ItemStack> z = new ArrayList<>();
            for(String s : cs.getKeys(false)) {
                z.add(new PathSoulTracker(s).getItem());
            }
            addGivedpCategory(z, UMaterial.PAPER, "Soul Trackers", "Givedp: Soul Trackers");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " +  (soultrackers != null ? soultrackers.size() : 0) + " Soul Trackers &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        instance = null;
        deleteAll(Feature.SOUL_TRACKERS);
        unloadUtils();
    }

    public void applySoulTracker(Player player, ItemStack is, SoulTracker soultracker) {
        if(is != null && !is.getType().equals(Material.AIR) && soultrackers != null) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            boolean did = false;
            final String a = soultracker.getApplied(), ist = is.getType().name(), istl = ist.toLowerCase();
            final Collection<SoulTracker> trackers = soultrackers.values();
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
        if(soultrackers != null) {
            item = getItemInHand(player);
            RarityGem g = valueOfRarityGem(item);
            int collectedsouls = 0, gems = 0;
            List<String> split = null;
            SoulTracker appliedst = null;
            if(g != null) {
                split = g.getSplitMsg();
                collectedsouls = getRemainingInt(item.getItemMeta().getDisplayName());
            } else if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                sendStringListMessage(player, config.getStringList("messages.need item with soul tracker"), null);
            } else {
                itemMeta = item.getItemMeta();
                lore.clear();
                if(itemMeta.hasLore())
                    lore.addAll(itemMeta.getLore());
                boolean did = false;
                int applied = -1, totalsouls = -1;
                for(SoulTracker st : soultrackers.values()) {
                    int i = -1;
                    if(!did) {
                        for(String s : lore) {
                            i += 1;
                            final String a = st.getApplied();
                            if(s.startsWith(a.replace("{SOULS}", ""))) {
                                appliedst = st;
                                applied = i;
                                split = st.getSplitMsg();
                                collectedsouls = getRemainingInt(s);
                                totalsouls = collectedsouls;
                                if(amount == -1) {
                                    amount = collectedsouls;
                                } else if(collectedsouls <= 0) {
                                    sendStringListMessage(player, config.getStringList("messages.need to collect souls"), null);
                                    return;
                                } else {
                                    collectedsouls = amount;
                                }
                                if(amount == 0)  {
                                    sendStringListMessage(player, config.getStringList("messages.need to collect souls"), null);
                                    return;
                                }
                                gems = (int) (collectedsouls * st.getSoulsCollected());
                                did = true;
                            }
                        }
                    }
                }
                if(did) {
                    lore.set(applied, appliedst.getApplied().replace("{SOULS}", Integer.toString(totalsouls - amount)));
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                }
            }
            if(split != null) {
                if(g == null) g = appliedst.getConvertsTo();
                item = g.getItem().clone(); itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(item.getItemMeta().getDisplayName().replace("{SOULS}", ChatColor.translateAlternateColorCodes('&', g.getColors(gems)) + gems));
                if(gems != 0) item.setAmount(1);
                item.setItemMeta(itemMeta);
                giveItem(player, item);
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{SOULS}", Integer.toString(collectedsouls));
                replacements.put("{GEMS}", Integer.toString(gems));
                sendStringListMessage(player, split, replacements);
                player.updateInventory();
            }
        }
    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
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
    }
    @EventHandler(priority = EventPriority.HIGH)
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
        if(soultrackers != null)
            for(SoulTracker st : soultrackers.values())
                if(st.getConvertsTo().equals(gem))
                    return st;
        return null;
    }
    public SoulTracker valueOf(ItemStack is) {
        if(soultrackers != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(SoulTracker s : soultrackers.values()) {
                if(s.getItem().getItemMeta().equals(m)) {
                    return s;
                }
            }
        }
        return null;
    }
    public HashMap<Integer, SoulTracker> valueOfApplied(ItemStack is) {
        if(soultrackers != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            final Collection<SoulTracker> trackers = soultrackers.values();
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
        if(soultrackers != null) {
            for(SoulTracker st : soultrackers.values()) {
                if(appliedlore.startsWith(st.getApplied().replace("{SOULS}", ""))) {
                    return st;
                }
            }
        }
        return null;
    }
}
