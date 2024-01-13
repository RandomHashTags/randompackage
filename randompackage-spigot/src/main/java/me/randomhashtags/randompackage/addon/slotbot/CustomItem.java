package me.randomhashtags.randompackage.addon.slotbot;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CustomItem extends Identifiable, Itemable, UVersionableSpigot {
    @Nullable List<String> getCommands();
    default boolean doesExecuteCommands() {
        final List<String> commands = getCommands();
        return commands != null && !commands.isEmpty();
    }
    default void executeCommands(@NotNull Player player) {
        final List<String> commands = getCommands();
        if(commands != null && !commands.isEmpty()) {
            final String playerName = player.getName();
            for(String command : commands) {
                SERVER.dispatchCommand(CONSOLE, command.replace("%player%", playerName));
            }
        }
    }
}
