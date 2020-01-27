package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.ItemSkin;
import me.randomhashtags.randompackage.addon.file.FileItemSkin;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemSkins extends RPFeature implements CommandExecutor {
    private static ItemSkins instance;
    public static ItemSkins getItemSkins() {
        if(instance == null) instance = new ItemSkins();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private ItemStack skin;
    private String appliedLore;
    private HashMap<String, String> materials;
    private HashMap<ItemSkin, ItemStack> cache;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
        }
        return true;
    }

    public String getIdentifier() {
        return "ITEM_SKINS";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save("item skins", "_settings.yml");

        if(!otherdata.getBoolean("saved default item skins")) {
            generateDefaultItemSkins();
            otherdata.set("saved default item skins", true);
            saveOtherData();
        }

        final String folder = DATA_FOLDER + SEPARATOR + "item skins";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));
        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        skin = d(config, "item");
        appliedLore = colorize(config.getString("item.applied lore"));
        materials = new HashMap<>();
        for(String s : getConfigurationSectionKeys(config, "materials", false)) {
            materials.put(s, colorize(config.getString("materials." + s)));
        }

        cache = new HashMap<>();
        final List<ItemStack> list = new ArrayList<>();
        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final ItemSkin skin = new FileItemSkin(f);
                list.add(getItemSkinItem(skin, false));
            }
        }
        addGivedpCategory(list, UMaterial.DRAGON_HEAD, "Item Skins", "Givedp: Item Skins");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.ITEM_SKIN).size() + " Item Skins &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.ITEM_SKIN);
    }

    public ItemStack getItemSkinItem(@NotNull ItemSkin skin, boolean fromCache) {
        if(fromCache) {
            return cache.getOrDefault(skin, null);
        }
        final String name = skin.getName(), material = getItemSkinMaterialString(skin);
        final ItemStack is = getClone(this.skin);
        itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", name).replace("{MATERIAL}", material));
        lore.clear();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{NAME}", name).replace("{MATERIAL}", material));
        }
        itemMeta.setLore(lore);
        lore.clear();
        is.setItemMeta(itemMeta);
        cache.put(skin, is);
        return is;
    }
    public String getItemSkinMaterialString(@NotNull ItemSkin skin) {
        return materials.get(skin.getMaterial());
    }
    public ItemSkin valueOfItemSkin(@NotNull ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            for(ItemSkin skin : getAllItemSkins().values()) {
                if(cache.containsKey(skin) && is.isSimilar(cache.get(skin))) {
                    return skin;
                }
            }
        }
        return null;
    }
    public ItemSkin valueOfItemSkinApplied(@NotNull ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> lore = is.getItemMeta().getLore();
            for(ItemSkin skin : getAllItemSkins().values()) {
                if(lore.contains(appliedLore.replace("{NAME}", skin.getName()))) {
                    return skin;
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        final String click = event.getClick().name();
        if(current == null || current.getType().equals(Material.AIR) || !(click.contains("RIGHT") || click.contains("LEFT"))) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        final ItemSkin skin = valueOfItemSkin(cursor), applied = valueOfItemSkinApplied(current);
        itemMeta = current.getItemMeta();
        if(skin != null && applied == null) {
            if(!current.getType().name().endsWith(skin.getMaterial())) {
                return;
            }
            lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
            lore.add(appliedLore.replace("{NAME}", skin.getName()));
            itemMeta.setLore(lore); lore.clear();
            current.setItemMeta(itemMeta);
            event.setCurrentItem(current);
            final int amount = cursor.getAmount();
            if(amount == 1) {
                event.setCursor(new ItemStack(Material.AIR));
            } else {
                cursor.setAmount(amount-1);
            }
        } else if(applied != null) {
            if(click.contains("RIGHT")) {
                itemMeta = current.getItemMeta();
                lore = itemMeta.getLore();
                lore.remove(appliedLore.replace("{NAME}", applied.getName()));
                itemMeta.setLore(lore); lore.clear();
                current.setItemMeta(itemMeta);
                event.setCurrentItem(current);
                event.setCursor(getItemSkinItem(applied, true));
            } else {
                return;
            }
        } else {
            return;
        }
        event.setCancelled(true);
        player.updateInventory();
    }
}
