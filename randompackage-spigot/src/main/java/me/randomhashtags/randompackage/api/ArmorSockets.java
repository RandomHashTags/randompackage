package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.ArmorSocket;
import me.randomhashtags.randompackage.addon.file.MultilingualStringSpigotValue;
import me.randomhashtags.randompackage.addon.file.PathArmorSocket;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum ArmorSockets implements RPFeatureSpigot {
    INSTANCE;

    public YamlConfiguration config;
    private ItemStack socket;
    private HashMap<String, String> itemTypes;
    private int chanceSlot;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.ARMOR_SOCKET;
    }

    @Override
    public void load() {
        save(null, "armor sockets.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "armor sockets.yml"));

        socket = createItemStack(config, "item");
        final List<String> lore = socket.getItemMeta().getLore();
        for(int i = 0; i < lore.size(); i++) {
            if(lore.get(i).contains("{CHANCE}")) {
                chanceSlot = i;
            }
        }

        itemTypes = new HashMap<>();
        for(String key : getConfigurationSectionKeys(config, "item types", false)) {
            itemTypes.put(key, config.getString("item types." + key));
        }

        final List<ItemStack> list = new ArrayList<>();
        for(String key : getConfigurationSectionKeys(config, "types", false)) {
            final String target_name = config.getConfigurationSection("types").getConfigurationSection(key).getString("name");
            final MultilingualStringSpigotValue name = new MultilingualStringSpigotValue(target_name);
            final ArmorSocket socket = new PathArmorSocket(key, name);
            list.add(getArmorSocketItem(socket, 100));
        }
        addGivedpCategory(list, UMaterial.TRIPWIRE_HOOK, "Armor Sockets", "Givedp: Armor Sockets");
    }
    @Override
    public void unload() {
    }

    public ItemStack getArmorSocketItem(@NotNull ArmorSocket socket, int chance) {
        final String name = getLocalizedName(socket), nameLC = name.toLowerCase(), itemType = itemTypes.get(socket.getItemType()), limit = Integer.toString(socket.getLimit()), chanceString = Integer.toString(Math.min(chance, 100));
        final ItemStack item = getClone(this.socket);
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", name));
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{NAME}", name).replace("{ITEM_TYPE}", itemType).replace("{NAME_LC}", nameLC).replace("{LIMIT}", limit).replace("{CHANCE}", chanceString));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public int getChanceSlot() {
        return chanceSlot;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        if(current == null || current.getType().equals(Material.AIR) || cursor == null || cursor.getType().equals(Material.AIR)) {
            return;
        }
        final ArmorSocket socket = valueOfArmorSocket(cursor);
        if(socket != null) {
            final String material = current.getType().name();
            boolean allowed = false;
            for(String allowedMaterial : socket.getItemType().split(";")) {
                if(material.endsWith(allowedMaterial.toUpperCase())) {
                    allowed = true;
                    break;
                }
            }
            if(allowed) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
            }
        }
    }
}
