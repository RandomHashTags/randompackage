package me.randomhashtags.randompackage.attribute;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class BreakBlocks extends AbstractEventAttribute {
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Map.Entry<Location, String> locationMap : locations.entrySet()) {
            final Block block = locationMap.getKey().getBlock();
            final String[] values = locationMap.getValue().split(":");
            final int c = values.length, ticks = c >= 2 ? Integer.parseInt(values[1]) : -1;
            final boolean naturally = c >= 1 && Boolean.parseBoolean(values[0]);
            if(ticks != -1) {
                final Material m = block.getType();
                final byte d = block.getData();
                final BlockState bs = block.getState();
                SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                    block.setType(m);
                    if(LEGACY) {
                        bs.setRawData(d);
                    }
                    bs.update();
                }, ticks);
            }
            if(naturally) {
                block.breakNaturally();
            } else {
                block.setType(Material.AIR);
            }
        }
    }
}
