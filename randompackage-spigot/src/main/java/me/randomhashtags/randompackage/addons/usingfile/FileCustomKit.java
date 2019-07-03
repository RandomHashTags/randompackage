package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.objects.KitItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class FileCustomKit extends CustomKit {
    private List<KitItem> items;
    private ItemStack spawnitem, gem;

    public int getSlot() { return yml.getInt("gui settings.slot"); }
    public int getMaxLevel() { return yml.getInt("settings.max level"); }
    public long getCooldown() { return yml.getLong("settings.cooldown"); }
    public FileFallenHero getFallenHero() {
        //final FallenHero f = UVersion.getUVersion().getFallenHero(null, yml.getString("settings.fallen hero"));
        return null;
    }
    public String getFallenHeroName() { return getFallenHero().getSpawnItem().getItemMeta().getDisplayName().replace("{NAME}", getItem().getItemMeta().getDisplayName()); }
    public ItemStack getFallenHeroSpawnItem() {
        if(spawnitem == null) {
            final String n = getItem().getItemMeta().getDisplayName();
            final ItemStack is = getFallenHero().getSpawnItem();
            final ItemMeta m = is.getItemMeta();
            m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
            final List<String> l = new ArrayList<>();
            for(String s : m.getLore()) {
                l.add(s.replace("{NAME}", n));
            }
            m.setLore(l);
            is.setItemMeta(m);
            spawnitem = is;
        }
        return spawnitem.clone();
    }
    public ItemStack getFallenHeroGem() {
        if(gem == null) {
            final String n = getItem().getItemMeta().getDisplayName();
            final ItemStack is = getFallenHero().getGem();
            final ItemMeta m = is.getItemMeta();
            m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
            final List<String> l = new ArrayList<>();
            for(String s : m.getLore()) {
                l.add(s.replace("{NAME}", n));
            }
            m.setLore(l);
            is.setItemMeta(m);
            gem = is;
        }
        return gem.clone();
    }
    public List<KitItem> getItems() {
        if(items == null) {
            items = new ArrayList<>();
            for(String i : yml.getConfigurationSection("items").getKeys(false)) {
                final String t = yml.getString("items." + i + ".item");
                if(t != null) {
                    final int chance = yml.get("items." + i + ".chance") != null ? yml.getInt("items." + i + ".chance") : 100;
                    items.add(new KitItem(this, i, yml.getString("items." + i + ".item"), yml.getString("items." + i + ".name"), yml.getStringList("items." + i + ".lore"), chance, "1", false, yml.getInt("items." + i + ".reqlevel")));
                }
            }
        }
        return items;
    }
    public void setItems(List<KitItem> items) { this.items = items; }
}
