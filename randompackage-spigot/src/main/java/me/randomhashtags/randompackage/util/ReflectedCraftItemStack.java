package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
                    RandomPackageAPI.INSTANCE.sendConsoleMessage("&6[RandomPackage] &cFailed to find craftbukkit version, some features will not work properly!");
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

    public final String version;
    public final Class<?> clazz;
    public final Class<?> net_class;
    public final Method as_nms_copy_function;
    public final Method as_craft_mirror_function;
    public final Class<?> tag_compound_class;
    public final Method tag_compound_remove_function, tag_compound_set_string_function, tag_compound_to_string_function, tag_compound_has_key_function, tag_compound_get_string_function;
    public final Method get_item_meta_function, has_tag_function, get_tag_function, save_function;

    private ReflectedCraftItemStack(@NotNull ClassLoader class_loader, @NotNull String version) throws Exception {
        this.version = version;
        clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack", false, class_loader);
        as_nms_copy_function = clazz.getMethod("asNMSCopy", ItemStack.class);
        Class<?> net_class;
        try {
            net_class = Class.forName("net.minecraft.server." + version + ".ItemStack", false, class_loader);
        } catch (Exception e) {
            net_class = Class.forName( "net.minecraft.world.item.ItemStack", false, class_loader);
        }
        this.net_class = net_class;
        as_craft_mirror_function = clazz.getMethod("asCraftMirror", net_class);

        Class<?> tag_compound_class;
        try {
            tag_compound_class = Class.forName("net.minecraft.server." + version + ".NBTTagCompound", false, class_loader);
        } catch (Exception ignored) {
            tag_compound_class = Class.forName("net.minecraft.nbt.NBTTagCompound", false, class_loader);
        }
        this.tag_compound_class = tag_compound_class;
        tag_compound_to_string_function = tag_compound_class.getMethod("toString");

        Method tag_compound_remove_function, tag_compound_set_string_function;
        try {
            tag_compound_remove_function = tag_compound_class.getMethod("remove", String.class);
            tag_compound_set_string_function = tag_compound_class.getMethod("setString", String.class, String.class);
        } catch (Exception ignored) {
            tag_compound_remove_function = tag_compound_class.getMethod("r", String.class);
            tag_compound_set_string_function = tag_compound_class.getMethod("a", String.class, String.class);
        }
        this.tag_compound_remove_function = tag_compound_remove_function;
        this.tag_compound_set_string_function = tag_compound_set_string_function;

        get_item_meta_function = clazz.getMethod("getItemMeta");
        Method has_tag_function;
        try {
            has_tag_function = net_class.getMethod("hasTag");
        } catch (Exception ignored) {
            has_tag_function = net_class.getMethod("t");
        }
        this.has_tag_function = has_tag_function;

        Method get_tag_function;
        try {
            get_tag_function = net_class.getMethod("getTag");
        } catch (Exception ignored) {
            get_tag_function = net_class.getMethod("u");
        }
        this.get_tag_function = get_tag_function;

        Method tag_compound_has_key_function;
        try {
            tag_compound_has_key_function = tag_compound_class.getMethod("hasKey", String.class);
        } catch (Exception ignored) {
            tag_compound_has_key_function = tag_compound_class.getMethod("e", String.class);
        }
        this.tag_compound_has_key_function = tag_compound_has_key_function;

        Method tag_compound_get_string_function;
        try {
            tag_compound_get_string_function = tag_compound_class.getMethod("getString", String.class);
        } catch (Exception ignored) {
            tag_compound_get_string_function = tag_compound_class.getMethod("l", String.class);
        }
        this.tag_compound_get_string_function = tag_compound_get_string_function;

        Method save_function;
        try {
            save_function = net_class.getMethod("save", tag_compound_class);
        } catch (Exception ignored) {
            save_function = net_class.getMethod("b", tag_compound_class);
        }
        this.save_function = save_function;
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
