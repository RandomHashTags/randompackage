package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface GivedpItemableSpigot extends RPStorage {
    HashMap<String, GivedpItemableSpigot> GIVEDP_ITEMS = new HashMap<>();
    String[] getGivedpItemIdentifiers();
    ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput);
}
