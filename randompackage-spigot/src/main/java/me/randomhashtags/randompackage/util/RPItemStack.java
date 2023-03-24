package me.randomhashtags.randompackage.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface RPItemStack extends Versionable {
    default void removeRPItemStackValue(@NotNull ItemStack is, String key) {
        removeRPItemStackValues(is, key);
    }
    default void removeRPItemStackValues(@NotNull ItemStack is, String...keys) {
        edit_itemstack_nbt(is, keys, null);
    }
    default void addRPItemStackValue(@NotNull ItemStack is, String key, String value) {
        addRPItemStackValues(is, new HashMap<>() {{
            put(key, value);
        }});
    }
    default void addRPItemStackValues(@NotNull ItemStack is, @NotNull HashMap<String, String> values) {
        edit_itemstack_nbt(is, null, values);
    }
    default void edit_itemstack_nbt(@NotNull ItemStack is, @Nullable String[] removedKeys, @Nullable HashMap<String, String> addedKeys) {
        ItemMeta itemMeta = null;
        final ReflectedCraftItemStack reflected_craft_itemstack = ReflectedCraftItemStack.shared_instance();
        if(reflected_craft_itemstack != null) {
            try {
                final Method nms_itemstack_get_item_meta_function = reflected_craft_itemstack.net_class.getMethod("getItemMeta");
                final Object nms_item = reflected_craft_itemstack.as_nms_copy_function.invoke(null, is);
                final Object tag = reflected_craft_itemstack.tag_compound_get_tag_function.invoke(nms_item);
                if(tag != null) {
                    if(removedKeys != null) {
                        for(String s : removedKeys) {
                            reflected_craft_itemstack.tag_compound_remove_function.invoke(tag, s);
                        }
                    } else if(addedKeys != null) {
                        for(Map.Entry<String, String> entry : addedKeys.entrySet()) {
                            final String key = entry.getKey(), value = entry.getValue();
                            reflected_craft_itemstack.tag_compound_set_string_function.invoke(tag, key, value);
                        }
                    }
                }
                final Object craft_itemstack_mirror = reflected_craft_itemstack.as_craft_mirror_function.invoke(nms_item, nms_item);
                itemMeta = (ItemMeta) nms_itemstack_get_item_meta_function.invoke(craft_itemstack_mirror);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(itemMeta != null) {
                is.setItemMeta(itemMeta);
            }
        }
    }
    @Nullable
    default String getRPItemStackValue(@NotNull ItemStack is, String key) {
        try {
            final ReflectedCraftItemStack reflected_craft_itemstack = ReflectedCraftItemStack.shared_instance();
            if(reflected_craft_itemstack != null) {
                final Object nmsItem = reflected_craft_itemstack.as_nms_copy_function.invoke(null, is);
                if(nmsItem != null) {
                    final Object has_tag = reflected_craft_itemstack.has_tag_function.invoke(nmsItem);
                    if(has_tag instanceof Boolean && (Boolean) has_tag) {
                        final Object tag = reflected_craft_itemstack.get_tag_function.invoke(nmsItem);
                        if(tag != null) {
                            final Object has_key = reflected_craft_itemstack.tag_compound_has_key_function.invoke(tag, key);
                            if(has_key instanceof Boolean && (Boolean) has_key) {
                                final Object tag_value = reflected_craft_itemstack.tag_compound_get_string_function.invoke(tag, key);
                                return tag_value instanceof String ? (String) tag_value : null;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    default String asNMSCopy(@NotNull ItemStack itemstack) {
        final ReflectedCraftItemStack reflected_craft_itemstack = ReflectedCraftItemStack.shared_instance();
        if(reflected_craft_itemstack != null) {
            try {
                final Object craft_itemstack = reflected_craft_itemstack.as_nms_copy_function.invoke(null, itemstack);
                final Class<?> tag_compound_class = reflected_craft_itemstack.tag_compound_class;
                final Object saved_tag_compound = reflected_craft_itemstack.save_function.invoke(craft_itemstack, tag_compound_class.getConstructor().newInstance());
                final Object tag_compound_to_string = reflected_craft_itemstack.tag_compound_to_string_function.invoke(saved_tag_compound);
                return tag_compound_to_string instanceof String ? (String) tag_compound_to_string : null;
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
