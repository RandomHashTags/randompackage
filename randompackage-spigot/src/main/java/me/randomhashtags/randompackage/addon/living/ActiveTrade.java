package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.universal.UVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveTrade {
    public static List<ActiveTrade> trades;
    private static UVersion uv;

    private List<Integer> tasks;
    private boolean successful, senderReady, receiverReady;
    private Player sender, receiver;
    private HashMap<Integer, ItemStack> sendingTrade, receivingTrade;

    public ActiveTrade(Player sender, Player receiver) {
        if(trades == null) {
            trades = new ArrayList<>();
            uv = UVersion.getUVersion();
        }
        tasks = new ArrayList<>();
        successful = false;
        senderReady = false;
        receiverReady = false;
        this.sender = sender;
        this.receiver = receiver;
        sendingTrade = new HashMap<>();
        receivingTrade = new HashMap<>();
        trades.add(this);
    }
    public boolean isSuccessful() { return successful; }
    public Player getSender() { return sender; }
    public Player getReceiver() { return receiver; }
    public HashMap<Integer, ItemStack> getSenderTrade() { return sendingTrade; }
    public HashMap<Integer, ItemStack> getReceiverTrade() { return receivingTrade; }
    public boolean senderIsReady() { return senderReady; }
    public boolean receiverIsReady() { return receiverReady; }
    public void setSenderReady(boolean ready) {
        senderReady = ready;
        if(senderReady && receiverReady) close(true);
    }
    public void setReceiverReady(boolean ready) {
        receiverReady = ready;
        if(senderReady && receiverReady) close(true);
    }
    public void updateTrades() {
        final ItemStack air = new ItemStack(Material.AIR);
        final Inventory s = sender.getOpenInventory().getTopInventory(), r = receiver.getOpenInventory().getTopInventory();
        for(int i = 0; i < 54; i++) {
            if(isOnSelfSide(i) || isOnOtherSide(i)) {
                s.setItem(i, air);
                r.setItem(i, air);
            }
        }
        for(int i : sendingTrade.keySet()) {
            final ItemStack is = sendingTrade.get(i);
            s.setItem(i, is);
            r.setItem(getOpposite(i), is);
        }
        for(int i : receivingTrade.keySet()) {
            final ItemStack is = receivingTrade.get(i);
            r.setItem(i, is);
            s.setItem(getOpposite(i), is);
        }
        sender.updateInventory();
        receiver.updateInventory();
    }
    private int getOpposite(int slot) {
        return slot <= 3 ? slot+4 : slot >= 5 && slot <= 8 ? slot-4 : isOnSelfSide(slot) ? slot+5 : slot-5;
    }
    public int getNextEmptySlot(Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(int i = 0; i <= 48; i++) {
            if(isOnSelfSide(i) && (top.getItem(i) == null || top.getItem(i).getType().equals(Material.AIR))) {
                return i;
            }
        }
        return -1;
    }

    public boolean isOnSelfSide(int slot) { return slot >= 1 && slot <= 3 || slot >= 9 && slot <= 12 || slot >= 18 && slot <= 21 || slot >= 27 && slot <= 30 || slot >= 36 && slot <= 39 || slot >= 45 && slot <= 48; }
    public boolean isOnOtherSide(int slot) { return slot >= 5 && slot <= 7 || slot >= 14 && slot <= 17 || slot >= 23 && slot <= 26 || slot >= 32 && slot <= 35 || slot >= 41 && slot <= 44 || slot >= 50 && slot <= 53; }

    public void cancel() {
        close(false);
    }
    public void accept() {
        close(true);
    }
    private void close(boolean accepted) {
        successful = accepted;
        for(ItemStack is : (accepted ? receivingTrade : sendingTrade).values()) {
            uv.giveItem(sender, is);
        }
        for(ItemStack is : (accepted ? sendingTrade : receivingTrade).values()) {
            uv.giveItem(receiver, is);
        }
        trades.remove(this);
        sender.closeInventory();
        receiver.closeInventory();

        tasks = null;
        sender = null;
        receiver = null;
        sendingTrade = null;
        receivingTrade = null;
        if(trades.isEmpty()) {
            trades = null;
            uv = null;
        }
    }


    public static ActiveTrade valueOf(Player player) {
        if(trades != null) {
            for(ActiveTrade a  : trades) {
                if(a.sender == player || a.receiver == player) {
                    return a;
                }
            }
        }
        return null;
    }
}
