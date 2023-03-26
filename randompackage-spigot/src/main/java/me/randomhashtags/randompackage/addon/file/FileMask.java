package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Mask;
import me.randomhashtags.randompackage.addon.util.Skullable;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class FileMask extends RPAddonSpigot implements Mask, Skullable {
    private ItemStack item;

    public FileMask(File f) {
        super(f);
        register(Feature.MASK, this);
    }
    public @NotNull String getIdentifier() {
        return identifier;
    }

    public String getOwner() {
        final String tex = yml.getString("texture");
        return tex != null ? tex : yml.getString("owner");
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        if(item == null) {
            item = createItemStack(yml, "item");
            if(item != null) {
                final ItemMeta im = item.getItemMeta();
                item = getSkull(im.getDisplayName(), im.getLore(), LEGACY || THIRTEEN);
            }
        }
        return getClone(item);
    }
    public boolean canBeApplied(@NotNull ItemStack is) {
        return is.getType().name().endsWith("_HELMET") && getMaskOnItem(is) == null;
    }
    public String getApplied() {
        final Object o = yml.get("added lore"); // changed from List<String> to String in v16.4.0
        String string;
        if(o instanceof List) string = ((List<String>) o).get(0);
        else string = (String) o;
        return colorize(string);
    }
    public @NotNull List<String> getAttributes() {
        return yml.getStringList("attributes");
    }
    public List<String> getAppliesTo() {
        return null;
    }
}
