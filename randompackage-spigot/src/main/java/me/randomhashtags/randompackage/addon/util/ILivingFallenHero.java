package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.FallenHero;
import org.bukkit.Location;

import java.util.UUID;

public interface ILivingFallenHero {
    CustomKit getKit();
    FallenHero getFallenHero();
    UUID getSummoner();
    Location getSpawnedLocation();
}
