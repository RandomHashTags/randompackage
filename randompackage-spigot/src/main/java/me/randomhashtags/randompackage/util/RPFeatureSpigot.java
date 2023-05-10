package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.TransmogScroll;
import me.randomhashtags.randompackage.addon.file.NonThrowableJSONBehaviorSpigot;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.api.addon.Scrolls;
import me.randomhashtags.randompackage.attributesys.EventAttributeCoreListener;
import me.randomhashtags.randompackage.attributesys.EventAttributeListener;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.supported.economy.Vault;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public interface RPFeatureSpigot extends RPFeature, UVersionableSpigot, Listener, RPStorage, NonThrowableJSONBehaviorSpigot {
    HashSet<String> ENABLED_RP_FEATURES = new HashSet<>();

    Economy ECONOMY = Vault.INSTANCE.getEconomy();

    File OTHER_YML_FILE = new File(DATA_FOLDER + SEPARATOR + "_Data", "other.yml");
    YamlConfiguration OTHER_YML = YamlConfiguration.loadConfiguration(OTHER_YML_FILE);

    UInventory GIVEDP_INVENTORY = new UInventory(null, 27, "Givedp Categories");
    List<Inventory> GIVEDP_CATEGORIES = new ArrayList<>();

    static boolean mcmmoIsEnabled() {
        return PLUGIN_MANAGER.isPluginEnabled("mcMMO");
    }

    @Override
    @NotNull
    default String getIdentifier() {
        return getClass().getSimpleName();
    }

    @Nullable
    default Feature get_feature() {
        return null;
    }

    @Override
    default boolean isEnabled() {
        return ENABLED_RP_FEATURES.contains(getIdentifier());
    }

    @Override
    default boolean canBeEnabled() {
        return true;
    }
    @Override
    default void enable() {
        if(canBeEnabled()) {
            if(OTHER_YML_FILE == null) {
                save("_Data", "other.yml");
            }
            if(isEnabled()) {
                return;
            }
            final Feature feature = get_feature();
            final boolean has_feature = feature != null;
            try {
                final String identifier = getIdentifier();
                ENABLED_RP_FEATURES.add(identifier);
                final long started = System.currentTimeMillis();
                if(this instanceof EventAttributeListener) {
                    final EventAttributeListener event_attribute_listener = (EventAttributeListener) this;
                    EventAttributeCoreListener.registerEventAttributeListener(event_attribute_listener);
                }
                load();
                if(has_feature) {
                    sendConsoleDidLoadFeature(getAll(feature).size() + " " + identifier, started);
                } else {
                    sendConsoleDidLoadFeature(identifier, started);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(isEnabled()) {
                    PLUGIN_MANAGER.registerEvents(this, RANDOM_PACKAGE);
                }
            }
        }
    }
    @Override
    default void disable() {
        if(!isEnabled()) {
            return;
        }
        final long started = System.currentTimeMillis();
        final String identifier = getIdentifier();
        try {
            ENABLED_RP_FEATURES.remove(identifier);
            final Feature feature = get_feature();
            if(feature != null) {
                unregister(feature);
            }
            unload();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(!isEnabled()) {
                if(this instanceof EventAttributeListener) {
                    final EventAttributeListener event_attribute_listener = (EventAttributeListener) this;
                    EventAttributeCoreListener.unregisterEventAttributeListener(event_attribute_listener);
                }
                HandlerList.unregisterAll(this);
                sendConsoleMessage("&cDisabled RandomPackage Feature " + identifier + " (took " + (System.currentTimeMillis()-started) + "ms)");
            }
        }
    }

    @Override
    default @NotNull String colorize(@Nullable String input) {
        return input != null ? ChatColor.translateAlternateColorCodes('&', input) : "NULL";
    }

    default void saveOtherData() {
        try {
            OTHER_YML.save(OTHER_YML_FILE);
            OTHER_YML.load(OTHER_YML_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    default void viewGivedp(@NotNull Player player) {
        player.openInventory(Bukkit.createInventory(player, GIVEDP_INVENTORY.getSize(), GIVEDP_INVENTORY.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(GIVEDP_INVENTORY.getInventory().getContents());
        player.updateInventory();
    }
    default void addGivedpCategory(List<ItemStack> items, @NotNull UMaterial material, @NotNull String what, String invtitle) {
        final ItemStack item = material.getItemStack();
        if(item == null) {
            sendConsoleMessage("&cERROR: Caught by adding a Givedp Category: &f" + what);
        } else {
            final ItemMeta itemMeta = item.getItemMeta();
            if(itemMeta != null) {
                itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + what);
                item.setItemMeta(itemMeta);
            }
            GIVEDP_INVENTORY.getInventory().addItem(item);
            final int size = items.size();
            final Inventory inv = Bukkit.createInventory(null, size == 9 || size == 18 || size == 27 || size == 36 || size == 45 || size == 54 ? size : ((size+9)/9)*9, invtitle);
            for(ItemStack is : items) {
                if(is != null) {
                    inv.addItem(is);
                }
            }
            GIVEDP_CATEGORIES.add(inv);
        }
    }

    default boolean hasPermission(@Nullable CommandSender sender, @NotNull String permission, boolean sendNoPermMessage) {
        if(!(sender instanceof Player) || sender.hasPermission(permission)) {
            return true;
        } else if(sendNoPermMessage) {
            sendStringListMessage(sender, getStringList(RP_CONFIG, "no permission"), null);
        }
        return false;
    }

    @Nullable
    default ItemStack createItemStack(FileConfiguration config, String path) {
        return createItemStack(config, path, 0, 0.00f);
    }
    @Nullable
    default ItemStack createItemStack(FileConfiguration config, String path, int tier, float enchantMultiplier) {
        final String item_path = config == null ? path : config.getString(path + ".item");
        String item_path_lowercase = item_path.toLowerCase();

        int amount = config != null && config.get(path + ".amount") != null ? config.getInt(path + ".amount") : 1;
        final String name = config != null ? colorize(config.getString(path + ".name")) : null;
        final List<String> lore = config != null ? config.getStringList(path + ".lore") : null;
        return create_item_stack(path, item_path_lowercase, amount, name, lore, tier, enchantMultiplier);
    }

    @Nullable
    default ItemStack create_item_stack(@NotNull JSONObject json, String key) {
        return create_item_stack(json, key, 0, 0);
    }
    @Nullable
    default ItemStack create_item_stack(@NotNull JSONObject json, String key, int tier, float enchantMultiplier) {
        final JSONObject item_json = parse_json_in_json(json, key, null);
        if(item_json == null) {
            return null;
        }
        final String item_path_lowercase = item_json.optString("item");
        final String name = parse_string_in_json(item_json, "name");
        final List<String> lore = parse_list_string_in_json(item_json, "lore");
        return create_item_stack(null, item_path_lowercase, 1, name, lore, tier, enchantMultiplier);
    }
    @Nullable
    default ItemStack create_item_stack(@Nullable String path, @NotNull String item_path_lowercase, int amount, String name, List<String> lore, int tier, float enchantMultiplier) {
        ItemStack item;
        if(item_path_lowercase.contains(";amount=")) {
            final String amountString = item_path_lowercase.split("=")[1];
            final boolean isRange = item_path_lowercase.contains("-");
            final int min = isRange ? Integer.parseInt(amountString.split("-")[0]) : 0;
            amount = isRange ? min + RANDOM.nextInt(Integer.parseInt(amountString.split("-")[1])-min+1) : Integer.parseInt(amountString);
            path = path.split(";amount=")[0];
            item_path_lowercase = item_path_lowercase.split(";")[0];
        }
        final boolean hasChance = item_path_lowercase.contains("chance=");
        if(hasChance && RANDOM.nextInt(100) > Integer.parseInt(item_path_lowercase.split("chance=")[1].split(";")[0])) {
            return null;
        }
        if(item_path_lowercase.contains("spawner") && !item_path_lowercase.startsWith("mob_spawner") && !path.equals("mysterymobspawner")) {
            return getSpawner(item_path_lowercase);
        } else if(item_path_lowercase.startsWith("enchantedbook:")) {
            final String[] values = item_path_lowercase.split(":");
            final Enchantment enchant = getEnchantment(values[1]);
            if(enchant != null) {
                int level = 1;
                if(values.length == 3) {
                    final String[] ints = values[2].split("-");
                    final boolean isRandom = ints[0].equalsIgnoreCase("random");
                    final int min = isRandom ? 0 : Integer.parseInt(ints[0]);
                    level = isRandom ? 1+RANDOM.nextInt(enchant.getMaxLevel()) : ints[2].contains("-") ? min+RANDOM.nextInt(Integer.parseInt(ints[1])) : min;
                }
                item = new ItemStack(Material.ENCHANTED_BOOK, amount);
                final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                meta.addStoredEnchant(enchant, level, true);
                item.setItemMeta(meta);
                return item;
            }
            return null;
        }
        ItemStack parsedGivedpItem = GivedpItem.INSTANCE.valueOfRPItem(path);
        if(parsedGivedpItem == null) {
            parsedGivedpItem = GivedpItem.INSTANCE.valueOfRPItem(item_path_lowercase);
        }
        if(parsedGivedpItem != null) {
            item = parsedGivedpItem.clone();
            item.setAmount(amount);
            return item;
        }
        final String[] material = item_path_lowercase.toUpperCase().split(":");
        final String mat = material[0];
        final byte data = material.length == 2 ? Byte.parseByte(material[1]) : 0;
        final UMaterial umaterial = UMaterial.match(mat + (data != 0 ? ":" + data : ""));
        try {
            item = umaterial.getItemStack();
        } catch (Exception e) {
            sendConsoleMessage("&cERROR: UMaterial null itemstack. mat=" + mat + ";data=" + data + ";versionName=" + (umaterial != null ? umaterial.getMaterial().name() : null) + ";getMaterial()=" + (umaterial != null ? umaterial.getMaterial() : null));
            return null;
        }
        if(item == null) {
            sendConsoleMessage("&cERROR: Material=" + mat + ";umaterial=" + umaterial);
            return null;
        }
        final Material skull_item = UMaterial.PLAYER_HEAD_ITEM.getMaterial(), i = item.getType();
        if(!i.equals(Material.AIR)) {
            item.setAmount(amount);
            ItemMeta itemMeta = item.getItemMeta();
            if(i.equals(skull_item)) {
                final String owner = item_path_lowercase.contains(";owner=") ? item_path_lowercase.split("=")[1].split("}")[0].split(";")[0] : "RandomHashTags";
                final SkullMeta m = (SkullMeta) itemMeta;
                m.setOwner(owner);
                itemMeta = m;
            }
            itemMeta.setDisplayName(name != null ? colorize(name) : null);
            item.setItemMeta(itemMeta);

            if(lore != null) {
                item = updateLore(item, lore, tier, enchantMultiplier, CustomEnchants.INSTANCE.levelZeroRemoval, "null");
                itemMeta = item.getItemMeta();
            }
            item.setItemMeta(itemMeta);
            if(name != null && name.contains("{ENCHANT_SIZE}")) {
                applyTransmogScroll(item, getTransmogScroll("REGULAR"));
            }
        }
        return item;
    }
    default ItemStack updateLore(ItemStack is, List<String> toLore, int tier, float enchantMultiplier, boolean levelzeroremoval, String max) {
        if(is != null && toLore != null && !toLore.isEmpty()) {
            final ItemMeta meta = is.getItemMeta();
            if(meta != null) {
                final LinkedHashMap<Enchantment, Integer> enchants = new LinkedHashMap<>();
                final List<ItemFlag> flags = new ArrayList<>();
                final List<String> lore = new ArrayList<>();
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
                            final CustomEnchantSpigot enchant = valueOfCustomEnchant(s);
                            if(enchant != null && enchant.isEnabled()) {
                                final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
                                if(rarity != null) {
                                    int target_enchant_level = getRemainingInt(s), x = (int) (enchant.getMaxLevel() * enchantMultiplier);
                                    target_enchant_level = target_enchant_level != -1 ? target_enchant_level : x+ RANDOM.nextInt(enchant.getMaxLevel()-x+1);
                                    if(target_enchant_level != 0 || !levelzeroremoval)
                                        lore.add(rarity.getApplyColors() + enchant.getName() + " " + toRoman(target_enchant_level != 0 ? target_enchant_level : 1));
                                } else {
                                    sendConsoleMessage("&cWARNING&e: No EnchantRarity found for enchant \"" + enchant.getName() + "\"!");
                                }
                            }
                        }
                    } else if(string.startsWith("{") && (!stringLC.contains("reqlevel=") && stringLC.contains("chance=") || stringLC.contains("reqlevel=") && tier >= Integer.parseInt(stringLC.split("reqlevel=")[1].split(":")[0]))) {
                        final CustomEnchantSpigot enchant = valueOfCustomEnchant(string.split("\\{")[1].split("}")[0], true);
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
        return is;
    }

    default void applyTransmogScroll(ItemStack is, TransmogScroll scroll) {
        final Scrolls scrolls = Scrolls.INSTANCE;
        if(scrolls.isEnabled() && scrolls.isEnabled(Feature.SCROLL_TRANSMOG)) {
            scrolls.applyTransmogScroll(null, is, scroll);
        }
    }
}
