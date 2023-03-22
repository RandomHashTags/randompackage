package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import me.randomhashtags.randompackage.util.listener.RPEventsSpigot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.MCMMO;

public enum RandomPackageAPI implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public static int SPAWNER_CHANCE = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String commandName = cmd.getName();
        if(commandName.equals("randompackage")) {
            if(args.length == 0) {
                if(player != null && player.getName().equals("RandomHashTags") || hasPermission(sender, "RandomPackage.randompackage", true)) {
                    final Plugin spawner = RandomPackage.SPAWNER_PLUGIN, mcmmo = RandomPackage.MCMMO;
                    final Plugin fac = PLUGIN_MANAGER.getPlugin("Factions");
                    for(String string : List.of(" ",
                            "&6&m&l---------------------------------------------",
                            "&7- Author: &6RandomHashTags",
                            "&7- RandomPackage Version: &b" + RANDOM_PACKAGE.getDescription().getVersion(),
                            "&7- Server Version: &f" + VERSION,
                            "&7- PlaceholderAPI: " + (RANDOM_PACKAGE.placeholder_api ? "&atrue &7(&2" + PLUGIN_MANAGER.getPlugin("PlaceholderAPI").getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Faction Plugin: " + (RegionalAPI.INSTANCE.hookedFactionsUUID() ? "&3" + getFactionType(fac) + " &7(&2" + fac.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- mcMMO: " + (mcmmo != null ? "&a" + (MCMMOAPI.INSTANCE.isClassic() ? "Classic" : "Overhaul") + " &7(&2" + mcmmo.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Spawner Plugin: " + (spawner != null ? "&e" + spawner.getName() + " &7(&2" + spawner.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Wiki: &9https://gitlab.com/RandomHashTags/randompackage-multi/wikis/Home",
                            "&7- Note: &aMade with <3",
                            "&6&m&l---------------------------------------------",
                            " "))
                        sender.sendMessage(colorize(string));
                }
            } else {
                final String argument = args[0];
                if(argument.equals("reload") && hasPermission(sender, "RandomPackage.randompackage.reload", true)) {
                    RandomPackage.INSTANCE.reload();
                    sender.sendMessage(colorize("&6[RandomPackage] &aReload complete!"));
                } else if(argument.equals("backup") && hasPermission(sender, "RandomPackage.randompackage.backup", true)) {
                    RPEventsSpigot.INSTANCE.backup();
                    sender.sendMessage(colorize("&6[RandomPackage] &aPlayer backup complete!"));
                }
            }
        }
        return true;
    }
    private String getFactionType(Plugin factions) {
        if(factions != null) {
            final List<String> authors = factions.getDescription().getAuthors();
            return authors.contains("ProSavage") ? "SavageFactions" : "FactionsUUID";
        }
        return null;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("_Data", "other.yml");

        GivedpItem.INSTANCE.enable();
        RANDOM_PACKAGE.getCommand("givedp").setExecutor(GivedpItem.INSTANCE);

        if(MCMMO != null) {
            MCMMOAPI.INSTANCE.enable();
        }

        sendConsoleMessage("&6[RandomPackage] &aLoaded API &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        SPAWNER_CHANCE = 0;
    }

    @EventHandler
    private void pluginEnableEvent(PluginEnableEvent event) {
        final String n = event.getPlugin().getName();
        if(RandomPackage.SPAWNER_PLUGIN_NAME == null && (n.equals("SilkSpawners") || n.equals("EpicSpawners"))) {
            RANDOM_PACKAGE.try_loading_spawner();
        } else if(!RegionalAPI.INSTANCE.hookedFactionsUUID() && n.equals("Factions")) {
            RegionalAPI.INSTANCE.trySupportingFactions();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack currentItem = event.getCurrentItem();
        if(currentItem != null && !currentItem.getType().equals(Material.AIR)) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory top = player.getOpenInventory().getTopInventory();
            final int rawSlot = event.getRawSlot();
            if(event.getView().getTitle().equals(GIVEDP_INVENTORY.getTitle()) && rawSlot < top.getSize()) {
                player.openInventory(GIVEDP_CATEGORIES.get(rawSlot));
            } else if(GIVEDP_CATEGORIES.contains(event.getClickedInventory()) && rawSlot < top.getSize()) {
                giveItem(player, currentItem);
            } else {
                return;
            }
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}