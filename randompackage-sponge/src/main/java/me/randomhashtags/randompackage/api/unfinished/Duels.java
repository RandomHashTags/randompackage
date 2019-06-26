package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;

import java.io.File;

public class Duels extends RandomPackageAPI implements CommandExecutor {

    private static Duels instance;
    public static Duels getDuels() {
        if(instance == null) instance = new Duels();
        return instance;
    }

    public boolean isEnabled;
    public YamlConfiguration config;

    private UInventory type, godset;

    public CommandResult execute(CommandSource src, CommandContext args) {
        if(src instanceof Player) {
            final Player player = (Player) src;
            final int l = args.length;
            if(l == 0) {
                viewTypes(player);
            } else {
                final String a = args[0];
                if(a.equals("godset")) {
                    viewGodset(player);
                } else {

                }
            }
        }
        return CommandResult.success();
    }

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        addCommand(this, "RandomPackage.duels", "Duels!", "duel", "duels");
        save(null, "duels.yml");
        eventmanager.registerListeners(randompackage, this);
        isEnabled = true;

        config = YamlConfiguration.loadConfiguration(new File(rpd, "duels.yml"));

        type = new UInventory(null, config.getInt("type.size"), translateColorCodes(config.getString("type.title")));
        godset = new UInventory(null, config.getInt("godset.size"), translateColorCodes(config.getString("godset.title")));

        sendConsoleMessage("&6[RandomPackage] &aLoaded Duels &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        config = null;
        type = null;
        godset = null;
        eventmanager.unregisterListeners(this);
    }

    public void viewTypes(Player player) {
        if(hasPermission(player, "RandomPackage.duels.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, type.getSize(), type.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(type.getInventory().getContents());

            player.updateInventory();
        }
    }
    public void viewGodset(Player player) {
        if(hasPermission(player, "RandomPackage.duels.view.godset", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, godset.getSize(), godset.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(godset.getInventory().getContents());

            player.updateInventory();
        }
    }
}
