package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Trinket extends RPAddon {
    public abstract String getRadius();
    public abstract String getCooldown();
    public abstract ItemStack getItem();
    public abstract List<String> getAttributes();
}
