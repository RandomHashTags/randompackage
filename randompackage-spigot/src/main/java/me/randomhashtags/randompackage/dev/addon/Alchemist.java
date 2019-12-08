package me.randomhashtags.randompackage.dev.addon;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Alchemist extends RPFeature implements CommandExecutor {
    private static Alchemist instance;
    public static Alchemist getAlchemist() {
        if(instance == null) instance = new Alchemist();
        return instance;
    }

    public String getIdentifier() { return "ALCHEMIST"; }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public YamlConfiguration config;

    public void load() {
    }
    public void unload() {
    }
}
