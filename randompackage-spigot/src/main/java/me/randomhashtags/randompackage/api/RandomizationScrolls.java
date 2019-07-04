package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RandomizationScroll;
import me.randomhashtags.randompackage.addons.usingfile.FileRandomizationScroll;
import me.randomhashtags.randompackage.events.RandomizationScrollUseEvent;
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
import java.util.List;

public class RandomizationScrolls extends RPFeature {

    public void load() {
        final long started = System.currentTimeMillis();
        if(!otherdata.getBoolean("saved default randomization scrolls")) {
            final String[] a = new String[]{};
            for(String s : a) save("randomization scrolls", s + ".yml");
            otherdata.set("saved default randomization scrolls", true);
            saveOtherData();
        }

        final File folder = new File(rpd + separator + "randomization scrolls");
        if(folder.exists()) {
            final List<ItemStack> z = new ArrayList<>();
            for(File f : folder.listFiles()) {
                z.add(new FileRandomizationScroll(f).getItem());
            }
            addGivedpCategory(z, UMaterial.PAPER, "Randomization Scrolls", "Givedp: Randomization Scrolls");
        }

        sendConsoleMessage("&6[RandomPackage] &aLoaded Randomization Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        deleteAll(Feature.RANDOMIZATION_SCROLLS);
    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final Player player = (Player) event.getWhoClicked();
            final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
            if(current != null && cursor != null && !current.getType().equals(Material.AIR) && !cursor.getType().equals(Material.AIR)) {
                final CustomEnchant enchant = CustomEnchant.valueOf(current);
                final RandomizationScroll randomizationscroll = RandomizationScroll.valueOf(cursor);
                if(enchant != null && randomizationscroll != null) {
                    final EnchantRarity r = EnchantRarity.valueOf(enchant);
                    if(randomizationscroll.getAppliesToRarities().contains(r)) {
                        final String s = r.getSuccess(), d = r.getDestroy();
                        int newSuccess = random.nextInt(101), newDestroy = random.nextInt(101);
                        final RandomizationScrollUseEvent e = new RandomizationScrollUseEvent(player, enchant, getEnchantmentLevel(itemMeta.getDisplayName()), randomizationscroll, newSuccess, newDestroy);
                        pluginmanager.callEvent(e);
                        newSuccess = e.getNewSuccess();
                        newDestroy = e.getNewDestroy();
                        for(String string : itemMeta.getLore()) {
                            if(string.equals(s.replace("{PERCENT}", "" + getRemainingInt(string))))        string = s.replace("{PERCENT}", "" + newSuccess);
                            else if(string.equals(d.replace("{PERCENT}", "" + getRemainingInt(string))))   string = d.replace("{PERCENT}", "" + newDestroy);
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        //playSuccess((Player) event.getWhoClicked());
                        item.setItemMeta(itemMeta);
                        event.setCancelled(true);
                        event.setCurrentItem(item);
                        final int a = cursor.getAmount();
                        if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                        else       cursor.setAmount(a-1);
                        player.updateInventory();
                    }
                }
            }
        }
    }
}
