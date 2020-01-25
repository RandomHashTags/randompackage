package me.randomhashtags.randompackage.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.*;

public final class CommandManager extends Reflect {
    private static CommandManager instance;
    public static CommandManager getCommandManager() {
        if(instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    private SimpleCommandMap commandMap;
    private HashMap<String, Command> knownCommands;

    private HashMap<String, RPFeature> features;
    private static HashMap<String, PluginCommand> actualCmds;
    private Object dispatcher, nodes;

    public String getIdentifier() { return "COMMAND_MANAGER"; }
    private CommandManager() {
        actualCmds = new HashMap<>();
        try {
            if(!LEGACY) {
                final Field o = getPrivateField(Class.forName("com.mojang.brigadier.tree.CommandNode"), "children");
                o.setAccessible(true);
                if(THIRTEEN) {
                    final com.mojang.brigadier.CommandDispatcher d = net.minecraft.server.v1_13_R2.MinecraftServer.getServer().commandDispatcher.a();
                    dispatcher = d;
                    nodes = o.get(d.getRoot());
                } else if(FOURTEEN) {
                    final com.mojang.brigadier.CommandDispatcher d = net.minecraft.server.v1_14_R1.MinecraftServer.getServer().commandDispatcher.a();
                    dispatcher = d;
                    nodes = o.get(d.getRoot());
                } else if(FIFTEEN) {
                    final com.mojang.brigadier.CommandDispatcher d = net.minecraft.server.v1_15_R1.MinecraftServer.getServer().commandDispatcher.a();
                    dispatcher = d;
                    nodes = o.get(d.getRoot());
                }
                o.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        features = new HashMap<>();
        try {
            commandMap = (SimpleCommandMap) getPrivateField(PLUGIN_MANAGER, "commandMap");
            knownCommands = (HashMap<String, Command>) getPrivateField(commandMap, "knownCommands", !LEGACY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {}
    public void unload() {}

    public void load(RPFeature f, List<String> baseCmds, boolean enabled) {
        final HashMap<String, String> cmds = new HashMap<>();
        if(baseCmds != null) {
            for(String s : baseCmds) {
                cmds.put(s, s);
            }
        }
        loadCustom(f, cmds, enabled);
    }
    public void loadCustom(RPFeature feature, HashMap<String, String> baseCmds, boolean isEnabled) {
        if(isEnabled) {
            try {
                final List<Boolean> enabledList = new ArrayList<>();
                if(baseCmds != null && !baseCmds.isEmpty()) {
                    for(String base : baseCmds.keySet()) {
                        final String path = baseCmds.get(base);
                        final boolean enabled = RP_CONFIG.getBoolean(path + ".enabled");
                        enabledList.add(enabled);
                        if(!knownCommands.containsKey(base)) {
                            final PluginCommand cmd = actualCmds.get(base);
                            commandMap.register(base, cmd);
                            knownCommands.put(base, cmd);
                            knownCommands.put("randompackage:" + base, cmd);
                        }
                        final PluginCommand baseCmd = (PluginCommand) knownCommands.get("randompackage:" + base);
                        baseCmd.setExecutor((CommandExecutor) feature);
                        if(!actualCmds.containsKey(base)) {
                            actualCmds.put(base, baseCmd);
                        }
                        if(enabled) {
                            final List<String> cmds = RP_CONFIG.getStringList(path + ".cmds");
                            if(!cmds.isEmpty()) {
                                final String first = cmds.get(0);
                                baseCmd.unregister(commandMap);
                                if(!first.equalsIgnoreCase(base)) {
                                    baseCmd.setName(first);
                                }
                                cmds.remove(first);
                                baseCmd.setAliases(cmds);
                                for(String s : cmds) {
                                    commandMap.register(s, baseCmd);
                                    knownCommands.put(s, baseCmd);
                                    knownCommands.put("randompackage:" + s, baseCmd);
                                }
                                baseCmd.register(commandMap);
                                updateBrigadierCmd(baseCmd, false);
                            }
                        } else {
                            unregisterPluginCommand(baseCmd);
                        }
                    }
                } else {
                    enabledList.add(true);
                }
                if(enabledList.contains(true)) {
                    feature.enable();
                    features.put(feature.getIdentifier(), feature);
                }
            } catch (Exception e) {
                sendConsoleMessage("&6[RandomPackage &cERROR&6] &c&lError trying to load feature commands:&r &f" + feature.getIdentifier());
                e.printStackTrace();
            }
        }
    }
    public void disable() {
        for(RPFeature f : features.values()) {
            disable(f);
        }
    }
    public void disable(RPFeature f) {
        if(f != null) {
            f.disable();
        }
    }

    private void updateBrigadierCmd(PluginCommand cmd, boolean unregister) {
        if(LEGACY) {
            return;
        }
        final String name = cmd.getName();
        final Map<String, com.mojang.brigadier.tree.CommandNode<?>> nodes = (Map<String, com.mojang.brigadier.tree.CommandNode<?>>) this.nodes;
        if(unregister) {
            nodes.remove("randompackage:" + name);
            nodes.remove(name);
        } else {
            //final com.mojang.brigadier.tree.CommandNode<?> c = nodes.get(name);
            final com.mojang.brigadier.CommandDispatcher dispatcher = (com.mojang.brigadier.CommandDispatcher) this.dispatcher;
            for(String a : cmd.getAliases()) {
                dispatcher.register(com.mojang.brigadier.builder.LiteralArgumentBuilder.literal(a));
            }
        }
    }

    private void unregisterPluginCommand(PluginCommand cmd) {
        final String cmdName = cmd.getName();
        knownCommands.remove("randompackage:" + cmdName);
        cmd.unregister(commandMap);
        boolean hasOtherCmd = false;
        final Object[] keys = knownCommands.keySet().toArray(), values = knownCommands.values().toArray();
        for(int i = 0; i < keys.length; i++) {
            final String otherCmd = (String) keys[i];
            // gives the last plugin that has the cmd.getName() the command priority
            if(!otherCmd.startsWith("RandomPackage:") && otherCmd.split(":")[otherCmd.split(":").length-1].equals(cmdName)) {
                final Object target = values[i];
                if(target instanceof PluginCommand) {
                    final PluginCommand targetCmd = (PluginCommand) target;
                    if(!targetCmd.getPlugin().equals(RANDOM_PACKAGE)) {
                        hasOtherCmd = true;
                        knownCommands.replace(cmdName, cmd, targetCmd);
                        break;
                    }
                }
            }
        }
        if(!hasOtherCmd) { // removes the command completely
            knownCommands.remove(cmdName);
            updateBrigadierCmd(cmd, true);
        }
    }
}
