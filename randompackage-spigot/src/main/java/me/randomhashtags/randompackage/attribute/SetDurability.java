package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SetDurability extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof PlayerItemDamageEvent) {
            final PlayerItemDamageEvent e = (PlayerItemDamageEvent) event;
            setDurability(e.getPlayer(), value, e.getItem());
        } else if(event instanceof CustomEnchantProcEvent) {
            final CustomEnchantProcEvent e = (CustomEnchantProcEvent) event;
            setDurability(e.getEntities().get("Player"), value, e.getItemWithEnchant());
        }
    }
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            setDurability(e, recipientValues.get(e));
        }
    }
    private void setDurability(Entity entity, String value) { setDurability(entity, value, null); }
    private void setDurability(Entity entity, String value, ItemStack item) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final EntityEquipment e = l.getEquipment();
            if(e != null) {
                final String[] values = value.split(":");
                final ItemStack[] toSet = getTargets(e, values[0], item);
                if(toSet != null) {
                    for(ItemStack is : toSet) {
                        if(is != null) {
                            final double dura = evaluate(values[1].replace("durability", Short.toString(is.getDurability())));
                            is.setDurability((short) (dura < 0 ? 0 : dura));
                        }
                    }
                }
            }
        }
    }
    private ItemStack[] getTargets(EntityEquipment e, String value, ItemStack item) {
        final ItemStack helmet = e.getHelmet(), chestplate = e.getChestplate(), leggings = e.getLeggings(), boots = e.getBoots(), mhand = EIGHT ? e.getItemInHand() : e.getItemInMainHand(), ohand = EIGHT ? null : e.getItemInOffHand();
        double difference = -1;
        ItemStack toSet = null;
        switch (value.toLowerCase()) {
            case "mostdamaged":
                for(ItemStack A : e.getArmorContents()) {
                    if(A != null) {
                        final Material m = A.getType();
                        if(!m.equals(Material.AIR)) {
                            final double newdif = getDifference(A);
                            if(difference == -1 || newdif > difference) {
                                difference = newdif;
                                toSet = A;
                            }
                        }
                    }
                }
                return new ItemStack[] {toSet};
            case "helmet": return new ItemStack[] {helmet};
            case "chestplate": return new ItemStack[] {chestplate};
            case "leggings": return new ItemStack[] {leggings};
            case "boots": return new ItemStack[] {boots};
            case "hand":
            case "mainhand": return new ItemStack[] {mhand};
            case "offhand": return new ItemStack[] {ohand};
            case "all": return new ItemStack[] {helmet, chestplate, leggings, boots, mhand, ohand};
            case "item": return new ItemStack[] {item};
            default: return null;
        }
    }
    private double getDifference(ItemStack is) {
        return Double.parseDouble(Short.toString(is.getDurability())) / Double.parseDouble(Short.toString(is.getType().getMaxDurability()));
    }
}
