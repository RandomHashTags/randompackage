package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.attribute.*;
import me.randomhashtags.randompackage.attribute.condition.*;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.dev.RPStorage;

import java.util.Arrays;
import java.util.List;

public abstract class EventAttributes extends EventExecutor {
    /*
        Read https://gitlab.com/RandomHashTags/randompackage-multi/wikis/Event-Attributes for all event attribute info
            * Event specific entity placeholders
            * Allowed conditions for specific entity types
            * Available event attributes with their identifier, and what they do
     */

    public static void loadEventAttributes() {
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
                new LaunchProj(),
                new MultiplierDeplete(),
                new PerformCommand(),
                new PlaySound(),
                new Remove(),
                new RemoveFromList(),
                new RemoveItem(),
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
                new SetDelay(),
                new SetDurability(),
                new SetFlySpeed(),
                new SetGameMode(),
                new SetHealth(),
                new SetHunger(),
                new SetMultiplier(),
                new SetNoDamageTicks(),
                new SetRewardSize(),
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
                new DoesDieFromDamage(),
                new HasCombo(),
                new HasCustomEnchantEquipped(),
                new HitBlock(),
                new HitCEEntity(),
                new IsHeadshot(),
                new IsInList(),
                new IsRelation()
        );
        for(EventCondition c : conditions) {
            c.load();
        }
        EACoreListener.getEACoreListener().enable();
    }
    public static void unloadEventAttributes() {
        RPStorage.unregisterAll(Feature.EVENT_ATTRIBUTE, Feature.EVENT_CONDITION);
        Combo.combos.clear();
        Listable.list.clear();
        EACoreListener.getEACoreListener().disable();
    }
}
