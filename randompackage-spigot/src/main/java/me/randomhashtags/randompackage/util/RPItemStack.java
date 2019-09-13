package me.randomhashtags.randompackage.util;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface RPItemStack extends Versionable {
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
        if(EIGHT) {
            final net.minecraft.server.v1_8_R3.ItemStack n = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_8_R3.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_8_R3.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        } else if(NINE) {
            final net.minecraft.server.v1_9_R2.ItemStack n = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_9_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_9_R2.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        } else if(TEN) {
            final net.minecraft.server.v1_10_R1.ItemStack n = org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_10_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_10_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        } else if(ELEVEN) {
            final net.minecraft.server.v1_11_R1.ItemStack n = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_11_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_11_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        } else if(TWELVE) {
            final net.minecraft.server.v1_12_R1.ItemStack n = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_12_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_12_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        } else if(THIRTEEN) {
            final net.minecraft.server.v1_13_R2.ItemStack n = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_13_R2.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_13_R2.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        } else if(FOURTEEN) {
            final net.minecraft.server.v1_14_R1.ItemStack n = org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(is);
            final net.minecraft.server.v1_14_R1.NBTTagCompound t = n.hasTag() ? n.getTag() : new net.minecraft.server.v1_14_R1.NBTTagCompound();
            for(String s : values.keySet()) {
                t.setString(s, values.get(s));
            }
            is.setItemMeta(org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asCraftMirror(n).getItemMeta());
        }
    }
    default String getRPItemStackValue(ItemStack is, String identifier) {
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
