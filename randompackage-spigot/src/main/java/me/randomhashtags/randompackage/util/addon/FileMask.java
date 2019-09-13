package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.Mask;
import me.randomhashtags.randompackage.addon.util.Skullable;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;

public class FileMask extends RPAddon implements Mask, Skullable {
    private ItemStack item;

    public FileMask(File f) {
        load(f);
        addMask(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getOwner() {
        final String tex = yml.getString("texture");
        return tex != null ? tex : yml.getString("owner");
    }
    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
            if(item != null) {
                final ItemMeta im = item.getItemMeta();
                item = getSkull(im.getDisplayName(), im.getLore(), LEGACY || THIRTEEN);
            }
        }
        return getClone(item);
    }
    public boolean canBeApplied(ItemStack is) {
        return is != null && is.getType().name().endsWith("_HELMET") && getMaskOnItem(is) == null;
    }
    public String getApplied() {
        final Object o = yml.get("added lore"); // changed from List<String> to String in v16.4.0
        String string;
        if(o instanceof List) string = ((List<String>) o).get(0);
        else string = (String) o;
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
    public List<String> getAppliesTo() { return null; }
}
