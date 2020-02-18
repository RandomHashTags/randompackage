package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.listener.RPEvents;
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

import java.util.Arrays;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackage.mcmmo;
import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

public class RandomPackageAPI extends RPFeature implements CommandExecutor {
    public static final RandomPackageAPI API = new RandomPackageAPI();

    public static int spawnerchance = 0;

    public String getIdentifier() {
        return "RANDOMPACKAGE_API";
    }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String n = cmd.getName();
        if(n.equals("randompackage")) {
            if(args.length == 0) {
                if(player != null && player.getName().equals("RandomHashTags") || hasPermission(sender, "RandomPackage.randompackage", true)) {
                    final Plugin spawner = RandomPackage.spawnerPlugin, mcmmo = RandomPackage.mcmmo;
                    final Plugin fac = PLUGIN_MANAGER.getPlugin("Factions");
                    for(String string : Arrays.asList(" ",
                            "&6&m&l---------------------------------------------",
                            "&7- Author: &6RandomHashTags",
                            "&7- RandomPackage Version: &b" + RANDOM_PACKAGE.getDescription().getVersion(),
                            "&7- Server Version: &f" + VERSION,
                            "&7- PlaceholderAPI: " + (RANDOM_PACKAGE.placeholderapi ? "&atrue &7(&2" + PLUGIN_MANAGER.getPlugin("PlaceholderAPI").getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Faction Plugin: " + (regions.hookedFactionsUUID() ? "&3" + getFactionType(fac) + " &7(&2" + fac.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- mcMMO: " + (mcmmo != null ? "&a" + (MCMMOAPI.getMCMMOAPI().isClassic() ? "Classic" : "Overhaul") + " &7(&2" + mcmmo.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Spawner Plugin: " + (spawner != null ? "&e" + spawner.getName() + " &7(&2" + spawner.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Wiki: &9https://gitlab.com/RandomHashTags/randompackage-multi/wikis/Home",
                            "&7- Purchaser: &a&nhttps://www.spigotmc.org/members/%%__USER__%%/&r &f%%__NONCE__%%",
                            "&6&m&l---------------------------------------------",
                            " "))
                        sender.sendMessage(colorize(string));
                }
            } else {
                final String a = args[0];
                if(a.equals("reload") && hasPermission(sender, "RandomPackage.randompackage.reload", true)) {
                    getPlugin.reload();
                    sender.sendMessage(colorize("&6[RandomPackage] &aReload complete!"));
                } else if(a.equals("backup") && hasPermission(sender, "RandomPackage.randompackage.backup", true)) {
                    RPEvents.getRPEvents().backup();
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

    public void load() {
        final long started = System.currentTimeMillis();
        save("_Data", "other.yml");

        GIVEDP_ITEM.enable();
        RANDOM_PACKAGE.getCommand("givedp").setExecutor(GIVEDP_ITEM);

        if(mcmmo != null) {
            MCMMOAPI.getMCMMOAPI().enable();
        }

        sendConsoleMessage("&6[RandomPackage] &aInfo: &e%%__USER__%%, %%__NONCE__%%");
        sendConsoleMessage("&6[RandomPackage] &aLoaded API &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        spawnerchance = 0;
    }
    @EventHandler
    private void pluginEnableEvent(PluginEnableEvent event) {
        final String n = event.getPlugin().getName();
        if(RandomPackage.spawner == null && (n.equals("SilkSpawners") || n.equals("EpicSpawners"))) {
            RANDOM_PACKAGE.tryLoadingSpawner();
        } else if(!regions.hookedFactionsUUID() && n.equals("Factions")) {
            regions.trySupportingFactions();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack c = event.getCurrentItem();
        if(c != null && !c.getType().equals(Material.AIR)) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory top = player.getOpenInventory().getTopInventory();
            final int r = event.getRawSlot();
            if(event.getView().getTitle().equals(givedp.getTitle()) && r < top.getSize()) {
                player.openInventory(givedpCategories.get(r));
            } else if(givedpCategories.contains(event.getClickedInventory()) && r < top.getSize()) {
                giveItem(player, c);
            } else return;
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}