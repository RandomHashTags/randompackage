package me.randomhashtags.randompackage.util;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.TransmogScroll;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Mathable;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.api.addon.Scrolls;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.economy.Vault;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

public abstract class RPFeature extends RegionalAPI implements Listener, Identifiable, Mathable, RPStorage {
    private boolean isEnabled = false;
    private static boolean mcmmoIsEnabled = false;
    public boolean isEnabled() { return isEnabled; }
    protected boolean mcmmoIsEnabled() { return mcmmoIsEnabled; }

    public static final RegionalAPI regions = RegionalAPI.getRegionalAPI();
    protected static final Economy eco = Vault.getVault().getEconomy();

    public static YamlConfiguration otherdata;
    protected static File otherdataF;
    public static UInventory givedp;
    public static List<Inventory> givedpCategories;

    public void enable() {
        if(otherdataF == null) {
            save("_Data", "other.yml");
            otherdataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "other.yml");
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);

            givedp = new UInventory(null, 27, "Givedp Categories");
            givedpCategories = new ArrayList<>();

            mcmmoIsEnabled = PLUGIN_MANAGER.isPluginEnabled("mcMMO");
        }
        if(isEnabled) return;
        try {
            isEnabled = true;
            load();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(isEnabled) {
                PLUGIN_MANAGER.registerEvents(this, RANDOM_PACKAGE);
            }
        }
    }
    public void disable() {
        if(!isEnabled) return;
        try {
            isEnabled = false;
            unload();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(!isEnabled) {
                HandlerList.unregisterAll(this);
                sendConsoleMessage("&6[RandomPackage] &cDisabled RandomPackage Feature " + getIdentifier());
            }
        }
    }
    public static void d() {
        otherdata = null;
        otherdataF = null;
        givedp = null;
        givedpCategories = null;
        mcmmoIsEnabled = false;
    }

    public abstract void load();
    public abstract void unload();

    public void saveOtherData() {
        try {
            otherdata.save(otherdataF);
            otherdataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "other.yml");;
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewGivedp(@NotNull Player player) {
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

    public boolean hasPermission(CommandSender sender, String permission, boolean sendNoPermMessage) {
        if(!(sender instanceof Player) || sender.hasPermission(permission)) return true;
        else if(sendNoPermMessage) {
            sendStringListMessage(sender, getStringList(RP_CONFIG, "no permission"), null);
        }
        return false;
    }


    public ItemStack d(FileConfiguration config, String path) {
        return d(config, path, 0, 0.00f);
    }
    public ItemStack d(FileConfiguration config, String path, int tier, float enchantMultiplier) {
        item = null;
        if(config == null && path != null || config != null && config.get(path + ".item") != null) {
            final String itemPath = config == null ? path : config.getString(path + ".item");
            String itemPathLC = itemPath.toLowerCase();

            int amount = config != null && config.get(path + ".amount") != null ? config.getInt(path + ".amount") : 1;
            if(itemPathLC.contains(";amount=")) {
                final String amountString = itemPathLC.split("=")[1];
                final boolean isRange = itemPathLC.contains("-");
                final int min = isRange ? Integer.parseInt(amountString.split("-")[0]) : 0;
                amount = isRange ? min+RANDOM.nextInt(Integer.parseInt(amountString.split("-")[1])-min+1) : Integer.parseInt(amountString);
                path = path.split(";amount=")[0];
                itemPathLC = itemPathLC.split(";")[0];
            }
            final boolean hasChance = itemPathLC.contains("chance=");
            if(hasChance && RANDOM.nextInt(100) > Integer.parseInt(itemPathLC.split("chance=")[1].split(";")[0])) {
                return null;
            }
            if(itemPathLC.contains("spawner") && !itemPathLC.startsWith("mob_spawner") && !path.equals("mysterymobspawner")) {
                return getSpawner(itemPathLC);
            } else if(itemPathLC.startsWith("enchantedbook:")) {
                final String[] values = itemPathLC.split(":");
                final Enchantment e = getEnchantment(values[1]);
                if(e != null) {
                    int level = 1;
                    if(values.length == 3) {
                        final String[] ints = values[2].split("-");
                        final boolean isRandom = ints[0].equalsIgnoreCase("random");
                        final int min = isRandom ? 0 : Integer.parseInt(ints[0]);
                        level = isRandom ? 1+RANDOM.nextInt(e.getMaxLevel()) : ints[2].contains("-") ? min+RANDOM.nextInt(Integer.parseInt(ints[1])) : min;
                    }
                    item = new ItemStack(Material.ENCHANTED_BOOK, amount);
                    final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    meta.addStoredEnchant(e, level, true);
                    item.setItemMeta(meta);
                    return item;
                }
                return null;
            }
            ItemStack B = GIVEDP_ITEM.valueOf(itemPath);
            if(B == null) B = GIVEDP_ITEM.valueOf(itemPathLC);
            if(B != null) {
                item = B.clone();
                item.setAmount(amount);
                return item;
            }
            lore.clear();
            String name = config != null ? config.getString(path + ".name") : null;
            final String[] material = itemPathLC.toUpperCase().split(":");
            final String mat = material[0];
            final byte data = material.length == 2 ? Byte.parseByte(material[1]) : 0;
            final UMaterial umaterial = UMaterial.match(mat + (data != 0 ? ":" + data : ""));
            try {
                item = umaterial.getItemStack();
            } catch (Exception e) {
                System.out.println("UMaterial null itemstack. mat=" + mat + ";data=" + data + ";versionName=" + (umaterial != null ? umaterial.getVersionName() : null) + ";getMaterial()=" + (umaterial != null ? umaterial.getMaterial() : null));
                return null;
            }
            final Material skullitem = UMaterial.PLAYER_HEAD_ITEM.getMaterial(), i = item.getType();
            if(!i.equals(Material.AIR)) {
                item.setAmount(amount);
                itemMeta = item.getItemMeta();
                if(i.equals(skullitem)) {
                    final String owner = itemPathLC.contains(";owner=") ? itemPathLC.split("=")[1].split("}")[0].split(";")[0] : "RandomHashTags";
                    final SkullMeta m = (SkullMeta) itemMeta;
                    m.setOwner(owner);
                    itemMeta = m;
                }
                itemMeta.setDisplayName(name != null ? colorize(name) : null);
                item.setItemMeta(itemMeta);

                if(config != null && config.get(path + ".lore") != null) {
                    item = updateLore(item, config.getStringList(path + ".lore"), tier, enchantMultiplier, CustomEnchants.getCustomEnchants().levelZeroRemoval, "null");
                    itemMeta = item.getItemMeta();
                }
                item.setItemMeta(itemMeta);
                if(name != null && name.contains("{ENCHANT_SIZE}")) {
                    applyTransmogScroll(item, getTransmogScroll("REGULAR"));
                }
            }
        }
        return item;
    }
    public ItemStack updateLore(ItemStack is, List<String> toLore, int tier, float enchantMultiplier, boolean levelzeroremoval, String max) {
        lore.clear();
        if(is != null && toLore != null && !toLore.isEmpty()) {
            final ItemMeta meta = is.getItemMeta();
            if(meta != null) {
                final LinkedHashMap<Enchantment, Integer> enchants = new LinkedHashMap<>();
                final List<ItemFlag> flags = new ArrayList<>();
                for(String string : toLore) {
                    final String stringLC = string.toLowerCase();
                    if(stringLC.startsWith("venchants{")) {
                        for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                            enchants.put(getEnchantment(s), getRemainingInt(s));
                        }
                    } else if(stringLC.startsWith("vmeta{")) {
                        for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                            try {
                                flags.add(ItemFlag.valueOf(s.toUpperCase()));
                            } catch (Exception e) {
                                System.out.println("[RandomPackage] WARNING: No ItemFlag found for string \"" + s + "\"");
                            }
                        }
                    } else if(stringLC.startsWith("rpenchants{")) {
                        for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                            final CustomEnchant enchant = valueOfCustomEnchant(s);
                            if(enchant != null && enchant.isEnabled()) {
                                final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
                                if(rarity != null) {
                                    int l = getRemainingInt(s), x = (int) (enchant.getMaxLevel()*enchantMultiplier);
                                    l = l != -1 ? l : x+ RANDOM.nextInt(enchant.getMaxLevel()-x+1);
                                    if(l != 0 || !levelzeroremoval)
                                        lore.add(rarity.getApplyColors() + enchant.getName() + " " + toRoman(l != 0 ? l : 1));
                                } else {
                                    System.out.println("[RandomPackage] WARNING: No EnchantRarity found for enchant \"" + enchant.getName() + "\"!");
                                }
                            }
                        }
                    } else if(string.startsWith("{") && (!stringLC.contains("reqlevel=") && stringLC.contains("chance=") || stringLC.contains("reqlevel=") && tier >= Integer.parseInt(stringLC.split("reqlevel=")[1].split(":")[0]))) {
                        final CustomEnchant enchant = valueOfCustomEnchant(string.split("\\{")[1].split("}")[0], true);
                        final boolean isChance = string.contains("chance=");
                        if(enchant != null && enchant.isEnabled() && (!isChance || RANDOM.nextInt(100) <= Integer.parseInt(string.split("chance=")[1]))) {
                            final int lvl = RANDOM.nextInt(enchant.getMaxLevel()+1);
                            if(lvl != 0 || !levelzeroremoval) {
                                lore.add(valueOfCustomEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(lvl == 0 ? 1 : lvl));
                            }
                        }
                    } else {
                        lore.add(string.isEmpty() ? string : colorize(string.replace("{MAX_TIER}", max)));
                    }
                }
                meta.setLore(lore);
                for(ItemFlag f : flags) {
                    meta.addItemFlags(f);
                }
                is.setItemMeta(meta);
                for(Enchantment enchantment : enchants.keySet()) {
                    if(enchantment != null) {
                        is.addUnsafeEnchantment(enchantment, enchants.get(enchantment));
                    }
                }
                final String name = meta.hasDisplayName() ? meta.getDisplayName() : null;
                if(name != null && name.contains("{ENCHANT_SIZE}")) {
                    applyTransmogScroll(is, getTransmogScroll("REGULAR"));
                }
            }
        }
        lore.clear();
        return is;
    }

    private void applyTransmogScroll(ItemStack is, TransmogScroll scroll) {
        final Scrolls scrolls = Scrolls.getScrolls();
        if(scrolls.isEnabled() && scrolls.isEnabled(Feature.SCROLL_TRANSMOG)) {
            scrolls.applyTransmogScroll(is, scroll);
        }
    }
}
