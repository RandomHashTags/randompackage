package me.randomhashtags.randompackage.attributes;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class GiveDrops extends AbstractEventAttribute {
    @Override
    public void execute(Event event) {
        final BlockBreakEvent e = event instanceof BlockBreakEvent ? (BlockBreakEvent) event : null;
        if(e != null) {
            final Player player = e.getPlayer();
            final Block b = e.getBlock();
            final Collection<ItemStack> drops = b.getDrops();
            for(ItemStack i : drops) {
                giveItem(player, i);
            }
            e.setCancelled(true);
            b.setType(Material.AIR);
        }
    }
}
