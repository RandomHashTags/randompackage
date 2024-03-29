package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.ItemSkin;
import me.randomhashtags.randompackage.addon.file.FileItemSkin;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPItemStack;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum ItemSkins implements RPFeatureSpigot, CommandExecutor, RPItemStack {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory gui;
    private ItemStack skin;
    private String appliedLore;
    private HashMap<String, String> materials;
    private HashMap<ItemSkin, ItemStack> cache;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.ITEM_SKIN;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
        }
        return true;
    }

    @Override
    public void load() {
        save("item skins", "_settings.yml");

        if(!OTHER_YML.getBoolean("saved default item skins")) {
            generateDefaultItemSkins();
            OTHER_YML.set("saved default item skins", true);
            saveOtherData();
        }

        final String folder = DATA_FOLDER + SEPARATOR + "item skins";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));
        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        skin = createItemStack(config, "item");
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
        addGivedpCategory(list, UMaterial.LEATHER, "Item Skins", "Givedp: Item Skins");
    }
    @Override
    public void unload() {
    }

    public ItemStack getItemSkinItem(@NotNull ItemSkin skin, boolean fromCache) {
        if(fromCache) {
            return cache.getOrDefault(skin, null);
        }
        final String name = getLocalizedName(skin), material = getItemSkinMaterialString(skin);
        final ItemStack is = getClone(this.skin);
        final ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", name).replace("{MATERIAL}", material));
        final List<String> lore = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{NAME}", name).replace("{MATERIAL}", material));
        }
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        cache.put(skin, is);
        return is;
    }
    @Nullable
    public String getItemSkinMaterialString(@NotNull ItemSkin skin) {
        return materials.get(skin.getMaterial());
    }
    @Nullable
    public ItemSkin valueOfItemSkin(@NotNull ItemStack is) {
        return isItemSkin(is) ? getItemSkin(getRPItemStackValue(is, "AppliedItemSkin")) : null;
    }
    @Nullable
    public ItemSkin valueOfItemSkinApplied(@NotNull ItemStack is) {
        if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> lore = is.getItemMeta().getLore();
            for(ItemSkin skin : getAllItemSkins().values()) {
                if(lore.contains(appliedLore.replace("{NAME}", getLocalizedName(skin)))) {
                    return skin;
                }
            }
        }
        return null;
    }

    public boolean isItemSkin(@NotNull ItemStack is) {
        return getRPItemStackValue(is, "AppliedItemSkin") != null;
    }
    public boolean applyItemSkin(@NotNull ItemStack is, @NotNull ItemSkin skin) {
        if(isItemSkin(is) && is.getType().name().endsWith(skin.getMaterial())) {
            final ItemMeta meta = is.getItemMeta();
            final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(appliedLore.replace("{NAME}", getLocalizedName(skin)));
            meta.setLore(lore); lore.clear();
            is.setItemMeta(meta);
            addRPItemStackValue(is, "AppliedItemSkin", skin.getIdentifier());
            return true;
        }
        return false;
    }
    public boolean removeItemSkin(@NotNull ItemStack is, @NotNull ItemSkin appliedSkin) {
        if(!isItemSkin(is)) {
            final ItemMeta meta = is.getItemMeta();
            final List<String> lore = meta.getLore();
            lore.remove(appliedLore.replace("{NAME}", getLocalizedName(appliedSkin)));
            meta.setLore(lore);
            removeRPItemStackValue(is, "AppliedItemSkin");
            return true;
        }
        return false;
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
        if(skin != null && applied == null) {
            if(applyItemSkin(current, skin)) {
                event.setCurrentItem(current);
                final int amount = cursor.getAmount();
                if(amount == 1) {
                    event.setCursor(new ItemStack(Material.AIR));
                } else {
                    cursor.setAmount(amount-1);
                }
            } else {
                return;
            }
        } else if(applied != null) {
            if(click.contains("RIGHT")) {
                removeItemSkin(current, applied);
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
