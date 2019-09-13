package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GivePetExp extends AbstractEventAttribute implements RPItemStack {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        final ItemStack is = event instanceof PlayerInteractEvent ? ((PlayerInteractEvent) event).getItem() : null;
        if(is != null) {
            for(Entity e : recipientValues.keySet()) {
                if(e instanceof Player) {
                    final String[] values = recipientValues.get(e).split(":");
                    givePetExp((Player) e, is, (int) evaluate(replaceValue(values[0], valueReplacements)), Boolean.parseBoolean(values[1]));
                }
            }
        }
    }
    private void givePetExp(Player player, ItemStack is, int amount, boolean addCooldown) {
        final String id = getRPItemStackValue(is, "InventoryPetInfo");
        if(id != null) {
            final String[] info = id.split(":");
            final String identifier = info[0];
            final int level = Integer.parseInt(info[1]), exp = Integer.parseInt(info[2]);
            final long expiration = Long.parseLong(info[3]);
            final InventoryPet pet = getInventoryPet(identifier);
            pet.setItem(is, identifier, level, exp+amount, addCooldown ? System.currentTimeMillis()+pet.getCooldown(level) : expiration);
            player.updateInventory();
        }
    }
}
