package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public final class ReflectedCraftItemStack {
    private static ReflectedCraftItemStack INSTANCE;
    @Nullable
    public static ReflectedCraftItemStack shared_instance() {
        if(INSTANCE == null) {
            final String version = get_craftbukkit_version();
            try {
                if(version != null) {
                    INSTANCE = new ReflectedCraftItemStack(RandomPackage.INSTANCE.getClass().getClassLoader(), version);
                } else {
                    throw new Exception("failed to get craftbukkit version");
                }
            } catch (Exception ignored) {
                RandomPackageAPI.INSTANCE.sendConsoleMessage("&6[RandomPackage] &cFailed to find craftbukkit version, some features will not work properly!");
            }
        }
        return null;
    }
    private static String get_craftbukkit_version() {
        String returned_version = null;
        int major = 1;
        final ClassLoader class_loader = RandomPackage.INSTANCE.getClass().getClassLoader();
        while (major <= 2) {
            int minor = 8;
            while (minor <= 50) {
                int release = 1;
                while (release <= 5) {
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
                                release = 10;
                                minor = 100;
                                major = 10;
                            }
                        }
                    }
                    release += 1;
                }
                minor += 1;
            }
            major += 1;
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
    public final Method tag_compound_get_tag_function;
    public final Method has_tag_function, get_tag_function, save_function;

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

        Method tag_compound_get_function;
        try {
            tag_compound_get_function = tag_compound_class.getMethod("getTag");
        } catch (Exception ignored) {
            tag_compound_get_function = tag_compound_class.getMethod("u");
        }
        this.tag_compound_get_tag_function = tag_compound_get_function;

        Method tag_compound_has_key_function;
        try {
            tag_compound_has_key_function = tag_compound_class.getMethod("hasKey", String.class);
        } catch (Exception ignored) {
            tag_compound_has_key_function = tag_compound_class.getMethod("e", String.class);
        }
        this.tag_compound_has_key_function = tag_compound_has_key_function;

        Method has_tag_function;
        try {
            has_tag_function = clazz.getMethod("hasTag");
        } catch (Exception ignored) {
            has_tag_function = clazz.getMethod("t");
        }
        this.has_tag_function = has_tag_function;

        Method get_tag_function;
        try {
            get_tag_function = clazz.getMethod("getTag");
        } catch (Exception ignored) {
            get_tag_function = clazz.getMethod("u");
        }
        this.get_tag_function = get_tag_function;

        Method tag_compound_get_string_function;
        try {
            tag_compound_get_string_function = clazz.getMethod("getString");
        } catch (Exception ignored) {
            tag_compound_get_string_function = clazz.getMethod("l");
        }
        this.tag_compound_get_string_function = tag_compound_get_string_function;

        Method save_function;
        try {
            save_function = clazz.getMethod("save", tag_compound_class);
        } catch (Exception ignored) {
            save_function = clazz.getMethod("b", tag_compound_class);
        }
        this.save_function = save_function;
    }
}
