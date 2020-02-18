package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface GivedpItemable extends RPStorage {
    HashMap<String, GivedpItemable> GIVEDP_ITEMS = new HashMap<>();
    String[] getGivedpItemIdentifiers();
    ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput);
}
