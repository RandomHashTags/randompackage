package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.SoulTracker;
import me.randomhashtags.randompackage.addons.usingfile.FileRarityGem;
import me.randomhashtags.randompackage.addons.usingfile.FileSoulTracker;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SoulTrackers extends RPFeature {

    public void load() {
        final long started = System.currentTimeMillis();
        if(!otherdata.getBoolean("saved default soul trackers")) {
            final String[] a = new String[] {"ELITE", "GODLY", "LEGENDARY", "MONSTER", "SIMPLE", "ULTIMATE", "UNIQUE"};
            for(String s : a) {
                save("soul trackers", s + ".yml");
            }
            otherdata.set("saved default soul trackers", true);
            saveOtherData();
        }

        final File folder = new File(rpd + separator + "soul trackers");
        if(folder.exists()) {
            final List<ItemStack> z = new ArrayList<>();
            for(File f : folder.listFiles()) z.add(new FileSoulTracker(f).getItem());
            addGivedpCategory(z, UMaterial.PAPER, "Soul Trackers", "Givedp: Soul Trackers");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " +  (soultrackers != null ? soultrackers.size() : 0) + " Soul Trackers &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        deleteAll(Feature.SOUL_TRACKERS);
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
            FileRarityGem g = FileRarityGem.valueOf(item);
            int collectedsouls = 0, gems = 0;
            List<String> split = null;
            SoulTracker appliedst = null;
            if(g != null) {
                split = g.getSplitMessage();
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
                                gems = (int) (collectedsouls * st.getSoulsPerKill());
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
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        final SoulTracker soultracker = SoulTracker.valueOf(cursor);
        if(soultracker != null) {
            final Player player = (Player) event.getWhoClicked();
            applySoulTracker(player, current, soultracker);
            //playSuccess((Player) event.getWhoClicked());
            event.setCancelled(true);
            event.setCurrentItem(item);
            final int a = cursor.getAmount();
            if(a == 1) event.setCursor(new ItemStack(Material.AIR));
            else       cursor.setAmount(a-1);
            player.updateInventory();
        }
    }
}
