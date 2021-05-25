package me.randomhashtags.randompackage.event.kit;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitClaimEvent extends RPEventCancellable {
    private final RPPlayer pdata;
    private final Player player;
    private final CustomKit kit;
    private final int level;
    private final List<ItemStack> lootObtained;
    public KitClaimEvent(RPPlayer pdata, Player player, CustomKit kit, int level, List<ItemStack> lootObtained) {
        super(player);
        this.pdata = pdata;
        this.player = player;
        this.kit = kit;
        this.level = level;
        this.lootObtained = lootObtained;
    }
    public RPPlayer getRPPlayer() {
        return pdata;
    }
    public Player getPlayer() {
        return player; }
    public CustomKit getKit() {
        return kit;
    }
    public int getLevel() {
        return level;
    }
    public List<ItemStack> getLootObtained() {
        return lootObtained;
    }
}
