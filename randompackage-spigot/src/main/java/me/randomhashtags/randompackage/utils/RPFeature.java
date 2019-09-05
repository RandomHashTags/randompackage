package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.utils.Mathable;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.api.addons.TransmogScrolls;
import me.randomhashtags.randompackage.utils.supported.RegionalAPI;
import me.randomhashtags.randompackage.utils.supported.economy.VaultAPI;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.utils.listeners.GivedpItem.givedpitem;

public abstract class RPFeature extends RPStorage implements Listener, Identifiable, Mathable {
    private boolean isEnabled = false;
    private static boolean mcmmoIsEnabled = false;
    public boolean isEnabled() { return isEnabled; }
    protected boolean mcmmoIsEnabled() { return mcmmoIsEnabled; }

    public static final File rpd = getPlugin.getDataFolder();
    public static final String separator = File.separator;
    public static final RegionalAPI regions = RegionalAPI.getRegionalAPI();
    protected static final Economy eco = VaultAPI.getVaultAPI().getEconomy();
    private static final TreeMap<Integer, String> treemap = new TreeMap<>();

    public static YamlConfiguration otherdata;
    protected static File otherdataF;
    public static UInventory givedp;
    public static List<Inventory> givedpCategories;

    public void enable() {
        if(otherdataF == null) {
            save("_Data", "other.yml");
            otherdataF = new File(rpd + separator + "_Data", "other.yml");
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);

            treemap.put(1000, "M"); treemap.put(900, "CM"); treemap.put(500, "D"); treemap.put(400, "CD"); treemap.put(100, "C"); treemap.put(90, "XC");
            treemap.put(50, "L"); treemap.put(40, "XL"); treemap.put(10, "X"); treemap.put(9, "IX"); treemap.put(5, "V"); treemap.put(4, "IV"); treemap.put(1, "I");

            givedp = new UInventory(null, 27, "Givedp Categories");
            givedpCategories = new ArrayList<>();

            mcmmoIsEnabled = pluginmanager.isPluginEnabled("mcMMO");
        }
        if(isEnabled) return;
        try {
            isEnabled = true;
            load();
            final RPFeature f = getFeature();
            if(f != null) {
                pluginmanager.registerEvents(f, randompackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void disable() {
        if(!isEnabled) return;
        try {
            isEnabled = false;
            unload();
            final RPFeature f = getFeature();
            if(f != null) {
                HandlerList.unregisterAll(f);
            }
            sendConsoleMessage("&6[RandomPackage] &cDisabled RandomPackage Feature " + getIdentifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void d() {
        otherdata = null;
        otherdataF = null;
        treemap.clear();
        givedp = null;
        givedpCategories = null;
        mcmmoIsEnabled = false;
    }

    protected abstract RPFeature getFeature();
    public abstract void load();
    public abstract void unload();

    public void saveOtherData() {
        try {
            otherdata.save(otherdataF);
            otherdataF = new File(rpd + separator + "_Data", "other.yml");;
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewGivedp(Player player) {
        player.openInventory(Bukkit.createInventory(player, givedp.getSize(), givedp.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(givedp.getInventory().getContents());
        player.updateInventory();
    }
    public void addGivedpCategory(List<ItemStack> items, UMaterial m, String what, String invtitle) {
        item = m.getItemStack(); itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + what);
        item.setItemMeta(itemMeta);
        givedp.getInventory().addItem(item);
        final int size = items.size();
        final Inventory inv = Bukkit.createInventory(null, size == 9 || size == 18 || size == 27 || size == 36 || size == 45 || size == 54 ? size : ((size+9)/9)*9, invtitle);
        for(ItemStack is : items) if(is != null) inv.addItem(is);
        givedpCategories.add(inv);
    }
    public String toRoman(int number) {
        /* This code is from "bhlangonijr" at https://stackoverflow.com/questions/12967896 */
        if(number <= 0) return "";
        int l = treemap.floorKey(number);
        if(number == l) return treemap.get(number);
        return treemap.get(l) + toRoman(number - l);
    }
    private enum RomanNumeralValues {
        I(1), X(10), C(100), M(1000), V(5), L(50), D(500);
        private int val;
        RomanNumeralValues(int val) { this.val = val; }
        public int asInt() { return val; }
    }
    protected int fromRoman(String num) {
        /* This code is from "batman" at https://stackoverflow.com/questions/9073150 */
        num = ChatColor.stripColor(num.toUpperCase());
        int intNum = 0, prev = 0;
        for(int i = num.length()-1; i >= 0; i--) {
            final String character = num.substring(i, i+1);
            int temp = RomanNumeralValues.valueOf(character).asInt();
            if(temp < prev) intNum -= temp;
            else            intNum += temp;
            prev = temp;
        }
        return intNum;
    }
    public boolean hasPermission(CommandSender sender, String permission, boolean sendNoPermMessage) {
        if(!(sender instanceof Player) || sender.hasPermission(permission)) return true;
        else if(sendNoPermMessage) {
            sendStringListMessage(sender, randompackage.getConfig().getStringList("no permission"), null);
        }
        return false;
    }


    public ItemStack d(FileConfiguration config, String path) {
        return d(config, path, 0, 0.00f);
    }
    public ItemStack d(FileConfiguration config, String path, int tier, float enchantMultiplier) {
        item = null;
        if(config == null && path != null || config != null && config.get(path + ".item") != null) {
            final String PP = config == null ? path : config.getString(path + ".item");
            String P = PP.toLowerCase();

            int amount = config != null && config.get(path + ".amount") != null ? config.getInt(path + ".amount") : 1;
            if(P.toLowerCase().contains(";amount=")) {
                final String A = P.split("=")[1];
                final boolean B = P.contains("-");
                final int min = B ? Integer.parseInt(A.split("-")[0]) : 0;
                amount = B ? min+random.nextInt(Integer.parseInt(A.split("-")[1])-min+1) : Integer.parseInt(A);
                path = path.split(";amount=")[0];
                P = P.split(";")[0];
            }
            if(P.contains("spawner") && !P.startsWith("mob_spawner") && !path.equals("mysterymobspawner")) {
                return getSpawner(P);
            } else if(P.startsWith("enchantedbook:")) {
                final Enchantment e = getEnchantment(P.split(":")[1]);
                if(e != null) {
                    int level = 1;
                    if(P.split(":").length == 3)
                        level = P.split(":")[2].equals("random") ? 1 + random.nextInt(e.getMaxLevel()) : P.split(":")[2].contains("-") ? Integer.parseInt(P.split(":")[2].split("\\-")[0]) + random.nextInt(Integer.parseInt(P.split(":")[2].split("\\-")[1])) : Integer.parseInt(P.split(":")[2]);
                    item = new ItemStack(Material.ENCHANTED_BOOK, amount);
                    final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    meta.addStoredEnchant(e, level, true);
                    item.setItemMeta(meta);
                    return item;
                }
                return null;
            }
            ItemStack B = givedpitem.valueOf(PP);
            if(B == null) B = givedpitem.valueOf(P);
            if(B != null) {
                item = B.clone();
                item.setAmount(amount);
                return item;
            }
            lore.clear();
            String name = config != null ? config.getString(path + ".name") : null;
            final String[] material = P.toUpperCase().split(":");
            final String mat = material[0];
            final byte data = material.length == 2 ? Byte.parseByte(material[1]) : 0;
            final UMaterial U = UMaterial.match(mat + (data != 0 ? ":" + data : ""));
            try {
                item = U.getItemStack();
            } catch (Exception e) {
                System.out.println("UMaterial null itemstack. mat=" + mat + ";data=" + data + ";versionName=" + (U != null ? U.getVersionName() : null) + ";getMaterial()=" + (U != null ? U.getMaterial() : null));
                return null;
            }
            final Material skullitem = UMaterial.PLAYER_HEAD_ITEM.getMaterial(), i = item.getType();
            if(!i.equals(Material.AIR)) {
                item.setAmount(amount);
                itemMeta = item.getItemMeta();
                if(i.equals(skullitem)) {
                    final String owner = P.contains(";owner=") ? P.split("=")[1].split("}")[0].split(";")[0] : "RandomHashTags";
                    final SkullMeta m = (SkullMeta) itemMeta;
                    m.setOwner(owner);
                    itemMeta = m;
                }
                itemMeta.setDisplayName(name != null ? ChatColor.translateAlternateColorCodes('&', name) : null);
                item.setItemMeta(itemMeta);

                if(config != null && config.get(path + ".lore") != null) {
                    item = updateLore(item, config.getStringList(path + ".lore"), tier, enchantMultiplier, CustomEnchants.getCustomEnchants().levelZeroRemoval, "null");
                    itemMeta = item.getItemMeta();
                }
                item.setItemMeta(itemMeta);
                if(name != null && name.contains("{ENCHANT_SIZE}")) {
                    final TransmogScrolls t = TransmogScrolls.getTransmogScrolls();
                    if(t.isEnabled()) t.applyTransmogScroll(item, getTransmogScroll("REGULAR"));
                }
            }
        }
        return item;
    }
    public ItemStack updateLore(ItemStack is, List<String> toLore, int tier, float enchantMultiplier, boolean levelzeroremoval, String max) {
        lore.clear();
        if(is != null && toLore != null && !toLore.isEmpty()) {
            final ItemMeta m = is.getItemMeta();
            if(m != null) {
                final LinkedHashMap<Enchantment, Integer> enchants = new LinkedHashMap<>();
                final List<ItemFlag> flags = new ArrayList<>();
                for(String string : toLore) {
                    final String sl = string.toLowerCase();
                    if(sl.startsWith("venchants{")) {
                        for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                            enchants.put(getEnchantment(s), getRemainingInt(s));
                        }
                    } else if(sl.startsWith("vmeta{")) {
                        for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                            try {
                                flags.add(ItemFlag.valueOf(s.toUpperCase()));
                            } catch (Exception e) {
                                System.out.println("[RandomPackage] WARNING: No ItemFlag found for string \"" + s + "\"");
                            }
                        }
                    } else if(sl.startsWith("rpenchants{")) {
                        for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                            final CustomEnchant e = valueOfCustomEnchant(s);
                            if(e != null && e.isEnabled()) {
                                final EnchantRarity r = valueOfEnchantRarity(e);
                                if(r != null) {
                                    int l = getRemainingInt(s), x = (int) (e.getMaxLevel()*enchantMultiplier);
                                    l = l != -1 ? l : x+random.nextInt(e.getMaxLevel()-x+1);
                                    if(l != 0 || !levelzeroremoval)
                                        lore.add(r.getApplyColors() + e.getName() + " " + toRoman(l != 0 ? l : 1));
                                } else {
                                    System.out.println("[RandomPackage] WARNING: No EnchantRarity found for enchant \"" + e.getName() + "\"!");
                                }
                            }
                        }
                    } else if(string.startsWith("{") && (!sl.contains("reqlevel=") && sl.contains("chance=") || sl.contains("reqlevel=") && tier >= Integer.parseInt(sl.split("reqlevel=")[1].split(":")[0]))) {
                        final CustomEnchant en = valueOfCustomEnchant(string.split("\\{")[1].split("}")[0], true);
                        final boolean c = string.contains("chance=");
                        if(en != null && en.isEnabled() && (!c || random.nextInt(100) <= Integer.parseInt(string.split("chance=")[1]))) {
                            final int lvl = random.nextInt(en.getMaxLevel()+1);
                            if(lvl != 0 || !levelzeroremoval) {
                                lore.add(valueOfEnchantRarity(en).getApplyColors() + en.getName() + " " + toRoman(lvl == 0 ? 1 : lvl));
                            }
                        }
                    } else {
                        lore.add(string.isEmpty() ? string : ChatColor.translateAlternateColorCodes('&', string.replace("{MAX_TIER}", max)));
                    }
                }
                m.setLore(lore);
                for(ItemFlag f : flags) {
                    m.addItemFlags(f);
                }
                is.setItemMeta(m);
                for(Enchantment enchantment : enchants.keySet()) {
                    if(enchantment != null) {
                        is.addUnsafeEnchantment(enchantment, enchants.get(enchantment));
                    }
                }
                final String name = m.hasDisplayName() ? m.getDisplayName() : null;
                if(name != null && name.contains("{ENCHANT_SIZE}")) {
                    final TransmogScrolls t = TransmogScrolls.getTransmogScrolls();
                    if(t.isEnabled()) {
                        t.applyTransmogScroll(is, getTransmogScroll("REGULAR"));
                    }
                }
            }
        }
        lore.clear();
        return is;
    }
}
