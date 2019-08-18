package me.randomhashtags.randompackage.addons.utils;

import me.randomhashtags.randompackage.addons.legacy.CustomKit;
import me.randomhashtags.randompackage.addons.FallenHero;
import org.bukkit.Location;

import java.util.UUID;

public interface ILivingFallenHero {
    CustomKit getKit();
    FallenHero getFallenHero();
    UUID getSummoner();
    Location getSpawnedLocation();
}
