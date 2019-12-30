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
import java.util.*;

public class SlotBot extends RPFeature implements Listener, CommandExecutor {
    private static SlotBot instance;
    public static SlotBot getSlotBot() {
        if(instance == null) instance = new SlotBot();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    public ItemStack ticket;
    private ItemStack ticketLocked, ticketUnlocked, spinnerMissingTickets, spinnerReadyToSpin, rewardSlot, withdrawTickets;
    private ItemStack randomizedLootPlaceholder, randomizedLootReadyToRoll, visualPlaceholder, background;
    private int withdrawTicketsSlot, spinnerSlot;
    private List<Integer> ticketSlots;
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
        ticketLocked = d(config, "items.ticket locked");
        ticketUnlocked = d(config, "items.ticket unlocked");
        rewardSlot = d(config, "items.reward slot");
        withdrawTickets = d(config, "items.withdraw tickets");
        spinnerMissingTickets = d(config, "items.spinner missing ticket");
        spinnerReadyToSpin = d(config, "items.spinner ready to spin");
        spinnerSlot = config.getInt("items.spinner missing ticket.slot");

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        visualPlaceholder = d(config, "items.visual placeholder");
        randomizedLootPlaceholder = d(config, "items.randomized loot placeholder");
        randomizedLootReadyToRoll = d(config, "items.randomized loot ready to roll");
        background = d(config, "gui.background");

        final Inventory inv = gui.getInventory();
        inv.setItem(spinnerSlot, spinnerMissingTickets);

        slots = new HashMap<>();
        ticketSlots = new ArrayList<>();
        int ticketAmount = 0;
        for(String s : config.getStringList("items.ticket.slots")) {
            ticketAmount++;
            final int slot = Integer.parseInt(s);
            inv.setItem(slot, getTicketLocked(ticketAmount));
            ticketSlots.add(slot);
        }

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

    private ItemStack getTicketLocked(int ticketAmount) {
        item = ticketLocked.clone(); itemMeta = item.getItemMeta(); lore.clear();
        for(String string : itemMeta.getLore()) {
            lore.add(string.replace("{AMOUNT}", Integer.toString(ticketAmount)));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        item.setAmount(ticketAmount);
        return item;
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.slotbot.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }
    public void tryWithdrawingTickets(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(withdrawTickets.isSimilar(top.getItem(withdrawTicketsSlot))) {
            int ticketAmount = 0;
            for(int i : ticketSlots) {
                if(ticketUnlocked.isSimilar(top.getItem(i))) {
                    ticketAmount++;
                    giveItem(player, ticket);
                    top.setItem(i, getTicketLocked(ticketAmount));
                }
            }
            for(int i = 0; i < ticketAmount; i++) {
                for(int slot : slots.get(slots.keySet().toArray()[i])) {
                    top.setItem(slot, randomizedLootPlaceholder);
                }
            }
            top.setItem(withdrawTicketsSlot, background);
            top.setItem(spinnerSlot, spinnerMissingTickets);
            player.updateInventory();
        }
    }
    public int getInsertedTickets(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        int total = 0;
        for(int slot : ticketSlots) {
            if(ticketUnlocked.isSimilar(top.getItem(slot))) {
                total++;
            }
        }
        return total;
    }

    public void tryInsertingTicket(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        final int inserted = getInsertedTickets(player), maxAllowed = ticketSlots.size();
        if(inserted < maxAllowed) {
            final ItemStack withdraw = top.getItem(withdrawTicketsSlot), spin = top.getItem(spinnerSlot);
            if(!withdrawTickets.isSimilar(withdraw)) {
                top.setItem(withdrawTicketsSlot, withdrawTickets);
            }
            if(!spinnerReadyToSpin.isSimilar(spin)) {
                top.setItem(spinnerSlot, spinnerReadyToSpin);
            }
            for(int slot : slots.get(slots.keySet().toArray()[inserted])) {
                top.setItem(slot, randomizedLootReadyToRoll);
            }

            removeItem(player, ticket, 1);
            final int slot = ticketSlots.get(inserted);
            item = ticketUnlocked.clone();
            item.setAmount(inserted+1);
            top.setItem(slot, item);
            player.updateInventory();
        }
    }
    public boolean trySpinning(@NotNull Player player, int slot) {
        return trySpinning(player, slot, player.getOpenInventory().getTopInventory().getItem(slot));
    }
    public boolean trySpinning(@NotNull Player player, int slot, @NotNull ItemStack targetItem) {
        if(targetItem != null && !targetItem.getType().equals(Material.AIR)) {
            if(targetItem.isSimilar(spinnerReadyToSpin)) {
                final Inventory top = player.getOpenInventory().getTopInventory();
                List<Integer> insertedTickets = new ArrayList<>();
                for(int i : ticketSlots) {
                    if(ticketUnlocked.isSimilar(top.getItem(i))) {
                        insertedTickets.add((int) slots.keySet().toArray()[insertedTickets.size()]);
                    }
                }
                if(!insertedTickets.isEmpty()) {
                    for(int rewardSlot : insertedTickets) {
                        startRolling(player, top, rewardSlot);
                    }
                }
                return true;
            } else if(ticketSlots.contains(slot)) {
                final HashMap<String, String> replacements = new HashMap<>();
                final int index = ticketSlots.indexOf(slot);
                replacements.put("{AMOUNT}", Integer.toString(index+1));
                sendStringListMessage(player, getStringList(config, "messages.slot requires ticket"), replacements);
            }
        }
        return false;
    }

    private void updateRandomLoot(Player player, Inventory top, boolean isRandom) {
        for(int key : slots.keySet()) {
            updateRandomLoot(player, top, key, isRandom);
        }
    }
    private void updateRandomLoot(Player player, Inventory top, int rewardSlot) {
        updateRandomLoot(player, top, rewardSlot, true);
    }
    private void updateRandomLoot(Player player, Inventory top, int rewardSlot, boolean isRandom) {
        final int size = rewards.size();
        top.setItem(rewardSlot, isRandom ? d(null, rewards.get(RANDOM.nextInt(size))) : null);
        for(int value : slots.get(rewardSlot)) {
            top.setItem(value, isRandom ? d(null, rewards.get(RANDOM.nextInt(size))) : null);
        }
        player.updateInventory();
    }
    private void startRolling(Player player, Inventory top) {
        for(int rewardSlot : slots.keySet()) {
            startRolling(player, top, rewardSlot);
        }
    }
    private void startRolling(Player player, Inventory top, int rewardSlot) {
        if(!rollingTasks.containsKey(player)) {
            rollingTasks.put(player, new HashSet<>());
        }

        final HashSet<Integer> tasks = rollingTasks.get(player);
        pending.add(player);

        updateRandomLoot(player, top, rewardSlot, true);
        for(int i = 0; i <= 10; i++) {
            tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                for(int slot : slots.get(rewardSlot)) {
                    updateRandomLoot(player, top, slot, true);
                }
            }, i*5));
        }
        for(int i = 0; i <= 10; i++) {
            final int I = i;
            tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                for(int slot : slots.get(rewardSlot)) {
                    updateRandomLoot(player, top, slot, true);
                }
                if(I == 10) {
                    stopRolling(player);
                }
            }, 50+(i*10)));
        }
    }
    public void stopRolling(@NotNull Player player) {
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
            final LinkedHashMap<String, Integer> items = new LinkedHashMap<>();
            final String tickets = Integer.toString(getInsertedTickets(player));
            for(int i : slots.keySet()) {
                item = top.getItem(i);
                itemMeta = item.getItemMeta();
                items.put(itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "UNKNOWN", item.getAmount());
                giveItem(player, item);
            }
            for(String s : getStringList(config, "messages.loot")) {
                s = s.replace("{PLAYER}", player.getName()).replace("{TICKETS}", tickets);
                if(s.contains("{AMOUNT}") && s.contains("{ITEM}")) {
                    for(String string : items.keySet()) {
                        Bukkit.broadcastMessage(s.replace("{AMOUNT}", Integer.toString(items.get(string)).replace("{ITEM}", string)));
                    }
                } else {
                    Bukkit.broadcastMessage(s);
                }
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
            final Inventory top = player.getOpenInventory().getTopInventory();
            final int slot = event.getRawSlot();

            if(slot >= top.getSize()) {
                if(current.isSimilar(ticket)) {
                    tryInsertingTicket(player);
                }
            } else if(current.isSimilar(spinnerMissingTickets)) {
                sendStringListMessage(player, getStringList(config, "messages.missing tickets"), null);
            } else if(current.isSimilar(spinnerReadyToSpin)) {
                final int tickets = getInsertedTickets(player);
                for(int i = 0; i < tickets; i++) {
                    final ItemStack target = top.getItem(ticketSlots.get(i));
                    trySpinning(player, -1, target);
                }
            } else if(current.isSimilar(withdrawTickets)) {
                tryWithdrawingTickets(player);
            } else if(current.isSimilar(spinnerReadyToSpin)) {
                for(int i : ticketSlots) {
                    trySpinning(player, i, top.getItem(i));
                }
            } else if(ticketSlots.contains(slot)) {
                trySpinning(player, slot, current);
            }
        }
    }
}
