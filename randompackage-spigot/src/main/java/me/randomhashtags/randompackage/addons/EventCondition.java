package me.randomhashtags.randompackage.addons;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.utils.Mathable;
import org.bukkit.entity.Player;

public interface EventCondition extends Identifiable, Mathable {
    void load();
    boolean check(@NotNull Player player);
}
