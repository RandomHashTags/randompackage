package me.randomhashtags.randompackage.addons.usingfile;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.randomhashtags.randompackage.addons.Mask;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class FileMask extends Mask {
    private static TreeMap<String, ItemStack> maskOwners;
    private List<String> addedLore;
    private ItemStack item;

    public FileMask(File f) {
        load(f);
        addMask(getIdentifier(), this);
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
    public List<String> getAddedLore() {
        if(addedLore == null) addedLore = api.colorizeListString(yml.getStringList("added lore"));
        return addedLore;
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }

    // https://www.spigotmc.org/threads/tutorial-player-skull-with-custom-skin.143323/ , edited by RandomHashTags
    private ItemStack getSkull(String skinURL, String name, List<String> lore) {
        final ItemStack head = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
        if(skinURL.isEmpty()) return head;
        final SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(name);
        headMeta.setLore(lore);
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        final byte[] encoded;
        if(version.contains("1.8") || version.contains("1.9") || version.contains("1.10") || version.contains("1.11") || version.contains("1.12") || version.contains("1.13")) {
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
