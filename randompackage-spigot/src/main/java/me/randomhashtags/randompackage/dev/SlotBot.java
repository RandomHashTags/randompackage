package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SlotBot extends RPFeature implements Listener, CommandExecutor {
    private static SlotBot instance;
    public static SlotBot getSlotBot() {
        if(instance == null) instance = new SlotBot();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private ItemStack ticket, rewardSlot, withdrawTickets, spinner, randomizedLootPlaceholder, visualPlaceholder, background;
    private int withdrawTicketsSlot;
    private List<String> rewards;

    private HashMap<Player, HashSet<Integer>> rollingTasks;
    private HashSet<Player> pending;

    private HashMap<Integer, HashSet<Integer>> slots;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }

    public String getIdentifier() { return "SLOT_BOT"; }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "slot bot.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "slot bot.yml"));

        ticket = d(config, "items.ticket");
        rewardSlot = d(config, "items.reward slot");
        withdrawTickets = d(config, "items.withdraw tickets");
        spinner = d(config, "items.spinner");

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        visualPlaceholder = d(config, "items.visual placeholder");
        randomizedLootPlaceholder = d(config, "items.randomized loot placeholder");
        background = d(config, "gui.background");
        final Inventory inv = gui.getInventory();

        inv.setItem(config.getInt("items.spinner.slot"), spinner);
        slots = new HashMap<>();

        for(String key : config.getConfigurationSection("gui.reward slots").getKeys(false)) {
            int slot = Integer.parseInt(key);
            inv.setItem(slot, rewardSlot);
            final HashSet<Integer> rewardSlots = new HashSet<>();
            for(String s : config.getStringList("gui.reward slots." + key)) {
                slot = Integer.parseInt(s);
                inv.setItem(slot, randomizedLootPlaceholder);
                rewardSlots.add(slot);
            }
            slots.put(slot, rewardSlots);
        }
        for(String key : config.getConfigurationSection("gui").getKeys(false)) {
            if(!key.equals("title") && !key.equals("size") && !key.equals("background") && !key.equals("reward slots") && !key.equals("visual placeholder slots")) {
                final String path = "gui." + key;
                final String item = config.getString(path + ".item");
                final int slot = config.getInt(path + ".slot");
                final boolean isWithdraw = "WITHDRAW_TICKETS".equals(item);
                if(isWithdraw) { withdrawTicketsSlot = slot; }
                inv.setItem(slot, isWithdraw ? null : d(config, path));
            }
        }

        for(String s : config.getStringList("gui.visual placeholder slots")) {
            inv.setItem(Integer.parseInt(s), visualPlaceholder);
        }

        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null) {
                inv.setItem(i, background);
            }
        }

        rollingTasks = new HashMap<>();
        pending = new HashSet<>();
        rewards = new ArrayList<>();

        sendConsoleMessage("&6[RandomPackage] &aLoaded Slot Bot &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player player : new HashSet<>(pending)) {
            player.closeInventory();
        }
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.slotbot.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }
    public void withdrawTickets(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(withdrawTickets.isSimilar(top.getItem(withdrawTicketsSlot))) {
            top.setItem(withdrawTicketsSlot, background);
            player.updateInventory();
        }
    }
    public void tryInsertingTicket(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
    }

    private void setRandomLoot(Player player, Inventory top) {
        updateRandomLoot(player, top, true);
    }
    private void updateRandomLoot(Player player, Inventory top, boolean setRandom) {
        final int size = rewards.size();
        for(int key : slots.keySet()) {
            top.setItem(key, setRandom ? d(null, rewards.get(RANDOM.nextInt(size))) : null);
            for(int value : slots.get(key)) {
                top.setItem(value, setRandom ? d(null, rewards.get(RANDOM.nextInt(size))) : null);
            }
        }
        player.updateInventory();
    }
    private void startRolling(Player player) {
        rollingTasks.put(player, new HashSet<>());
        final HashSet<Integer> tasks = rollingTasks.get(player);
        pending.add(player);
        final Inventory top = player.getOpenInventory().getTopInventory();
        setRandomLoot(player, top);
        for(int i = 1; i <= 10; i++) {
            tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                updateRandomLoot(player, top, true);
            }, i*5));
        }
        for(int i = 1; i <= 10; i++) {
            final int I = i;
            tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                updateRandomLoot(player, top, true);
                if(I == 10) {
                    stopRolling(player);
                }
            }, (i+50)+(i*10)));
        }
    }
    private void stopRolling(Player player) {
        if(pending.contains(player) && rollingTasks.containsKey(player)) {
            for(int task : rollingTasks.get(player)) {
                SCHEDULER.cancelTask(task);
            }
            rollingTasks.remove(player);
            final ItemStack air = new ItemStack(Material.AIR);
            final Inventory top = player.getOpenInventory().getTopInventory();
            for(int key : slots.keySet()) {
                for(int i : slots.get(key)) {
                    top.setItem(i, air);
                }
            }
            player.updateInventory();
        }
    }
    private void giveLoot(Player player) {
        if(pending.contains(player)) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            for(int i : slots.keySet()) {
                giveItem(player, top.getItem(i));
            }
            pending.remove(player);
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        if(rollingTasks.containsKey(player)) {
            stopRolling(player);
            giveLoot(player);
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(ticket.isSimilar(is)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(gui.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack current = event.getCurrentItem();
            if(current == null) return;

            if(current.isSimilar(spinner)) {
            }
        }
    }
}
