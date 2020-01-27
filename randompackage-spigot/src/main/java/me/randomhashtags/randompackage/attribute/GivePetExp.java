package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GivePetExp extends AbstractEventAttribute implements RPItemStack {
    @Override
    public void execute(PendingEventAttribute pending, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        final ItemStack is = event instanceof PlayerInteractEvent ? ((PlayerInteractEvent) event).getItem() : null;
        if(is != null) {
            for(Entity entity : recipientValues.keySet()) {
                if(entity instanceof Player) {
                    final String[] values = recipientValues.get(entity).split(":");
                    givePetExp((Player) entity, is, (int) evaluate(replaceValue(entities, values[0], valueReplacements)), values.length >= 2 && Boolean.parseBoolean(values[1]));
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
