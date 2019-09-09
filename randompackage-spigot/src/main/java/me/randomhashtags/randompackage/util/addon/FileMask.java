package me.randomhashtags.randompackage.util.addon;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.randomhashtags.randompackage.addon.Mask;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class FileMask extends RPAddon implements Mask {
    private static TreeMap<String, ItemStack> maskOwners;
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
            final String owner = getOwner();
            item = api.d(yml, "item");
            if(item != null) {
                if(maskOwners == null) maskOwners = new TreeMap<>();
                if(maskOwners.containsKey(getOwner())) {
                    item = maskOwners.get(owner);
                } else if(owner.startsWith("http")) {
                    final ItemMeta im = item.getItemMeta();
                    item = getSkull(owner, im.getDisplayName(), im.getLore());
                } else {
                    final SkullMeta sm = (SkullMeta) item.getItemMeta();
                    sm.setOwner(owner);
                    item.setItemMeta(sm);
                    maskOwners.put(owner, item);
                }
            }
        }
        return item != null ? item.clone() : null;
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

    // https://www.spigotmc.org/threads/143323/ , edited by RandomHashTags
    private ItemStack getSkull(String skinURL, String name, List<String> lore) {
        final ItemStack head = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
        if(skinURL.isEmpty()) return head;
        final SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(name);
        headMeta.setLore(lore);
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        final byte[] encoded;
        if(isLegacy || version.contains("1.13")) {
            encoded = org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", skinURL).getBytes());
        } else {
            encoded = org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", skinURL).getBytes());
        }
        profile.getProperties().put("textures", new Property("textures", new String(encoded)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch(Exception e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
