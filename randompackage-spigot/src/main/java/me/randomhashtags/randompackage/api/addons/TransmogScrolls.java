package me.randomhashtags.randompackage.api.addons;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.TransmogScroll;
import me.randomhashtags.randompackage.addons.usingpath.PathTransmogScroll;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransmogScrolls extends CustomEnchantUtils {
    private static TransmogScrolls instance;
    public static TransmogScrolls getTransmogScrolls() {
        if(instance == null) instance = new TransmogScrolls();
        return instance;
    }

    public String getIdentifier() { return "TRANSMOG_SCROLLS"; }
    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
        save("addons", "transmog scrolls.yml");
        final ConfigurationSection c = getAddonConfig("transmog scrolls.yml").getConfigurationSection("transmog scrolls");
        if(c != null) {
            final List<ItemStack> a = new ArrayList<>();
            for(String s : c.getKeys(false)) {
                a.add(new PathTransmogScroll(s).getItem());
            }
            addGivedpCategory(a, UMaterial.PAPER, "Transmog Scrolls", "Givedp: Transmog Scrolls");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (transmogscrolls != null ? transmogscrolls.size() : 0) + " Transmog Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        transmogscrolls = null;
        instance = null;
        unloadUtils();
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final Player player = (Player) event.getWhoClicked();
            final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
            if(cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
                final TransmogScroll t = valueOf(cursor);
                if(t != null && applyTransmogScroll(current, t)) {
                    //playSuccess((Player) event.getWhoClicked());
                    event.setCancelled(true);
                    event.setCurrentItem(current);
                    final int a = cursor.getAmount();
                    if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                    else       cursor.setAmount(a-1);
                    player.updateInventory();
                }
            }
        }
    }


    public boolean applyTransmogScroll(ItemStack is, TransmogScroll scroll) {
        boolean did = true;
        if(is != null && scroll != null) {
            did = scroll.canBeApplied(is);
            if(did) {
                final String apply = scroll.getApplied();
                final HashMap<CustomEnchant, Integer> enchants = getEnchants(is);
                final int size = enchants.size();
                int newsize = 0;
                final String previous = apply.replace("{LORE_COUNT}", Integer.toString(size));
                itemMeta = is.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    final List<String> l = itemMeta.getLore();
                    for(String ss : scroll.getRarityOrganization()) {
                        final EnchantRarity r = rarities.get(ss);
                        for(String s : l) {
                            final CustomEnchant enchant = CustomEnchant.valueOf(s);
                            if(enchant != null && valueOfEnchantRarity(enchant) == r) {
                                lore.add(s);
                            }
                        }
                        newsize = lore.size();
                    }
                    for(String s : l) {
                        if(!lore.contains(s))
                            lore.add(s);
                    }
                }
                final String current = apply.replace("{LORE_COUNT}", Integer.toString(newsize)), material = is.getType().name();
                itemMeta.setLore(lore); lore.clear();
                //
                String name;
                if(itemMeta.hasDisplayName()) {
                    name = itemMeta.getDisplayName();
                    if(name.contains(previous)) name = name.replace(previous, current);
                } else name = is.getType().name();
                if(name.equals(material)) name = toMaterial(material, false);
                name = name.replace("{ENCHANT_SIZE}", current);
                if(!name.contains(previous)) name = name.concat(" " + current);
                ChatColor color = ChatColor.RESET;
                if(itemMeta.hasEnchants()) color = ChatColor.AQUA;
                itemMeta.setDisplayName(color + name);
                is.setItemMeta(itemMeta);
            }
        }
        return did;
    }

    public TransmogScroll valueOf(ItemStack is) {
        if(transmogscrolls != null && is != null) {
            for(TransmogScroll t : transmogscrolls.values()) {
                if(t.getItem().isSimilar(is)) {
                    return t;
                }
            }
        }
        return null;
    }
    public TransmogScroll getApplied(ItemStack is) {
        if(transmogscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final String size = Integer.toString(getEnchants(is).size()), d = is.getItemMeta().getDisplayName();
            for(TransmogScroll t : transmogscrolls.values()) {
                if(d.endsWith(t.getApplied().replace("{LORE_COUNT}", size).replace("{ENCHANT_SIZE}", size))) {
                    return t;
                }
            }
        }
        return null;
    }
    public void update(ItemStack is, int prevSize, int newSize) {
        if(transmogscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final String size = Integer.toString(prevSize), newsize = Integer.toString(newSize), d = is.getItemMeta().getDisplayName();
            for(TransmogScroll t : transmogscrolls.values()) {
                final String a = t.getApplied(), actual = a.replace("{LORE_COUNT}", size).replace("{ENCHANT_SIZE}", size);
                if(d.endsWith(actual)) {
                    itemMeta = is.getItemMeta();
                    itemMeta.setDisplayName(itemMeta.getDisplayName().replace(actual, a.replace("{LORE_COUNT}", newsize).replace("{ENCHANT_SIZE}", newsize)));
                    is.setItemMeta(itemMeta);
                    break;
                }
            }
        }
    }
}
