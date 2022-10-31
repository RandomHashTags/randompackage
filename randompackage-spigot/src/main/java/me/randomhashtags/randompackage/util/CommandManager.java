package me.randomhashtags.randompackage.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CommandManager implements Reflect {
    INSTANCE;

    private SimpleCommandMap commandMap;
    private HashMap<String, Command> knownCommands;

    private final HashMap<String, RPFeatureSpigot> features;
    private static final HashMap<String, PluginCommand> ACTUAL_COMMANDS = new HashMap<>();
    private Object dispatcher, nodes;

    CommandManager() {
        try {
            if(!LEGACY) {
                final com.mojang.brigadier.CommandDispatcher versionDispatcher;
                if(THIRTEEN) {
                    versionDispatcher = net.minecraft.server.v1_13_R2.MinecraftServer.getServer().commandDispatcher.a();
                } else if(FOURTEEN) {
                    versionDispatcher = net.minecraft.server.v1_14_R1.MinecraftServer.getServer().commandDispatcher.a();
                } else if(FIFTEEN) {
                    versionDispatcher = net.minecraft.server.v1_15_R1.MinecraftServer.getServer().commandDispatcher.a();
                } else if(SIXTEEN) {
                    versionDispatcher = net.minecraft.server.v1_16_R3.MinecraftServer.getServer().vanillaCommandDispatcher.a();
                } else {
                    sendConsoleMessage("&6[RandomPackage &3CommandManager&6] &3attempting to support a Spigot version >= 1.17.0...");
                    versionDispatcher = net.minecraft.server.MinecraftServer.getServer().vanillaCommandDispatcher.a();
                    sendConsoleMessage("&6[RandomPackage &3CommandManager&6] &asuccessful integration");
                }
                if(versionDispatcher != null) {
                    dispatcher = versionDispatcher;

                    final Field field = getPrivateField(Class.forName("com.mojang.brigadier.tree.CommandNode"), "children");
                    field.setAccessible(true);
                    nodes = field.get(versionDispatcher.getRoot());
                    field.setAccessible(false);
                }
            }
        } catch (Exception e) {
            sendConsoleMessage("&6[RandomPackage &cWARNING&6] &c&lYou're running an unsupported Spigot version! Custom commands won't work, and disabled commands won't disable!");
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

    @Override
    public void load() {}
    @Override
    public void unload() {}

    public void load(RPFeatureSpigot f, List<String> baseCmds, boolean enabled) {
        final HashMap<String, String> cmds = new HashMap<>();
        if(baseCmds != null) {
            for(String s : baseCmds) {
                cmds.put(s, s);
            }
        }
        loadCustom(f, cmds, enabled);
    }
    public void loadCustom(RPFeatureSpigot feature, HashMap<String, String> baseCmds, boolean isEnabled) {
        if(isEnabled) {
            final String featureIdentifier = feature.getIdentifier();
            final List<Boolean> enabledList = new ArrayList<>();
            if(baseCmds != null && !baseCmds.isEmpty()) {
                for(String base : baseCmds.keySet()) {
                    final String path = baseCmds.get(base);
                    final boolean enabled = RP_CONFIG.getBoolean(path + ".enabled");
                    enabledList.add(enabled);
                    if(!knownCommands.containsKey(base)) {
                        final PluginCommand cmd = ACTUAL_COMMANDS.get(base);
                        commandMap.register(base, cmd);
                        knownCommands.put(base, cmd);
                        knownCommands.put("randompackage:" + base, cmd);
                    }
                    final PluginCommand baseCmd = (PluginCommand) knownCommands.get("randompackage:" + base);
                    baseCmd.setExecutor((CommandExecutor) feature);
                    if(!ACTUAL_COMMANDS.containsKey(base)) {
                        ACTUAL_COMMANDS.put(base, baseCmd);
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
                            updateBrigadierCmd(featureIdentifier, baseCmd, false);
                        }
                    } else {
                        unregisterPluginCommand(featureIdentifier, baseCmd);
                    }
                }
            } else {
                enabledList.add(true);
            }
            if(enabledList.contains(true)) {
                feature.enable();
                features.put(featureIdentifier, feature);
            }
        }
    }
    public void disable() {
        for(RPFeatureSpigot f : features.values()) {
            disable(f);
        }
    }
    public void disable(RPFeatureSpigot f) {
        if(f != null) {
            f.disable();
        }
    }

    private void updateBrigadierCmd(String featureIdentifier, PluginCommand cmd, boolean unregister) {
        if(LEGACY || dispatcher == null) {
            return;
        }
        final String name = cmd.getName();
        try {
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
        } catch (Exception e) {
            sendConsoleMessage("&6[RandomPackage &cERROR&6] &c&lError trying to update the Brigadier command(s) for feature:&r &f" + featureIdentifier);

            e.printStackTrace();
        }
    }

    private void unregisterPluginCommand(String featureIdentifier, PluginCommand cmd) {
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
            updateBrigadierCmd(featureIdentifier, cmd, true);
        }
    }
}
