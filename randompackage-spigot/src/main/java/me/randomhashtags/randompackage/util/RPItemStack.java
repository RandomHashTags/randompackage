package me.randomhashtags.randompackage.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface RPItemStack {
    String VERSION = Bukkit.getVersion();
    boolean EIGHT = VERSION.contains("1.8"), NINE = VERSION.contains("1.9"), TEN = VERSION.contains("1.10"), ELEVEN = VERSION.contains("1.11"), TWELVE = VERSION.contains("1.12"), THIRTEEN = VERSION.contains("1.13"), FOURTEEN = VERSION.contains("1.14");
    default Object createRPItemStack(ItemStack is, HashMap<String, String> values) {
        if(EIGHT) {
            final net.minecraft.server.v1_8_R3.ItemStack n = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_8_R3.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_8_R3.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asCraftMirror(n);
        } else if(NINE) {
            final net.minecraft.server.v1_9_R2.ItemStack n = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_9_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_9_R2.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asCraftMirror(n);
        } else if(TEN) {
            final net.minecraft.server.v1_10_R1.ItemStack n = org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_10_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_10_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asCraftMirror(n);
        } else if(ELEVEN) {
            final net.minecraft.server.v1_11_R1.ItemStack n = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_11_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_11_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asCraftMirror(n);
        } else if(TWELVE) {
            final net.minecraft.server.v1_12_R1.ItemStack n = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_12_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_12_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asCraftMirror(n);
        } else if(THIRTEEN) {
            final net.minecraft.server.v1_13_R2.ItemStack n = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_13_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_13_R2.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asCraftMirror(n);
        } else if(FOURTEEN) {
            final net.minecraft.server.v1_14_R1.ItemStack n = org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_14_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_14_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            return org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asCraftMirror(n);
        }
        return is;
    }
    default void setRPItemStackValues(ItemStack is, HashMap<String, String> values) {
        final String v = Bukkit.getVersion();
        if(EIGHT) {
            final net.minecraft.server.v1_8_R3.ItemStack n = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_8_R3.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_8_R3.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        } else if(NINE) {
            final net.minecraft.server.v1_9_R2.ItemStack n = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_9_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_9_R2.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        } else if(TEN) {
            final net.minecraft.server.v1_10_R1.ItemStack n = org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_10_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_10_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        } else if(ELEVEN) {
            final net.minecraft.server.v1_11_R1.ItemStack n = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_11_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_11_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        } else if(TWELVE) {
            final net.minecraft.server.v1_12_R1.ItemStack n = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_12_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_12_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        } else if(THIRTEEN) {
            final net.minecraft.server.v1_13_R2.ItemStack n = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_13_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_13_R2.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        } else if(FOURTEEN) {
            final net.minecraft.server.v1_14_R1.ItemStack n = org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_14_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_14_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
        }
    }
    default String getRPItemStackValue(ItemStack is, String identifier) {
        final String v = Bukkit.getVersion();
        if(EIGHT) {
            final net.minecraft.server.v1_8_R3.ItemStack n = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_8_R3.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        } else if(NINE) {
            final net.minecraft.server.v1_9_R2.ItemStack n = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_9_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        } else if(TEN) {
            final net.minecraft.server.v1_10_R1.ItemStack n = org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_10_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        } else if(ELEVEN) {
            final net.minecraft.server.v1_11_R1.ItemStack n = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_11_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        } else if(TWELVE) {
            final net.minecraft.server.v1_12_R1.ItemStack n = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_12_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        } else if(THIRTEEN) {
            final net.minecraft.server.v1_13_R2.ItemStack n = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_13_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        } else if(FOURTEEN) {
            final net.minecraft.server.v1_14_R1.ItemStack n = org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_14_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : null;
            return t != null && t.hasKey(identifier) ? t.getString(identifier) : null;
        }
        return null;
    }
}
