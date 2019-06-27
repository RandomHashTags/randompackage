package me.randomhashtags.randompackage.utils.classes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class Mask {
    private static HashMap<String, ItemStack> maskOwners;
    public static HashMap<String, Mask> masks;
    private static String version;

    private YamlConfiguration yml;
    private String ymlName, owner;
    private List<String> addedLore, attributes;
    private ItemStack item;

    public Mask(File f) {
        if(masks == null) {
            maskOwners = new HashMap<>();
            masks = new HashMap<>();
            version = api.version;
        }
        yml = YamlConfiguration.loadConfiguration(f);
        ymlName = f.getName().split("\\.yml")[0];
        masks.put(ymlName, this);
    }

    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return ymlName; }
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
    public List<String> getAttributes() {
        if(attributes == null) attributes = yml.getStringList("attributes");
        return attributes;
    }

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

    public static Mask valueOf(ItemStack is) {
        if(masks != null && is != null && is.hasItemMeta()) {
            for(Mask m : masks.values()) {
                final ItemStack i = m.getItem();
                if(i.isSimilar(is))
                    return m;
            }
        }
        return null;
    }
    public static Mask getOnItem(ItemStack is) {
        if(masks != null) {
            final ItemMeta im = is != null ? is.getItemMeta() : null;
            if(im != null && im.hasLore()) {
                final List<String> l = im.getLore();
                for(Mask m : masks.values())
                    if(l.containsAll(m.getAddedLore()))
                        return m;
            }
        }
        return null;
    }

    public static void deleteAll() {
        maskOwners = null;
        masks = null;
        version = null;
    }
}
