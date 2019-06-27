package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.utils.RPEvents;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import me.randomhashtags.randompackage.utils.supported.MCMMOAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.utils.GivedpItem.givedpitem;

public class RandomPackageAPI extends RPFeature implements CommandExecutor, TabCompleter {
    public static final RandomPackageAPI api = new RandomPackageAPI();

    public static int spawnerchance = 0;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String n = cmd.getName();
        if(n.equals("randompackage")) {
            if(args.length == 0) {
                if(player != null && player.getName().equals("RandomHashTags") || hasPermission(sender, "RandomPackage.randompackage", true)) {
                    final Plugin spawner = RandomPackage.spawnerPlugin, mcmmo = RandomPackage.mcmmo;
                    for(String string : Arrays.asList(" ",
                            "&6&m&l---------------------------------------------",
                            "&7- Author: &6RandomHashTags",
                            "&7- RandomPackage Version: &b" + randompackage.getDescription().getVersion(),
                            "&7- Server Version: &f" + version,
                            "&7- Faction Plugin: " + (fapi.factions != null ? "&3" + fapi.factions + " &7(&2" + pluginmanager.getPlugin("Factions").getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- mcMMO: " + (mcmmo != null ? "&a" + (MCMMOAPI.getMCMMOAPI().isClassic ? "Classic" : "Overhaul") + " &7(&2" + mcmmo.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Spawner Plugin: " + (spawner != null ? "&e" + spawner.getName() + " &7(&2" + spawner.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Wiki: &9https://gitlab.com/RandomHashTags/randompackage/wikis/Home",
                            "&7- Info: &f%%__USER__%%, &f%%__NONCE__%%",
                            "&7- Purchaser: &a&nhttps://www.spigotmc.org/members/%%__USER__%%/",
                            "&6&m&l---------------------------------------------",
                            " "))
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                }
            } else if(args[0].equals("reload") && hasPermission(sender, "RandomPackage.randompackage.reload", true)) {
                getPlugin.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &aReload complete!"));
            } else if(args[0].equals("backup") && hasPermission(sender, "RandomPackage.randompackage.backup", true)) {
                RPEvents.getRPEvents().backup();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &aPlayer backup complete!"));
            }
        }
        return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final int l = args.length;
        if(cmd.getName().equals("randompackage") && sender.hasPermission("RandomPackage.customenchant.enchant") && l >= 1) {
            final List<String> lore = new ArrayList<>();
            if(args[0].equals("enchant")) {
                if(l == 2) {
                    for(String s : CustomEnchant.enabled.keySet()) lore.add(s.replace(" ", "_"));
                } else if(l == 3) {
                    final CustomEnchant e = CustomEnchant.enabled.getOrDefault(args[1].toUpperCase().replace("_", " "), null);
                    if(e != null) for(int i = 1; i <= e.getMaxLevel(); i++) lore.add(Integer.toString(i));
                }
            }
            return lore;
        }
        return null;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("_Data", "other.yml");
        final Plugin f = pluginmanager.getPlugin("Factions");
        fapi.factions = f != null ? f.getDescription().getAuthors().contains("ProSavage") ? "SavageFactions" : "Factions" : null;

        givedpitem.enable();
        getPlugin.getCommand("givedp").setExecutor(givedpitem);

        sendConsoleMessage("&6[RandomPackage] &aInfo: &e%%__USER__%%, %%__NONCE__%%");
        sendConsoleMessage("&6[RandomPackage] &aLoaded API &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        spawnerchance = 0;
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void pluginEnableEvent(PluginEnableEvent event) {
        if(RandomPackage.spawner == null) {
            final String n = event.getPlugin().getName();
            if(n.equals("SilkSpawners") || n.equals("EpicSpawners")) {
                getPlugin.tryLoadingSpawner();
            }
        }
    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack c = event.getCurrentItem();
        if(!event.isCancelled() && c != null && !c.getType().equals(Material.AIR)) {
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