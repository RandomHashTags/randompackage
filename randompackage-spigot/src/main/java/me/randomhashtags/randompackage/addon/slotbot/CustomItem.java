package me.randomhashtags.randompackage.addon.slotbot;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomItem extends Identifiable, Itemable, UVersionableSpigot {
    List<String> getCommands();
    default boolean doesExecuteCommands() {
        final List<String> commands = getCommands();
        return commands != null && !commands.isEmpty();
    }
    default boolean executeCommands(@NotNull Player player) {
        if(doesExecuteCommands()) {
            final String playerName = player.getName();
            final List<String> commands = getCommands();
            for(String command : commands) {
                SERVER.dispatchCommand(CONSOLE, command.replace("%player%", playerName));
            }
            return true;
        }
        return false;
    }
}
