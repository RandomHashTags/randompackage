package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.addon.EventCondition;
import me.randomhashtags.randompackage.attribute.*;
import me.randomhashtags.randompackage.attribute.condition.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.*;

public abstract class EventAttributes extends EventExecutor {
    /*
        Read https://gitlab.com/RandomHashTags/randompackage-multi/wikis/Event-Attributes for all event attribute info
            * Event specific entity placeholders
            * Allowed conditions for specific entity types
            * Available event attributes with their identifier, and what they do
     */
    static {
        if(eventattributes == null) {
            eventattributes = new LinkedHashMap<>();
        }
        final List<EventAttribute> attributes = Arrays.asList(
                // event attributes
                new BreakHitBlock(),
                new GiveDrops(),
                new SetDamage(),
                new SetDroppedExp(),
                // attributes
                new AddPotionEffect(),
                new AddToList(),
                new BreakBlocks(),
                new ComboAdd(),
                new ComboDeplete(),
                new ComboStop(),
                new Damage(),
                new DropItem(),
                new ExecuteCommand(),
                //new Explode(),
                new Freeze(),
                new GiveItem(),
                new Heal(),
                new Ignite(),
                new KickWithReason(),
                new PerformCommand(),
                new PlaySound(),
                new Remove(),
                new RemoveFromList(),
                new RemovePotionEffect(),
                new Repeat(),
                new Return(),
                new SendMessage(),
                new SendTitle(),
                new SetAir(),
                new SetAllowed(),
                new SetBlock(),
                new SetCancelled(),
                new SetCombo(),
                new SetCompassTarget(),
                new SetDurability(),
                new SetFlySpeed(),
                new SetGameMode(),
                new SetHealth(),
                new SetHunger(),
                new SetLevelupChance(),
                new SetMultiplier(),
                new SetNoDamageTicks(),
                new SetSneaking(),
                new SetSprinting(),
                new SetWalkSpeed(),
                new SetXp(),
                new Smite(),
                new StealXp(),
                new Teleport(),
                new Wait()
        );
        for(EventAttribute e : attributes) {
            e.load();
        }
        final List<EventCondition> conditions = Arrays.asList(
                new DepleteRarityGem(),
                new HasCombo(),
                new HasCustomEnchantEquipped(),
                new HitBlock(),
                new HitCEEntity(),
                new IsHeadshot(),
                new IsInList()
        );
        for(EventCondition c : conditions) {
            c.load();
        }
    }

    public static void unloadEventAttributes() {
        Combo.combos.clear();
        Listable.list.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            final UUID u = event.getEntity().getUniqueId();
            if(!spawnedFromSpawner.contains(u)) {
                spawnedFromSpawner.add(u);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        spawnedFromSpawner.remove(event.getEntity().getUniqueId());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        projectileEvents.put(event.getProjectile().getUniqueId(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void projectileHitEvent(ProjectileHitEvent event) {
        projectileEvents.remove(event.getEntity().getUniqueId());
    }
}
