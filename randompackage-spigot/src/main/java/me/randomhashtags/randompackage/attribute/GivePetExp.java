package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class GivePetExp extends AbstractEventAttribute implements RPItemStack {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {
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
    private void givePetExp(Player player, @NotNull ItemStack is, int amount, boolean addCooldown) {
        final String info = getRPItemStackValue(is, "InventoryPetInfo");
        if(info != null) {
            final String[] values = info.split(":");
            final String identifier = values[0];
            final int level = Integer.parseInt(values[1]), exp = Integer.parseInt(values[2]);
            final long expiration = Long.parseLong(values[3]);
            final InventoryPet targetPet = getInventoryPet(identifier);
            if(targetPet != null) {
                targetPet.set_inventory_pet_values(is, identifier, level, exp+amount, addCooldown ? System.currentTimeMillis()+targetPet.getCooldown(level) : expiration);
                player.updateInventory();
            }
        }
    }
}
