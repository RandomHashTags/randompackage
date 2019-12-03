package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Captureable;
import me.randomhashtags.randompackage.addon.util.Scoreboardable;
import me.randomhashtags.randompackage.addon.util.Slotable;
import me.randomhashtags.randompackage.dev.ActiveStronghold;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;

import java.util.List;

public interface Stronghold extends Captureable, Slotable, Scoreboardable {
    PolyBoundary getZone();
    int getBlockDurability();
    ActiveStronghold getActiveStronghold();
    List<String> getContestedMsg();
}
