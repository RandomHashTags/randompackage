package me.randomhashtags.randompackage.addon.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.ReflectedCraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Skullable extends UVersionableSpigot {
    HashMap<String, ItemStack> CACHED_SKULLS = new HashMap<>();
    String getOwner();
    // https://www.spigotmc.org/threads/143323/ , edited by RandomHashTags
    @NotNull
    default ItemStack getSkull(String name, List<String> lore, boolean isLegacy) {
        final String skinURL = getOwner();

        if(CACHED_SKULLS.containsKey(skinURL)) {
            return CACHED_SKULLS.get(skinURL);
        }
        final ItemStack head = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
        final SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(name);
        headMeta.setLore(lore);
        head.setItemMeta(headMeta);
        if(skinURL == null) {
            return head;
        }

        if(skinURL.isEmpty()) {
            return head;
        } else if(skinURL.startsWith("http")) {
            final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            final String skin_value = "{SKIN:{url:\"%s\"}}";
            final byte[] encoded = base64Encode(String.format("{textures:" + skin_value + "}", skinURL));
            profile.getProperties().put("textures", new Property("textures", new String(encoded)));
            Field profile_variable = null;
            try {
                // CraftMetaSkull
                final Class<?> head_meta_class = headMeta.getClass();
                profile_variable = head_meta_class.getDeclaredField("profile");
                profile_variable.setAccessible(true);
                profile_variable.set(headMeta, profile);
                profile_variable.setAccessible(false);
                final ReflectedCraftItemStack shared_instance = ReflectedCraftItemStack.shared_instance();
                if(shared_instance != null) {
                    try {
                        final Field serialized_profile_variable = head_meta_class.getDeclaredField("serializedProfile");
                        final Object tag_compound = shared_instance.tag_compound_constructor.newInstance();
                        shared_instance.tag_compound_set_string_function.invoke(tag_compound, "textures", skin_value);
                        serialized_profile_variable.setAccessible(true);
                        serialized_profile_variable.set(headMeta, tag_compound);
                        serialized_profile_variable.setAccessible(false);
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            headMeta.setOwner(skinURL);
        }
        head.setItemMeta(headMeta);
        CACHED_SKULLS.put(skinURL, head);
        return head;
    }
    default byte[] base64Encode(String input) {
        try {
            return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8)).getBytes();
        } catch (Exception e) {
            return null;
        }
    }
}
