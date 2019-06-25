package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Lootbox {

    public static HashMap<String, Lootbox> lootboxes;
    private static RandomPackageAPI api;
    private static Random random;

    private YamlConfiguration yml;
    private String ymlName, name, guiTitle, previewTitle, regularLootSize, bonusLootSize;
    private int priority, availableFor, guiSize;
    private List<String> guiFormat, regularLootFormat, jackpotLootFormat, bonusLootFormat, randomLoot, jackpotLoot, bonusLoot;
    private ItemStack item, background;

    public Lootbox(File f) {
        if(lootboxes == null) {
            lootboxes = new HashMap<>();
            api = RandomPackageAPI.getAPI();
            random = new Random();
        }
        yml = YamlConfiguration.loadConfiguration(f);
        this.ymlName = f.getName().split("\\.yml")[0];
        this.priority = yml.getInt("priority");
        this.availableFor = yml.getInt("available for");
        lootboxes.put(ymlName, this);
    }

    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return ymlName; }
    public String getName() {
        if(name == null) name = ChatColor.translateAlternateColorCodes('&', yml.getString("name"));
        return name;
    }
    public String getGuiTitle() {
        if(guiTitle == null) guiTitle = ChatColor.translateAlternateColorCodes('&', yml.getString("gui.title"));
        return guiTitle;
    }
    public String getPreviewTitle() {
        if(previewTitle == null) previewTitle = ChatColor.translateAlternateColorCodes('&', yml.getString("preview title"));
        return previewTitle;
    }
    public String getRegularLootSize() {
        if(regularLootSize == null) regularLootSize = yml.getString("regular loot size");
        return regularLootSize;
    }
    public String getBonusLootSize() {
        if(bonusLootSize == null) bonusLootSize = yml.getString("bonus loot size");
        return bonusLootSize;
    }
    public int getPriority() { return priority; }
    public int getAvailableFor() { return availableFor; }
    public int getGuiSize() {
        if(guiSize == 0) guiSize = yml.getInt("gui.size");
        return guiSize;
    }
    public List<String> getGuiFormat() {
        if(guiFormat == null) guiFormat = yml.getStringList("gui.format");
        return guiFormat;
    }
    public List<String> getRegularLootFormat() {
        if(regularLootFormat == null) regularLootFormat = api.colorizeListString(yml.getStringList("lore formats.regular loot"));
        return regularLootFormat;
    }
    public List<String> getJackpotLootFormat() {
        if(jackpotLootFormat == null) jackpotLootFormat = api.colorizeListString(yml.getStringList("lore formats.jackpot loot"));
        return jackpotLootFormat;
    }
    public List<String> getBonusLootFormat() {
        if(bonusLootFormat == null) bonusLootFormat = api.colorizeListString(yml.getStringList("lore formats.bonus loot"));
        return bonusLootFormat;
    }
    public List<String> getRandomLoot() {
        if(randomLoot == null) randomLoot = yml.getStringList("regular loot");
        return randomLoot;
    }
    public List<String> getJackpotLoot() {
        if(jackpotLoot == null) jackpotLoot = yml.getStringList("jackpot loot");
        return jackpotLoot;
    }
    public List<String> getBonusLoot() {
        if(bonusLoot == null) bonusLoot = yml.getStringList("bonus loot");
        return bonusLoot;
    }

    public ItemStack getItem() {
        if(item == null) {
            final ItemStack i = api.d(yml, "lootbox");
            final ItemMeta itemMeta = i.getItemMeta();
            final List<String> lore = new ArrayList<>(), regularLootFormat = getRegularLootFormat(), jackpotLootFormat = getJackpotLootFormat(), bonusLootFormat = getBonusLootFormat();
            for(String s : itemMeta.getLore()) {
                if(s.equals("{REGULAR_LOOT}")) {
                    for(ItemStack is : regularLoot()) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : regularLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else if(s.equals("{JACKPOT_LOOT}")) {
                    for(ItemStack is : jackpotLoot()) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : jackpotLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else if(s.equals("{BONUS_LOOT}")) {
                    for(ItemStack is : bonusLoot()) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : bonusLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else {
                    lore.add(s);
                }
            }
            itemMeta.setLore(lore);
            i.setItemMeta(itemMeta);
            this.item = i;
        }
        return item.clone();
    }
    public ItemStack getBackground() {
        if(background == null) background = api.d(yml, "gui.background");;
        return background.clone();
    }


    public int randomRegularLootSize() {
        final String s = getRegularLootSize();
        final boolean b = s.contains("-");
        final int min = Integer.parseInt(b ? s.split("-")[0] : s), max = maxRegularLoot();
        return b ? min+random.nextInt(max-min+1) : min;
    }
    public int maxRegularLoot() {
        final String s = getRegularLootSize();
        return s.contains("-") ? Integer.parseInt(s.split("-")[1]) : Integer.parseInt(s);
    }

    public static Lootbox valueOf(String guiTitle) {
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values())
                if(l.getGuiTitle().equals(guiTitle))
                    return l;
        }
        return null;
    }
    public static Lootbox valueof(String previewTitle) {
        if(lootboxes != null) {
            previewTitle = ChatColor.stripColor(previewTitle);
            for(Lootbox l : lootboxes.values()) {
                if(ChatColor.stripColor(l.getPreviewTitle()).equals(previewTitle)) {
                    return l;
                }
            }
        }
        return null;
    }
    public static Lootbox valueOf(ItemStack is) {
        if(lootboxes != null && is != null && is.hasItemMeta())
            for(Lootbox l : lootboxes.values())
                if(l.getItem().isSimilar(is))
                    return l;
        return null;
    }
    public static Lootbox valueOf(int priority) {
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values())
                if(l.priority == priority)
                    return l;
        }
        return null;
    }
    public static Lootbox latest() {
        int p = 0;
        Lootbox lo = null;
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values()) {
                if(lo == null || l.priority > p) {
                    p = l.priority;
                    lo = l;
                }
            }
        }
        return lo;
    }
    public List<ItemStack> regularLoot() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getRandomLoot()) {
            items.add(api.d(null, s));
        }
        return items;
    }
    public String randomRegularLoot(List<String> excluding) {
        final List<String> loot = new ArrayList<>(getRandomLoot());
        loot.addAll(getJackpotLoot());
        for(String s : excluding) {
            loot.remove(s);
        }
        return loot.get(random.nextInt(loot.size()));
    }
    public List<ItemStack> jackpotLoot() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getJackpotLoot()) {
            items.add(api.d(null, s));
        }
        return items;
    }
    public String randomBonusLoot(List<String> excluding) {
        final List<String> loot = new ArrayList<>(getBonusLoot());
        for(String s : excluding) {
            loot.remove(s);
        }
        final int s = loot.size();
        return s > 0 ? loot.get(random.nextInt(s)) : "air";
    }
    public List<ItemStack> bonusLoot() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getBonusLoot()) {
            items.add(api.d(null, s));
        }
        return items;
    }
    public List<ItemStack> items() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getRandomLoot()) items.add(api.d(null, s));
        for(String s : getJackpotLoot()) items.add(api.d(null, s));
        for(String s : getBonusLoot()) items.add(api.d(null, s));
        return items;
    }


    public static void deleteAll() {
        lootboxes = null;
        api = null;
        random = null;
    }
}
