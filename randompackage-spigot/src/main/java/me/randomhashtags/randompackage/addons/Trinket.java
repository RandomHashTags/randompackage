package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.utils.RPAddon;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Trinket extends RPAddon {
    public abstract String getRadius();
    public abstract String getCooldown();
    public abstract ItemStack getItem();
    public abstract List<String> getAttributes();
}
