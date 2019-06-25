package me.randomhashtags.randompackage.utils.supported.plugins;

import de.dustplanet.util.SilkUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class silkSpawners {

    private static silkSpawners instance;
    public static silkSpawners getSilkSpawners() {
        if(instance == null) {
            instance = new silkSpawners();
            util = SilkUtil.hookIntoSilkSpanwers();
        }
        return instance;
    }
    private static SilkUtil util;

    public ItemStack getItem(String entitytype) {
        final String input = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        for(EntityType t : EntityType.values()) {
            if(input.equals(t.name().replace("_", "").replace(" ", ""))) {
                final short id = t.getTypeId();
                return util.newSpawnerItem(id, util.getCustomSpawnerName(util.getCreatureName(id)), 1, false);
            }
        }
        return null;
    }
}
