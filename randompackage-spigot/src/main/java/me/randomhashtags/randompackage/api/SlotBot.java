package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.enums.SlotBotSetting;
import me.randomhashtags.randompackage.addon.slotbot.CustomItem;
import me.randomhashtags.randompackage.perms.SlotBotPermission;
import me.randomhashtags.randompackage.universal.CustomSound;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.USound;
import me.randomhashtags.randompackage.util.ChatUtils;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public enum SlotBot implements RPFeatureSpigot, CommandExecutor, ChatUtils {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory gui, preview;
    public ItemStack ticket;

    private ItemStack ticketLocked, ticketUnlocked, spinnerMissingTickets, spinner_ready_to_spin, rewardSlot, withdraw_tickets;
    private ItemStack randomizedLootPlaceholder, randomizedLootReadyToRoll, previewRewards, background;
    private int withdrawTicketsSlot, spinnerSlot, preview_rewards_slot;
    private List<Integer> ticket_slots;
    private List<String> rewards;

    private HashMap<Player, HashMap<Integer, List<Integer>>> rolling_tasks;
    private HashMap<Player, List<Integer>> pending_reward_slots, unrolled_tickets;
    private HashMap<Integer, List<Integer>> slots;

    private HashMap<String, CustomSound> sounds;
    private HashMap<SlotBotSetting, Boolean> settings;

    private Collection<CustomItem> customItems;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final boolean isPlayer = sender instanceof Player;
        final int length = args.length;
        if(length >= 1) {
            switch (args[0]) {
                case "reload":
                    if(sender.hasPermission(SlotBotPermission.COMMAND_RELOAD)) {
                        disable();
                        enable();
                        sender.sendMessage(colorize("&6[SlotBot] &aSuccessfully reloaded"));
                    } else if(isPlayer) {
                        view((Player) sender);
                    }
                    break;
                case "give":
                    if(sender.hasPermission(SlotBotPermission.COMMAND_GIVE)) {
                        if(length >= 3) {
                            final String target = args[1];
                            final Player player = SERVER.getPlayer(target);
                            if(player != null) {
                                final ItemStack is = getClone(ticket);
                                is.setAmount(Integer.parseInt(args[2]));
                                giveItem(player, is);
                            }
                        }
                    } else if(isPlayer) {
                        view((Player) sender);
                    }
                    break;
                default:
                    if(isPlayer) {
                        view((Player) sender);
                    }
                    break;
            }
        } else if(isPlayer && hasPermission(sender, SlotBotPermission.COMMAND, true)) {
            view((Player) sender);
        }
        return true;
    }

    @Override
    public void load() {
        save(null, "slot bot.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "slot bot.yml"));

        sounds = new HashMap<>();
        final String[] soundStrings = new String[] {
                "cancelled",
                "withdraw tickets",
                "insert ticket",
                "started spinning",
                "spinning",
                "finished spinning",
        };
        for(String s : soundStrings) {
            final String target = config.getString("sounds." + s);
            if(target != null) {
                sounds.put(s, new CustomSound(target));
            }
        }

        settings = new HashMap<>();
        for(SlotBotSetting setting : SlotBotSetting.values()) {
            settings.put(setting, config.getBoolean("settings." + setting.name().toLowerCase().replace("_", " ")));
        }

        ticket = createItemStack(config, "items.ticket");
        GivedpItem.INSTANCE.items.put("slotbotticket", ticket);
        
        ticket = createItemStack(config, "items.ticket");
        ticketLocked = createItemStack(config, "items.ticket locked");
        ticketUnlocked = createItemStack(config, "items.ticket unlocked");
        rewardSlot = createItemStack(config, "items.reward slot");
        withdraw_tickets = createItemStack(config, "items.withdraw tickets");
        spinnerMissingTickets = createItemStack(config, "items.spinner missing ticket");
        spinner_ready_to_spin = createItemStack(config, "items.spinner ready to spin");
        spinnerSlot = config.getInt("items.spinner missing ticket.slot");

        preview = new UInventory(null, config.getInt("preview rewards.size"), colorize(config.getString("preview rewards.title")));
        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        randomizedLootPlaceholder = createItemStack(config, "items.randomized loot placeholder");
        randomizedLootReadyToRoll = createItemStack(config, "items.randomized loot ready to roll");
        background = createItemStack(config, "gui.background");

        final Inventory inv = gui.getInventory();
        inv.setItem(spinnerSlot, spinnerMissingTickets);

        slots = new HashMap<>();
        ticket_slots = new ArrayList<>();
        int ticketAmount = 0;
        for(String s : config.getStringList("items.ticket.slots")) {
            ticketAmount++;
            final int slot = Integer.parseInt(s);
            inv.setItem(slot, getTicketLocked(ticketAmount));
            ticket_slots.add(slot);
        }

        for(String key : getConfigurationSectionKeys(config, "gui.reward slots", false)) {
            final int slot = Integer.parseInt(key);
            inv.setItem(slot, rewardSlot);
            final List<Integer> rewardSlots = new ArrayList<>();
            for(String s : config.getStringList("gui.reward slots." + key)) {
                final int rewardSlot = Integer.parseInt(s);
                inv.setItem(rewardSlot, randomizedLootPlaceholder);
                rewardSlots.add(rewardSlot);
            }
            slots.put(slot, rewardSlots);
        }
        for(String key : getConfigurationSectionKeys(config, "gui", false, "title", "size", "background", "reward slots", "visual placeholder slots")) {
            final String path = "gui." + key;
            final String item = config.getString(path + ".item");
            final int slot = config.getInt(path + ".slot");
            final boolean isWithdraw = "WITHDRAW_TICKETS".equals(item);
            if(isWithdraw) {
                withdrawTicketsSlot = slot;
            }
            inv.setItem(slot, isWithdraw ? null : createItemStack(config, path));
        }

        final ItemStack visualPlaceholder = createItemStack(config, "items.visual placeholder");
        for(String s : config.getStringList("gui.visual placeholder slots")) {
            inv.setItem(Integer.parseInt(s), visualPlaceholder);
        }

        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null) {
                inv.setItem(i, background);
            }
        }

        rolling_tasks = new HashMap<>();
        pending_reward_slots = new HashMap<>();
        unrolled_tickets = new HashMap<>();
        rewards = config.getStringList("rewards");

        preview_rewards_slot = config.getInt("items.preview rewards.slot");
        previewRewards = createItemStack(config, "items.preview rewards");
        final ItemMeta itemMeta = previewRewards.getItemMeta();

        final List<ItemStack> previewRewardList = new ArrayList<>();
        final List<String> lore = new ArrayList<>(), actualRewards = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            if(s.contains("{AMOUNT}") && s.contains("{ITEM}")) {
                for(String reward : rewards) {
                    final ItemStack is = createItemStack(null, reward);
                    if(is != null) {
                        actualRewards.add(reward);
                        ItemMeta meta = is.getItemMeta();
                        lore.add(s.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", meta.hasDisplayName() ? meta.getDisplayName() : is.getType().name()));
                        previewRewardList.add(is);
                    }
                }
            } else {
                lore.add(s);
            }
        }
        itemMeta.setLore(lore);
        previewRewards.setItemMeta(itemMeta);
        inv.setItem(preview_rewards_slot, previewRewards);
        rewards = actualRewards;

        final Inventory previewInv = preview.getInventory();
        for(ItemStack is : previewRewardList) {
            previewInv.setItem(previewInv.firstEmpty(), is);
        }
    }
    @Override
    public void unload() {
        for(Player player : new ArrayList<>(pending_reward_slots.keySet())) {
            player.closeInventory();
        }
    }

    @Nullable
    private CustomItem valueOfCustomItem(ItemStack item) {
        if(item != null && customItems != null) {
            for(CustomItem customItem : customItems) {
                if(item.isSimilar(customItem.getItem())) {
                    return customItem;
                }
            }
        }
        return null;
    }

    private void playSound(Player player, String identifier) {
        final CustomSound sound = sounds.getOrDefault(identifier, null);
        if(sound != null) {
            final USound usound = sound.getUSound();
            final Sound realSound = usound.getSound();
            if(realSound != null) {
                usound.playSound(player, sound.getVolume(), sound.getPitch());
            }
        }
    }
    public boolean isSlotBotSettingEnabled(@NotNull SlotBotSetting setting) {
        return settings.getOrDefault(setting, false);
    }

    @NotNull
    private ItemStack getTicketLocked(int ticketAmount) {
        final ItemStack item = ticketLocked.clone();
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String string : itemMeta.getLore()) {
            lore.add(string.replace("{AMOUNT}", Integer.toString(ticketAmount)));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        item.setAmount(ticketAmount);
        return item;
    }

    public void view(@NotNull Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(gui.getInventory().getContents());
        player.updateInventory();
    }
    public void viewPreview(@NotNull Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, preview.getSize(), preview.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(preview.getInventory().getContents());
        player.updateInventory();
    }
    public void tryWithdrawingTickets(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        int ticketAmount = 0;
        final boolean is_unrolled = unrolled_tickets.containsKey(player);
        final List<Integer> unrolled_slots = is_unrolled ? unrolled_tickets.get(player) : null;
        final List<Integer> keySet = new ArrayList<>(slots.keySet());
        for(int i : ticket_slots) {
            final int reward_slot = keySet.get(ticketAmount);
            ticketAmount++;
            final boolean isActuallyUnrolled = is_unrolled && unrolled_slots.contains(reward_slot);
            if(ticketUnlocked.isSimilar(top.getItem(i)) && isActuallyUnrolled) {
                giveItem(player, ticket);
                top.setItem(i, getTicketLocked(keySet.indexOf(reward_slot)+1));
                for(int slot : slots.get(reward_slot)) {
                    top.setItem(slot, randomizedLootPlaceholder);
                }
            }
        }
        top.setItem(withdrawTicketsSlot, background);
        top.setItem(spinnerSlot, spinnerMissingTickets);
        player.updateInventory();
        playSound(player, "withdraw tickets");
        unrolled_tickets.remove(player);
    }
    public int getInsertedTickets(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        int total = 0;
        for(int slot : ticket_slots) {
            if(ticketUnlocked.isSimilar(top.getItem(slot))) {
                total++;
            }
        }
        return total;
    }
    public void tryInsertingTicket(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        final int inserted = getInsertedTickets(player), maxAllowed = ticket_slots.size();
        if(inserted < maxAllowed) {
            final ItemStack withdraw = top.getItem(withdrawTicketsSlot), spin = top.getItem(spinnerSlot);
            if(!withdraw_tickets.isSimilar(withdraw)) {
                top.setItem(withdrawTicketsSlot, withdraw_tickets);
            }
            if(!spinner_ready_to_spin.isSimilar(spin)) {
                top.setItem(spinnerSlot, spinner_ready_to_spin);
            }
            for(int slot : slots.get(slots.keySet().toArray()[inserted])) {
                top.setItem(slot, randomizedLootReadyToRoll);
            }
            removeItem(player, ticket, 1);
            final int slot = ticket_slots.get(inserted);
            final ItemStack item = getClone(ticketUnlocked);
            item.setAmount(inserted+1);
            top.setItem(slot, item);
            player.updateInventory();
            playSound(player, "insert ticket");

            if(!unrolled_tickets.containsKey(player)) {
                unrolled_tickets.put(player, new ArrayList<>());
            }
            unrolled_tickets.get(player).add(slots.keySet().toArray(new Integer[slots.size()])[inserted]);
        }
    }
    public boolean trySpinning(@NotNull Player player, int slot, @NotNull ItemStack targetItem) {
        if(spinner_ready_to_spin.isSimilar(targetItem)) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setItem(withdrawTicketsSlot, background);
            List<Integer> insertedTickets = new ArrayList<>();
            final boolean isUnrolled = unrolled_tickets.containsKey(player);
            final List<Integer> unrolledSlots = isUnrolled ? unrolled_tickets.get(player) : null;
            int ticket = 0;
            for(int ticketSlot : ticket_slots) {
                if(ticketUnlocked.isSimilar(top.getItem(ticketSlot))) {
                    final int rewardSlot = (int) slots.keySet().toArray()[ticket];
                    if(isUnrolled && unrolledSlots.contains(rewardSlot)) {
                        insertedTickets.add(rewardSlot);
                    }
                    ticket++;
                }
            }
            unrolled_tickets.remove(player);
            if(!insertedTickets.isEmpty()) {
                for(int rewardSlot : insertedTickets) {
                    startRolling(player, top, rewardSlot);
                }
                playSound(player, "started spinning");
                return true;
            }
        } else if(ticket_slots.contains(slot) && !ticketUnlocked.isSimilar(targetItem)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final int index = ticket_slots.indexOf(slot);
            replacements.put("{AMOUNT}", Integer.toString(index+1));
            sendStringListMessage(player, getStringList(config, "messages.slot requires ticket"), replacements);
            playSound(player, "cancelled");
        }
        return false;
    }
    private ItemStack getRandomReward(int size) {
        return createItemStack(null, rewards.get(RANDOM.nextInt(size)));
    }
    private void updateRandomLoot(Player player, Inventory top, int rewardSlot, boolean isRandom, boolean playSound) {
        if(playSound) {
            playSound(player, "spinning");
        }
        final int size = rewards.size();

        final List<Integer> slots = new ArrayList<>(this.slots.get(rewardSlot));
        slots.add(rewardSlot);
        Collections.sort(slots);
        final List<ItemStack> previousRewards = new ArrayList<>();
        previousRewards.add(null);

        if(!isRandom) {
            for(int slot : slots) {
                previousRewards.add(top.getItem(slot));
            }
        }
        top.setItem(slots.get(0), getRandomReward(size));
        int index = 0;
        for(int slot : slots) {
            ItemStack target = isRandom ? getRandomReward(size) : previousRewards.get(index);
            if(target == null) {
                target = getRandomReward(size);
            }
            top.setItem(slot, target);
            index++;
        }
        player.updateInventory();
    }
    private void startRolling(Player player, Inventory top, int rewardSlot) {
        if(!rolling_tasks.containsKey(player)) {
            rolling_tasks.put(player, new HashMap<>());
        }
        if(!pending_reward_slots.containsKey(player)) {
            pending_reward_slots.put(player, new ArrayList<>());
        }
        final HashMap<Integer, List<Integer>> slotTasks = rolling_tasks.get(player);
        if(!slotTasks.containsKey(rewardSlot)) {
            slotTasks.put(rewardSlot, new ArrayList<>());
        }

        final List<Integer> tasks = slotTasks.get(rewardSlot);
        pending_reward_slots.get(player).add(rewardSlot);

        final boolean isRandom = isSlotBotSettingEnabled(SlotBotSetting.ALWAYS_RANDOM_LOOT);

        updateRandomLoot(player, top, rewardSlot, true, false);
        for(int i = 1; i <= 10; i++) {
            tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                updateRandomLoot(player, top, rewardSlot, isRandom, true);
            }, i*5));
        }
        for(int i = 1; i <= 10; i++) {
            final int I = i;
            tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                updateRandomLoot(player, top, rewardSlot, isRandom, true);
                if(I == 10) {
                    stopRolling(player, rewardSlot, true);
                }
            }, 50+(i*10)));
        }
    }
    public void stopRolling(@NotNull Player player) {
        for(int rewardSlot : slots.keySet()) {
            stopRolling(player, rewardSlot, false);
        }
        playSound(player, "finished spinning");
    }
    public void stopRolling(@NotNull Player player, int rewardSlot, boolean playSound) {
        final List<Integer> pendingSlots = pending_reward_slots.getOrDefault(player, null);
        if(pendingSlots != null && pendingSlots.contains(rewardSlot) && rolling_tasks.containsKey(player)) {
            final HashMap<Integer, List<Integer>> tasks = rolling_tasks.get(player);
            if(tasks.containsKey(rewardSlot)) {
                for(int task : tasks.get(rewardSlot)) {
                    SCHEDULER.cancelTask(task);
                }
                final Inventory top = player.getOpenInventory().getTopInventory();
                for(int slot : slots.get(rewardSlot)) {
                    top.setItem(slot, randomizedLootPlaceholder);
                }
                tasks.remove(rewardSlot);
                player.updateInventory();
                if(playSound) {
                    playSound(player, "finished spinning");
                }
            }
        }
    }
    public boolean isCustomItemThatInstantlyExecutesCommands(@Nullable CustomItem customItem) {
        return customItem != null && isSlotBotSettingEnabled(SlotBotSetting.INSTANT_CUSTOM_ITEM_COMMAND_EXECUTION) && customItem.doesExecuteCommands();
    }
    private void giveLoot(@NotNull Player player) {
        final List<ItemStack> items = new ArrayList<>();
        final List<CustomItem> executeCustomItemCommands = new ArrayList<>();
        final int slotsSize = slots.size();
        int tickets = 0;
        if(pending_reward_slots.containsKey(player)) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            int ticketsInserted = getInsertedTickets(player);
            tickets += ticketsInserted;

            for(int i : slots.keySet()) {
                final ItemStack item = top.getItem(i);
                if(item != null && ticketsInserted > 0) {
                    if(!rewardSlot.isSimilar(item)) {
                        items.add(item);
                        ticketsInserted -= 1;
                    } else {
                        tickets -= 1;
                    }
                }
            }
            pending_reward_slots.remove(player);
        }
        if(unrolled_tickets.containsKey(player)) {
            final List<Integer> unrolledPlayerTickets = unrolled_tickets.get(player);
            final int size = unrolledPlayerTickets.size();
            tickets += size;
            for(int i = 1; i <= size; i++) {
                items.add(getRandomReward(slotsSize));
            }
            unrolled_tickets.remove(player);
        }

        if(!items.isEmpty()) {
            for(ItemStack is : items) {
                final CustomItem customItem = valueOfCustomItem(is);
                if(isCustomItemThatInstantlyExecutesCommands(customItem)) {
                    executeCustomItemCommands.add(customItem);
                } else {
                    giveItem(player, is);
                }
            }
            final String playerName = player.getName(), ticketsInserted = Integer.toString(tickets);
            final boolean isCentered = config.getBoolean("messages.loot.centered");
            for(String string : getStringList(config, "messages.loot.msg")) {
                string = string.replace("{PLAYER}", playerName).replace("{TICKETS}", ticketsInserted);
                if(string.contains("{AMOUNT}") && string.contains("{ITEM}")) {
                    for(ItemStack is : items) {
                        final ItemMeta itemMeta = is.getItemMeta();
                        final String name = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : is.getType().name();
                        final String target = string.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", name);
                        final String message = isCentered ? center(target, 70) : target;
                        final TextComponent hover = getHoverMessage(message, is);
                        sendHoverMessage(null, hover, true);
                    }
                } else {
                    Bukkit.broadcastMessage(isCentered ? center(string, 70) : string);
                }
            }
            for(CustomItem customItem : executeCustomItemCommands) {
                customItem.executeCommands(player);
            }
            player.updateInventory();
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final boolean isSpinning = rolling_tasks.containsKey(player);
        final boolean isInSlotBot = isSpinning || unrolled_tickets.containsKey(player);
        if(isInSlotBot) {
            if(isSpinning) {
                final int size = rolling_tasks.get(player).size();
                if(size > 0) {
                    if(isSlotBotSettingEnabled(SlotBotSetting.INVENTORY_IS_CLOSEABLE_WHEN_SPINNING)) {
                        stopRolling(player);
                        rolling_tasks.remove(player);
                    } else {
                        final Inventory inv = event.getInventory();
                        SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                            player.openInventory(inv);
                        }, 0);
                        return;
                    }
                }
            }
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
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        stopRolling(player);
        giveLoot(player);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(player.equals(top.getHolder())) {
            final String title = event.getView().getTitle();
            final boolean isGUI = title.equals(gui.getTitle());
            if(isGUI || title.equals(preview.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final ItemStack current = event.getCurrentItem();
                if(current == null || !isGUI) {
                    return;
                }
                final int slot = event.getRawSlot();

                if(slot >= top.getSize()) {
                    if(current.isSimilar(ticket)) {
                        tryInsertingTicket(player);
                    }
                } else if(slot == preview_rewards_slot) {
                    if(!rolling_tasks.containsKey(player) || rolling_tasks.get(player).size() == 0) {
                        viewPreview(player);
                    }
                } else if(current.isSimilar(spinnerMissingTickets)) {
                    sendStringListMessage(player, getStringList(config, "messages.missing tickets"), null);
                } else if(current.isSimilar(spinner_ready_to_spin)) {
                    for(int i : ticket_slots) {
                        trySpinning(player, i, spinner_ready_to_spin);
                    }
                } else if(current.isSimilar(withdraw_tickets)) {
                    tryWithdrawingTickets(player);
                } else if(ticket_slots.contains(slot)) {
                    trySpinning(player, slot, current);
                }
            }
        }
    }
}
