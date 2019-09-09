package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.RandomizationScroll;
import org.bukkit.entity.Player;

public class RandomizationScrollUseEvent extends RPEventCancellable {
    public final CustomEnchant customenchant;
    public final int enchantlevel;
    public final RandomizationScroll scroll;
    private int newSuccess, newDestroy;
    public RandomizationScrollUseEvent(Player player, CustomEnchant customenchant, int enchantlevel, RandomizationScroll scroll, int newSuccess, int newDestroy) {
        super(player);
        this.customenchant = customenchant;
        this.enchantlevel = enchantlevel;
        this.scroll = scroll;
        this.newSuccess = newSuccess;
        this.newDestroy = newDestroy;
    }
    public int getNewSuccess() { return newSuccess; }
    public void setNewSuccess(int success) { newSuccess = success; }
    public int getNewDestroy() { return newDestroy; }
    public void setNewDestroy(int destroy) { newDestroy = destroy; }
}
