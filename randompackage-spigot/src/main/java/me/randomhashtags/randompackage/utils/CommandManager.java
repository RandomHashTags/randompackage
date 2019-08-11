package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.RandomPackage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandManager {
    private static CommandManager instance;
    public static CommandManager getCommandManager(RandomPackage randompackage) {
        if(instance == null) {
            instance = new CommandManager();
            instance.config = randompackage.config;
        }
        return instance;
    }

    private SimpleCommandMap commandMap;
    private HashMap<String, Command> knownCommands;

    private HashMap<String, RPFeature> features;
    private Object nodes;

    private String v;
    private boolean isLegacy;
    private FileConfiguration config;
    private ConsoleCommandSender console;

    private CommandManager() {
        v = Bukkit.getVersion();
        isLegacy = v.contains("1.8") || v.contains("1.9") || v.contains("1.10") || v.contains("1.11") || v.contains("1.12");

        try {
            if(!isLegacy) {
                final Field o = getPrivateField(Class.forName("com.mojang.brigadier.tree.CommandNode"), "children");
                o.setAccessible(true);
                if(v.contains("1.13")) {
                    nodes = o.get(net.minecraft.server.v1_13_R2.MinecraftServer.getServer().commandDispatcher.a().getRoot());
                } else {
                    nodes = o.get(net.minecraft.server.v1_14_R1.MinecraftServer.getServer().commandDispatcher.a().getRoot());
                }
                for(String s : ((Map<String, com.mojang.brigadier.tree.CommandNode<?>>) nodes).keySet()) {
                    if(s.startsWith("randompackage:")) {
                        System.out.println("found " + s);
                    }
                }
                o.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        console = Bukkit.getConsoleSender();
        features = new HashMap<>();
        try {
            commandMap = (SimpleCommandMap) getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
            knownCommands = (HashMap<String, Command>) getPrivateField(commandMap, "knownCommands");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void tryLoadingg(RPFeature f, List<String> baseCmds, boolean enabled) {
        final HashMap<String, String> cmds = new HashMap<>();
        if(baseCmds != null) {
            for(String s : baseCmds) {
                cmds.put(s, s);
            }
        }
        tryLoading(f, cmds, enabled);
    }
    public void tryLoading(RPFeature f, HashMap<String, String> baseCmds, boolean enabled) {
        try {
            if(baseCmds != null && !baseCmds.isEmpty()) {
                for(String base : baseCmds.keySet()) {
                    final String path = baseCmds.get(base);
                    enabled = config.getBoolean(path + ".enabled");
                    final PluginCommand baseCmd = (PluginCommand) knownCommands.get(base);
                    baseCmd.setExecutor((CommandExecutor) f);
                    if(enabled) {
                        final List<String> cmds = config.getStringList(path + ".cmds");
                        if(cmds != null && !cmds.isEmpty()) {
                            final String first = cmds.get(0);
                            baseCmd.unregister(commandMap);
                            if(!first.equalsIgnoreCase(base)) {
                                baseCmd.setName(first);
                            }
                            cmds.remove(first);
                            baseCmd.setAliases(cmds);
                            for(String s : cmds) {
                                commandMap.register(s, baseCmd);
                                knownCommands.put("randompackage:" + s, baseCmd);
                            }
                            baseCmd.register(commandMap);
                            //updateCommand(baseCmd, false);
                        }
                    } else {
                        unregisterPluginCommand(baseCmd);
                    }
                }
            }
            if(enabled) {
                f.enable();
                features.put(f.getIdentifier(), f);
            }
        } catch (Exception e) {
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage &cERROR&6] &c&lError trying to load feature:&r &f" + f.getIdentifier()));
            e.printStackTrace();
        }
    }
    public void disable() {
        for(RPFeature f : features.values()) {
            disable(f);
        }
    }
    public void disable(RPFeature f) {
        if(f.isEnabled()) {
            f.disable();
        }
    }

    private void updateCommand(PluginCommand cmd, boolean unregister) {
        final String s = cmd.getName();
        System.out.println("updating command \"" + s + "\"");
        final Map<String, com.mojang.brigadier.tree.CommandNode<?>> o = (Map<String, com.mojang.brigadier.tree.CommandNode<?>>) nodes;
        if(unregister) {
            o.remove("randompackage:" + s);
            o.remove(s);
        } else {
            final com.mojang.brigadier.tree.CommandNode<?> c = o.get(s);
            for(String a : cmd.getAliases()) {
                System.out.println("put alias \"" + a + "\"");
                o.put(a, c);
            }
        }
    }

    private void unregisterPluginCommand(PluginCommand cmd) {
        final String c = cmd.getName();
        knownCommands.remove("randompackage:" + c);
        cmd.unregister(commandMap);
        boolean hasOtherCmd = false;
        final Set<String> keys = knownCommands.keySet();
        for(int i = 0; i < keys.size(); i++) {
            final String otherCmd = (String) keys.toArray()[i];
            if(!otherCmd.startsWith("RandomPackage:") && otherCmd.split(":")[otherCmd.split(":").length-1].equals(c)) { // gives the last plugin that has the cmd.getName() the command priority
                hasOtherCmd = true;
                knownCommands.replace(c, cmd, (PluginCommand) knownCommands.values().toArray()[i]);
                break;
            }
        }
        if(!hasOtherCmd) { // removes the command completely
            knownCommands.remove(c);
            //updateCommand(cmd, true);
        }
    }


    private Object getPrivateField(Object object, String field) throws Exception {
        /* Code from "zeeveener" at https://bukkit.org/threads/131808/ , edited by RandomHashTags */
        Class<?> clazz = object.getClass();
        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? isLegacy || v.equals("1.13") ? clazz.getDeclaredField(field) : clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    private Field getPrivateField(Class clazz, String field) throws Exception {
        Field objectField = clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        return objectField;
    }
}
