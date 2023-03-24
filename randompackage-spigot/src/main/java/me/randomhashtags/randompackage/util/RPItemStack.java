package me.randomhashtags.randompackage.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

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
        final ReflectedCraftItemStack reflected_craft_itemstack = ReflectedCraftItemStack.shared_instance();
        if(reflected_craft_itemstack != null) {
            reflected_craft_itemstack.edit_itemstack_nbt(is, removedKeys, addedKeys);
        }
    }
    @Nullable
    default String getRPItemStackValue(@NotNull ItemStack is, @NotNull String key) {
        final ReflectedCraftItemStack reflected_craft_itemstack = ReflectedCraftItemStack.shared_instance();
        return reflected_craft_itemstack != null ? reflected_craft_itemstack.get_tag(is, key) : null;
    }
    @Nullable
    default String asNMSCopy(@NotNull ItemStack itemstack) {
        final ReflectedCraftItemStack reflected_craft_itemstack = ReflectedCraftItemStack.shared_instance();
        return reflected_craft_itemstack != null ? reflected_craft_itemstack.as_nms_copy(itemstack) : null;
    }
}
