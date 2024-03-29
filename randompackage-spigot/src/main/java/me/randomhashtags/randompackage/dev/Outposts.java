package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.Outpost;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Outposts implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack background;
    public static HashMap<String, String> STATUSES;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
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

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("outposts", "_settings.yml");

        if(!OTHER_YML.getBoolean("saved default outposts")) {
            generateDefaultOutposts();
            OTHER_YML.set("saved default outposts", true);
            saveOtherData();
        }

        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "outposts", "_settings.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        STATUSES = new HashMap<>();
        for(String s : config.getConfigurationSection("status").getKeys(false)) {
            STATUSES.put(s.toUpperCase().replace(" ", "_"), colorize(config.getString("status." + s)));
        }
        for(File f : new File(DATA_FOLDER + SEPARATOR + "outposts").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                //final FileOutpost o = new FileOutpost(f);
                //o.setOutpostStatus(OutpostStatus.UNCONTESTED);
                //gi.setItem(o.getSlot(), o.getItem());
            }
        }
        background = createItemStack(config, "gui.background");
        int i = 0;
        for(ItemStack is : gi.getContents()) {
            if(is == null) gi.setItem(i, background);
            i++;
        }
        sendConsoleMessage("&aLoaded " + getAll(Feature.OUTPOST).size() + " Outposts &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        unregister(Feature.OUTPOST);
    }

    public void viewStatus(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.outpost", true)) {
            final List<String> msg = getStringList(config, "messages.view current");
            for(String s : msg) {
                if(s.contains("{OUTPOST}")) {
                    for(Outpost o : getAllOutposts().values()) {
                        sender.sendMessage(s.replace("{OUTPOST}", getLocalizedName(o)).replace("{STATUS}", o.getStatus()));
                    }
                } else {
                    sender.sendMessage(s);
                }
            }
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.outpost.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void view(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.outpost.view", true)) {
            player.closeInventory();

            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(null, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                final ItemStack item = top.getItem(i);
                final Outpost o = valueOfOutpost(i);
                if(o != null) {
                    final String cap = Double.toString(round(/*o.getControlPercent()*/0, 4)), attacking = o.getAttackingFaction(), controlling = o.getControllingFaction(), status = o.getStatus();
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    for(String s : itemMeta.getLore()) {
                        if(s.contains("{CAP%}") && controlling == null || s.contains("{ATTACKING}") && attacking == null || s.contains("{CONTROLLING}") && controlling == null) {
                            s = null;
                        }
                        if(s != null) {
                            lore.add(s.replace("{STATUS}", status).replace("{CAP%}", cap).replace("{ATTACKING}", attacking != null ? attacking : "N/A").replace("{CONTROLLING}", controlling != null ? controlling : "N/A"));
                        }
                    }
                    itemMeta.setLore(lore);
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
                player.sendMessage(colorize("&6[RandomPackage] &cThis Outpost's world doesn't exist!"));
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
