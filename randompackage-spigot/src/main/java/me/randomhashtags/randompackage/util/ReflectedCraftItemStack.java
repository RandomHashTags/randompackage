package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public final class ReflectedCraftItemStack {
    private static ReflectedCraftItemStack INSTANCE;
    private static boolean SENT_ERROR = false;
    @Nullable
    public static ReflectedCraftItemStack shared_instance() {
        if(!SENT_ERROR && INSTANCE == null) {
            final String version = get_craftbukkit_version();
            try {
                if(version != null) {
                    INSTANCE = new ReflectedCraftItemStack(RandomPackage.INSTANCE.getClass().getClassLoader(), version);
                } else {
                    throw new Exception("failed to get craftbukkit version");
                }
            } catch (Exception e) {
                if(!SENT_ERROR) {
                    SENT_ERROR = true;
                    e.printStackTrace();
                    RandomPackageAPI.INSTANCE.sendConsoleErrorMessage("ReflectedCraftItemStack", "Failed to find CraftBukkit version, some features will not work properly!");
                }
            }
        }
        return INSTANCE;
    }
    private static String get_craftbukkit_version() {
        String returned_version = null;
        final ClassLoader class_loader = RandomPackage.INSTANCE.getClass().getClassLoader();
        loop : for(int major = 1; major <= 2; major++) {
            for(int minor = 8; minor <= 50; minor++) {
                for(int release = 1; release <= 5; release++) {
                    final String version = "v" + major + "_" + minor + "_R" + release;
                    Class<?> bruh = null;
                    final String[] tries = {
                            "org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack"
                    };
                    for(String target_class_name : tries) {
                        try {
                            bruh = Class.forName(target_class_name, false, class_loader);
                        } catch (Exception ignored) {
                        } finally {
                            if(bruh != null) {
                                returned_version = version;
                            }
                        }
                    }
                    if(returned_version != null) {
                        break loop;
                    }
                }
            }
        }
        return returned_version;
    }

    @NotNull public final String version;
    @NotNull private final Method as_nms_copy_function;
    @NotNull private final Method as_craft_mirror_function;
    @NotNull private final Class<?> tag_compound_class;
    @NotNull public final Constructor<?> tag_compound_constructor;
    @NotNull private final Method tag_compound_remove_function, tag_compound_set_string_function, tag_compound_to_string_function, tag_compound_has_key_function, tag_compound_get_string_function;
    @NotNull private final Method get_item_meta_function, has_tag_function, get_tag_function, save_function;

    private ReflectedCraftItemStack(@NotNull ClassLoader class_loader, @NotNull String version) throws Exception {
        this.version = version;
        final Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack", false, class_loader);
        as_nms_copy_function = clazz.getMethod("asNMSCopy", ItemStack.class);
        final Class<?> net_class = parse_class("ItemStack", class_loader, List.of("net.minecraft.server." + version + ".ItemStack", "net.minecraft.world.item.ItemStack"));
        as_craft_mirror_function = clazz.getMethod("asCraftMirror", net_class);

        tag_compound_class = parse_class("NBTTagCompound", class_loader, List.of("net.minecraft.server." + version + ".NBTTagCompound", "net.minecraft.nbt.NBTTagCompound"));
        tag_compound_constructor = tag_compound_class.getConstructor();
        tag_compound_to_string_function = tag_compound_class.getMethod("toString");

        tag_compound_remove_function = parse_method("remove", tag_compound_class, List.of("r"), String.class);
        tag_compound_set_string_function = parse_method("setString", tag_compound_class, List.of("r"), String.class, String.class);

        get_item_meta_function = clazz.getMethod("getItemMeta");
        has_tag_function = parse_method("hasTag", net_class, List.of("t"));

        get_tag_function = parse_method("getTag", net_class, List.of("u"));
        tag_compound_has_key_function = parse_method("hasKey", tag_compound_class, List.of("e"), String.class);
        tag_compound_get_string_function = parse_method("getString", tag_compound_class, List.of("l"), String.class);
        save_function = parse_method("save", net_class, List.of("b"), tag_compound_class);
    }

    @Nullable
    private Class<?> parse_class(@NotNull String type, @NotNull ClassLoader loader, List<String> aliases) {
        for(String name : aliases) {
            try {
                return Class.forName(name, false, loader);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Nullable
    private Method parse_method(@NotNull String type, @NotNull Class<?> parent_class, @NotNull List<String> aliases, Class<?>...parameters) {
        final List<String> names = new ArrayList<>(Collections.singletonList(type));
        names.addAll(aliases);
        for(String name : names) {
            try {
                return parent_class.getMethod(name, parameters);
            } catch (Exception ignored) {
            }
        }
        RandomPackageAPI.INSTANCE.sendConsoleErrorMessage("ReflectedCraftItemStack", "Failed to find method via reflection with name \"" + type + "\"! Errors will happen!");
        return null;
    }

    @Nullable
    public String get_tag(@NotNull ItemStack itemstack, @NotNull String key) {
        try {
            final Object nmsItem = as_nms_copy_function.invoke(null, itemstack);
            if(nmsItem != null && (Boolean) has_tag_function.invoke(nmsItem)) {
                final Object tag_compound = get_tag_function.invoke(nmsItem);
                if(tag_compound != null && (Boolean) tag_compound_has_key_function.invoke(tag_compound, key)) {
                    return (String) tag_compound_get_string_function.invoke(tag_compound, key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public String as_nms_copy(@NotNull ItemStack itemstack) {
        try {
            final Object craft_itemstack = as_nms_copy_function.invoke(null, itemstack);
            final Object saved_tag_compound = save_function.invoke(craft_itemstack, tag_compound_class.getConstructor().newInstance());
            return (String) tag_compound_to_string_function.invoke(saved_tag_compound);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void edit_itemstack_nbt(@NotNull ItemStack is, @Nullable String[] removedKeys, @Nullable HashMap<String, String> addedKeys) {
        try {
            final Object nms_item = as_nms_copy_function.invoke(null, is);
            final Object tag = get_tag_function.invoke(nms_item);
            if(tag != null) {
                if(removedKeys != null) {
                    for(String s : removedKeys) {
                        tag_compound_remove_function.invoke(tag, s);
                    }
                } else if(addedKeys != null) {
                    for(Map.Entry<String, String> entry : addedKeys.entrySet()) {
                        tag_compound_set_string_function.invoke(tag, entry.getKey(), entry.getValue());
                    }
                }
            }
            final Object craft_itemstack_mirror = as_craft_mirror_function.invoke(null, nms_item);
            final ItemMeta itemMeta = (ItemMeta) get_item_meta_function.invoke(craft_itemstack_mirror);
            is.setItemMeta(itemMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
