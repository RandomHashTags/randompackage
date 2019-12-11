package me.randomhashtags.randompackage.util;

import org.bukkit.command.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void loadCustom(RPFeature f, HashMap<String, String> baseCmds, boolean enabled) {
        try {
            if(baseCmds != null && !baseCmds.isEmpty()) {
                for(String base : baseCmds.keySet()) {
                    final String path = baseCmds.get(base);
                    enabled = RP_CONFIG.getBoolean(path + ".enabled");
                    if(!knownCommands.containsKey(base)) {
                        final PluginCommand cmd = actualCmds.get(base);
                        commandMap.register(base, cmd);
                        knownCommands.put(base, cmd);
                        knownCommands.put("randompackage:" + base, cmd);
                    }
                    final PluginCommand baseCmd = (PluginCommand) knownCommands.get("randompackage:" + base);
                    baseCmd.setExecutor((CommandExecutor) f);
                    if(!actualCmds.containsKey(base)) actualCmds.put(base, baseCmd);
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
            }
            if(enabled) {
                f.enable();
                features.put(f.getIdentifier(), f);
            }
        } catch (Exception e) {
            sendConsoleMessage("&6[RandomPackage &cERROR&6] &c&lError trying to load feature commands:&r &f" + f.getIdentifier());
            e.printStackTrace();
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
        if(LEGACY) return;
        final String s = cmd.getName();
        final Map<String, com.mojang.brigadier.tree.CommandNode<?>> o = (Map<String, com.mojang.brigadier.tree.CommandNode<?>>) nodes;
        if(unregister) {
            o.remove("randompackage:" + s);
            o.remove(s);
        } else {
            final com.mojang.brigadier.tree.CommandNode<?> c = o.get(s);
            final com.mojang.brigadier.CommandDispatcher w = (com.mojang.brigadier.CommandDispatcher) dispatcher;
            for(String a : cmd.getAliases()) {
                w.register(com.mojang.brigadier.builder.LiteralArgumentBuilder.literal(a));
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
                final Object obj = knownCommands.values().toArray()[i];
                if(obj instanceof PluginCommand) {
                    final PluginCommand pc = (PluginCommand) obj;
                    if(!pc.getPlugin().equals(RANDOM_PACKAGE)) {
                        hasOtherCmd = true;
                        knownCommands.replace(c, cmd, pc);
                        break;
                    }
                }
            }
        }
        if(!hasOtherCmd) { // removes the command completely
            knownCommands.remove(c);
            updateBrigadierCmd(cmd, true);
        }
    }
}
