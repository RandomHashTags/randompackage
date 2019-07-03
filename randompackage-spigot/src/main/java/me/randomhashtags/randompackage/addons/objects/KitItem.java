package me.randomhashtags.randompackage.addons.objects;

import java.util.List;

public class KitItem {

    public final Object kit;
    public final String path;
    public int chance, reqLevel;
    public String stringItem, stringName, stringChances;
    public final List<String> lore;

    public String amount;
    public boolean canRepeat = false;
    public KitItem(Object kit, String path, String stringItem, String stringName, List<String> lore, int chance) {
        this.kit = kit;
        this.path = path;
        this.stringItem = stringItem;
        this.stringName = stringName;
        this.lore = lore;
        this.chance = chance;
        this.amount = "1";
    }

    public KitItem(Object kit, String path, String stringItem, String stringName, List<String> lore, int chance, String amount) {
        this.kit = kit;
        this.path = path;
        this.stringItem = stringItem;
        this.stringName = stringName;
        this.lore = lore;
        this.chance = chance;
        this.amount = amount;
    }
    public KitItem(Object kit, String path, String stringItem, String stringName, List<String> lore, String stringChances, String amount, boolean canRepeat) {
        this.kit = kit;
        this.path = path;
        this.stringItem = stringItem;
        this.stringName = stringName;
        this.lore = lore;
        this.stringChances = stringChances;
        this.amount = amount;
        this.canRepeat = canRepeat;
    }

    public KitItem(Object kit, String path, String stringItem, String stringName, List<String> lore, int chance, String amount, boolean canRepeat, int reqLevel) {
        this.kit = kit;
        this.path = path;
        this.stringItem = stringItem;
        this.stringName = stringName;
        this.lore = lore;
        this.chance = chance;
        this.amount = amount;
        this.canRepeat = canRepeat;
        this.reqLevel = reqLevel;
    }
}
