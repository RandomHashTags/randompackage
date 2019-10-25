package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.addon.Outpost;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Outposts extends RPFeature implements CommandExecutor {
    private static Outposts instance;
    public static Outposts getOutposts() {
        if(instance == null) instance = new Outposts();
        return instance;
    }
    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack background;
    public static HashMap<String, String> statuses;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0) {
            viewStatus(player);
        } else {
            switch (args[0]) {
                case "help":
                    viewHelp(sender);
                    break;
                case "warp":
                    if(l == 1 && player != null) view(player);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    public String getIdentifier() { return "OUTPOSTS"; }
    protected RPFeature getFeature() { return getOutposts(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save("outposts", "_settings.yml");

        if(!otherdata.getBoolean("saved default outposts")) {
            final String[] o = new String[]{"HERO", "SERVONAUT", "TRAINEE", "VANILLA"};
            for(String s : o) save("outposts", s + ".yml");
            otherdata.set("saved default outposts", true);
            saveOtherData();
        }

        config = YamlConfiguration.loadConfiguration(new File(rpd + separator + "outposts", "_settings.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        statuses = new HashMap<>();
        for(String s : config.getConfigurationSection("status").getKeys(false)) {
            statuses.put(s.toUpperCase().replace(" ", "_"), ChatColor.translateAlternateColorCodes('&', config.getString("status." + s)));
        }
        for(File f : new File(rpd + separator + "outposts").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                //final FileOutpost o = new FileOutpost(f);
                //o.setOutpostStatus(OutpostStatus.UNCONTESTED);
                //gi.setItem(o.getSlot(), o.getItem());
            }
        }
        background = d(config, "gui.background");
        int i = 0;
        for(ItemStack is : gi.getContents()) {
            if(is == null) gi.setItem(i, background);
            i++;
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (outposts != null ? outposts.size() : 0) + " Outposts &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        outposts = null;
    }

    public void viewStatus(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.outpost", true)) {
            final List<String> msg = colorizeListString(config.getStringList("messages.view current"));
            for(String s : msg) {
                if(s.contains("{OUTPOST}")) {
                    for(Outpost o : outposts.values()) {
                        sender.sendMessage(s.replace("{OUTPOST}", o.getName()).replace("{STATUS}", o.getStatus()));
                    }
                } else {
                    sender.sendMessage(s);
                }
            }
        }
    }
    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.outpost.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void view(Player player) {
        if(hasPermission(player, "RandomPackage.outpost.view", true)) {
            player.closeInventory();

            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(null, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                final Outpost o = valueOfOutpost(i);
                if(o != null) {
                    final String cap = Double.toString(round(/*o.getControlPercent()*/0, 4)), attacking = o.getAttackingFaction(), controlling = o.getControllingFaction(), status = o.getStatus();
                    itemMeta = item.getItemMeta(); lore.clear();
                    for(String s : itemMeta.getLore()) {
                        if(s.contains("{CAP%}") && controlling == null || s.contains("{ATTACKING}") && attacking == null || s.contains("{CONTROLLING}") && controlling == null) s = null;
                        if(s != null) lore.add(s.replace("{STATUS}", status).replace("{CAP%}", cap).replace("{ATTACKING}", attacking != null ? attacking : "N/A").replace("{CONTROLLING}", controlling != null ? controlling : "N/A"));
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
            }
            player.updateInventory();
        }
    }
    public void tryTeleportingTo(Player player, Outpost outpost) {
        if(hasPermission(player, "RandomPackage.outpost.warp.*", false) || hasPermission(player, "RandomPackage.outpost.warp." + outpost.getIdentifier(), true)) {
            try {
                player.teleport(outpost.getWarpLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch(Exception e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &cThis Outpost's world doesn't exist!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(gui.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            final int r = event.getRawSlot();
            if(r < 0 || r >= player.getOpenInventory().getTopInventory().getSize()) return;
            final Outpost o = valueOfOutpost(r);
            if(o != null) {
                tryTeleportingTo(player, o);
            }
        }
    }
}
