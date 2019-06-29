package me.randomhashtags.randompackage.utils.abstraction;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public abstract class AbstractMask extends Saveable {
    private static TreeMap<String, ItemStack> maskOwners = new TreeMap<>();
    private static String version = Bukkit.getVersion();

    private String owner;
    private List<String> addedLore;
    private ItemStack item;

    public String getOwner() {
        if(owner == null) {
            final String tex = yml.getString("texture");
            owner = tex != null ? tex : yml.getString("owner");
        }
        return owner;
    }
    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
            if(item != null) {
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
