package me.randomhashtags.randompackage.addons.interfaces;

import org.bukkit.Location;

import java.util.UUID;

public interface ILivingFallenHero {
    AbstractCustomKit getKit();
    FallenHero getFallenHero();
    UUID getSummoner();
    Location getSpawnedLocation();
}
