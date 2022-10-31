package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ChatUtils extends UVersionableSpigot, RPItemStack {
    default TextComponent getHoverMessage(@NotNull String message, @NotNull ItemStack is) {
        final TextComponent component = new TextComponent(message);
        final String craftItem = asNMSCopy(is);
        if(craftItem == null || craftItem.isEmpty()) {
            return null;
        }
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(craftItem).create()));
        return component;
    }
    default void sendHoverMessage(Player player, TextComponent component, boolean isServer) {
        if(isServer) {
            SERVER.spigot().broadcast(component);
        } else {
            player.spigot().sendMessage(component);
        }
    }
}
