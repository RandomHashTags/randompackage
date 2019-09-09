package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.EventAttribute;
import me.randomhashtags.randompackage.addons.EventCondition;
import me.randomhashtags.randompackage.attributes.*;
import me.randomhashtags.randompackage.attributes.conditions.*;
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
                new SetDamage(),
                // attributes
                new AddPotionEffect(),
                new AddToList(),
                new BreakHitBlock(),
                new ComboAdd(),
                new ComboDeplete(),
                new ComboStop(),
                new Damage(),
                new DepleteRarityGem(),
                new DropItem(),
                new ExecuteCommand(),
                //new Explode(),
                new Freeze(),
                new GiveDrops(),
                new GiveItem(),
                new Heal(),
                new Ignite(),
                new KickWithReason(),
                new PerformCommand(),
                new PlaySound(),
                new RemovePotionEffect(),
                new SendMessage(),
                new SetAir(),
                new SetCancelled(),
                new SetCompassTarget(),
                new SetDroppedExp(),
                new SetDurability(),
                new SetFlySpeed(),
                new SetGameMode(),
                new SetHealth(),
                new SetHunger(),
                new SetNoDamageTicks(),
                new SetSneaking(),
                new SetSprinting(),
                new SetWalkSpeed(),
                new SetXp(),
                new Smite(),
                new StealXp(),
                new Wait()
        );
        for(EventAttribute e : attributes) {
            e.load();
        }
        final List<EventCondition> conditions = Arrays.asList(
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
