package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.api.Trade;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ActiveTrade implements UVersionableSpigot {
    public static List<ActiveTrade> ACTIVE_TRADES;

    private final List<Integer> tasks;
    private boolean successful, senderReady, receiverReady, countingDown;
    private final Player sender, receiver;
    private final HashMap<Integer, ItemStack> sendingTrade, receivingTrade;

    public ActiveTrade(Player sender, Player receiver) {
        if(ACTIVE_TRADES == null) {
            ACTIVE_TRADES = new ArrayList<>();
        }
        this.sender = sender;
        this.receiver = receiver;
        tasks = new ArrayList<>();
        sendingTrade = new HashMap<>();
        receivingTrade = new HashMap<>();
        ACTIVE_TRADES.add(this);
    }

    private Trade getTrade() {
        return Trade.INSTANCE;
    }
    public boolean isSuccessful() {
        return successful;
    }
    public Player getSender() {
        return sender;
    }
    public Player getReceiver() {
        return receiver;
    }
    public HashMap<Integer, ItemStack> getSenderTrade() {
        return sendingTrade;
    }
    public HashMap<Integer, ItemStack> getReceiverTrade() {
        return receivingTrade;
    }
    public boolean senderIsReady() {
        return senderReady;
    }
    public boolean receiverIsReady() {
        return receiverReady;
    }
    public void setSenderReady(boolean ready) {
        senderReady = ready;
        tryAccepting();
    }
    public void setReceiverReady(boolean ready) {
        receiverReady = ready;
        tryAccepting();
    }
    private void tryAccepting() {
        if(senderReady && receiverReady) {
            close(true);
        }
    }

    public void updateTrades() {
        cancelCountdown();
        final ItemStack air = new ItemStack(Material.AIR);
        final Inventory senderInv = sender.getOpenInventory().getTopInventory(), receiverInv = receiver.getOpenInventory().getTopInventory();
        for(int i = 0; i < 54; i++) {
            if(isOnSelfSide(i) || isOnOtherSide(i)) {
                senderInv.setItem(i, air);
                receiverInv.setItem(i, air);
            }
        }
        for(int i : sendingTrade.keySet()) {
            final ItemStack is = sendingTrade.get(i);
            senderInv.setItem(i, is);
            receiverInv.setItem(getOpposite(i), is);
        }
        for(int i : receivingTrade.keySet()) {
            final ItemStack is = receivingTrade.get(i);
            receiverInv.setItem(i, is);
            senderInv.setItem(getOpposite(i), is);
        }
        sender.updateInventory();
        receiver.updateInventory();
    }
    private int getOpposite(int slot) {
        return slot <= 3 ? slot+4 : slot >= 5 && slot <= 8 ? slot-4 : isOnSelfSide(slot) ? slot+5 : slot-5;
    }
    public int getNextEmptySlot(@NotNull Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(int i = 0; i <= 48; i++) {
            if(isOnSelfSide(i) && (top.getItem(i) == null || top.getItem(i).getType().equals(Material.AIR))) {
                return i;
            }
        }
        return -1;
    }

    public boolean isOnSelfSide(int slot) {
        return slot >= 1 && slot <= 3 || slot >= 9 && slot <= 12 || slot >= 18 && slot <= 21 || slot >= 27 && slot <= 30 || slot >= 36 && slot <= 39 || slot >= 45 && slot <= 48;
    }
    public boolean isOnOtherSide(int slot) {
        return slot >= 5 && slot <= 7 || slot >= 14 && slot <= 17 || slot >= 23 && slot <= 26 || slot >= 32 && slot <= 35 || slot >= 41 && slot <= 44 || slot >= 50 && slot <= 53;
    }

    public void cancel() {
        close(false);
    }
    public void accept() {
        close(true);
    }

    private void close(boolean accepted) {
        successful = accepted;
        if(accepted) {
            countingDown = true;
            final int countdown = getTrade().getCountdown();
            setCountdown(true, countdown);
            for(int i = 1; i <= countdown; i++) {
                final int integer = i;
                tasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                    setCountdown(true, countdown-integer);
                    if(integer == countdown) {
                        giveTradedItems();
                        stopTrade();
                    }
                }, i*20));
            }
        } else {
            giveBackItems();
            stopTrade();
        }
    }

    private void setCountdown(boolean setAccepting, int integer) {
        final Inventory top = sender.getOpenInventory().getTopInventory(), t = receiver.getOpenInventory().getTopInventory();
        final ItemStack i = (setAccepting ? getTrade().accepting : getTrade().accept).clone();
        i.setAmount(integer);
        top.setItem(0, i);
        top.setItem(8, i);
        sender.updateInventory();
        t.setItem(0, i);
        t.setItem(8, i);
        receiver.updateInventory();
    }
    public void cancelCountdown() {
        if(countingDown) {
            countingDown = false;
            for(int task : tasks) {
                SCHEDULER.cancelTask(task);
            }
            tasks.clear();
            senderReady = false;
            receiverReady = false;

            setCountdown(false, getTrade().getCountdown());
        }
    }

    private void giveBackItems() {
        for(ItemStack is : sendingTrade.values()) {
            giveItem(sender, is);
        }
        for(ItemStack is : receivingTrade.values()) {
            giveItem(receiver, is);
        }
    }
    private void giveTradedItems() {
        for(ItemStack is : receivingTrade.values()) {
            giveItem(sender, is);
        }
        for(ItemStack is : sendingTrade.values()) {
            giveItem(receiver, is);
        }
    }

    private void stopTrade() {
        ACTIVE_TRADES.remove(this);
        sender.closeInventory();
        receiver.closeInventory();

        if(ACTIVE_TRADES.isEmpty()) {
            ACTIVE_TRADES = null;
        }
    }

    public static ActiveTrade valueOf(Player player) {
        if(ACTIVE_TRADES != null) {
            for(ActiveTrade trade : ACTIVE_TRADES) {
                if(trade.sender == player || trade.receiver == player) {
                    return trade;
                }
            }
        }
        return null;
    }
}
