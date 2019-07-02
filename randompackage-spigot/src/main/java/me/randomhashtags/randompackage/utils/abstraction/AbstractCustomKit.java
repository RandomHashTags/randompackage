package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.classes.kits.KitItem;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractCustomKit extends AbstractRPFeature {
    public static HashMap<String, AbstractCustomKit> kits;

    public void created(String identifier) {
        if(kits == null) kits = new HashMap<>();
        kits.put(identifier, this);
    }

    public abstract int getSlot();
    public abstract int getMaxLevel();
    public abstract long getCooldown();
    public abstract AbstractFallenHero getFallenHero();
    public abstract List<KitItem> getItems();
}
