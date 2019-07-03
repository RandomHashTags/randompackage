package me.randomhashtags.randompackage.utils.interfaces;

import me.randomhashtags.randompackage.utils.classes.kits.FallenHero;
import org.bukkit.Location;

import java.util.UUID;

public interface ILivingFallenHero {
    AbstractCustomKit getKit();
    FallenHero getFallenHero();
    UUID getSummoner();
    Location getSpawnedLocation();
}
