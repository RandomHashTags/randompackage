package me.randomhashtags.randompackage.api.enchantAddons;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.SoulTracker;
import me.randomhashtags.randompackage.addons.usingpath.PathSoulTracker;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
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

import java.io.File;
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

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && hasPermission(sender, "RandomPackage.splitsouls", true)) {
            splitsouls(player, args.length == 0 ? -1 : getRemainingInt(args[0]));
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("custom enchants", "soul trackers.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants" + separator, "soul trackers.yml"));
        PathSoulTracker.soultrackersyml = config;

        final ConfigurationSection cs = config.getConfigurationSection("trackers");
        if(cs != null) {
            final List<ItemStack> z = new ArrayList<>();
            for(String s : cs.getKeys(false)) z.add(new PathSoulTracker(s).getItem());
            addGivedpCategory(z, UMaterial.PAPER, "Soul Trackers", "Givedp: Soul Trackers");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " +  (soultrackers != null ? soultrackers.size() : 0) + " Soul Trackers &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        instance = null;
        deleteAll(Feature.SOUL_TRACKERS);
        PathSoulTracker.soultrackersyml = null;
    }

    public void applySoulTracker(Player player, ItemStack is, SoulTracker soultracker) {
        if(is != null && !is.getType().equals(Material.AIR) && soultrackers != null) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            boolean did = false;
            final String a = soultracker.getAppliedLore(), ist = is.getType().name(), istl = ist.toLowerCase();
            final Collection<SoulTracker> trackers = soultrackers.values();
            for(String s : soultracker.getAppliesTo()) {
                if(istl.endsWith(s.toLowerCase())) {
                    if(!lore.isEmpty()) {
                        for(int i = 0; i < lore.size(); i++) {
                            final String targetLore = lore.get(i);
                            for(SoulTracker st : trackers) {
                                if(!did && targetLore.startsWith(st.getAppliedLore().replace("{SOULS}", ""))) {
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
            RarityGem g = RarityGem.valueOf(item);
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
                            final String a = st.getAppliedLore();
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
                    lore.set(applied, appliedst.getAppliedLore().replace("{SOULS}", Integer.toString(totalsouls - amount)));
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
            final SoulTracker soultracker = SoulTracker.valueOf(cursor);
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
                final HashMap<Integer, SoulTracker> s = SoulTracker.valueOfApplied(is);
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
        lore.set(loreSlot, tracker.getAppliedLore().replace("{SOULS}", Integer.toString(getRemainingInt(ChatColor.stripColor(lore.get(loreSlot)))+1)));
        itemMeta.setLore(lore); lore.clear();
        is.setItemMeta(itemMeta);
        player.updateInventory();
    }
}
