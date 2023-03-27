package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Mask;
import me.randomhashtags.randompackage.addon.util.Skullable;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public final class FileMask extends RPAddonSpigot implements Mask, Skullable {

    private final String owner;
    private final ItemStack item;
    private final String applied_string;
    private final List<String> attributes;

    public FileMask(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        owner = parse_string_in_json(json, "texture", parse_string_in_json(json, "owner"));
        ItemStack item = create_item_stack(json, "item");
        if(item != null) {
            final ItemMeta im = item.getItemMeta();
            item = getSkull(im.getDisplayName(), im.getLore(), LEGACY || THIRTEEN);
        }
        this.item = item;
        applied_string = parse_string_in_json(json, "added lore");
        attributes = parse_list_string_in_json(json, "attributes");
        register(Feature.MASK, this);
    }
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @NotNull
    public String getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    public boolean canBeApplied(@NotNull ItemStack is) {
        return is.getType().name().endsWith("_HELMET") && getMaskOnItem(is) == null;
    }
    public @NotNull String getAppliedString() {
        return applied_string;
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
    public @NotNull List<String> getAppliesTo() {
        return null;
    }
}
