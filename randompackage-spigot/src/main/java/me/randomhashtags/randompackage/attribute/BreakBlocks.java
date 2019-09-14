package me.randomhashtags.randompackage.attribute;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashMap;

public class BreakBlocks extends AbstractEventAttribute {
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            final Block b = l.getBlock();
            final String[] values = locations.get(l).split(":");
            final int c = values.length, ticks = c >= 2 ? Integer.parseInt(values[1]) : -1;
            final boolean naturally = c >= 1 && Boolean.parseBoolean(values[0]);
            if(ticks != -1) {
                final Material m = b.getType();
                final byte d = b.getData();
                final BlockState bs = b.getState();
                scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                    b.setType(m);
                    if(LEGACY) {
                        bs.setRawData(d);
                    }
                    bs.update();
                }, ticks);
            }
            if(naturally) {
                b.breakNaturally();
            } else {
                b.setType(Material.AIR);
            }
        }
    }
}
