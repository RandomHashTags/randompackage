package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;

import java.util.HashMap;

public interface TemporaryBlocks {
    HashMap<Location, UMaterial> tempblocks = new HashMap<>();
}
