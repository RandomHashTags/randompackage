package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BattleRoyale extends RPFeature implements CommandExecutor {
    private static BattleRoyale instance;
    public static BattleRoyale getBattleRoyale() {
        if(instance == null) instance = new BattleRoyale();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public String getIdentifier() { return "BATTLE_ROYALE"; }
    protected RPFeature getFeature() { return getBattleRoyale(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "battle royale.yml");

        sendConsoleMessage("&6[RandomPackage] &aLoaded Battle Royale &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
