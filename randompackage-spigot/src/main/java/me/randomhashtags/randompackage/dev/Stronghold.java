package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Captureable;
import me.randomhashtags.randompackage.addon.util.Scoreboardable;
import me.randomhashtags.randompackage.addon.util.Slotable;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;

import java.util.List;

public interface Stronghold extends Captureable, Slotable, Scoreboardable {
    PolyBoundary getZone();
    ActiveStronghold getActiveStronghold();
    List<String> getContestedMsg();
}
