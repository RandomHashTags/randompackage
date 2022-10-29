package me.randomhashtags.randompackage.util;

import org.bukkit.Bukkit;

public interface Versionable {
    String VERSION = Bukkit.getVersion();
    boolean EIGHT = VERSION.contains("1.8");
    boolean NINE = VERSION.contains("1.9");
    boolean TEN = VERSION.contains("1.10");
    boolean ELEVEN = VERSION.contains("1.11");
    boolean TWELVE = VERSION.contains("1.12");
    boolean THIRTEEN = VERSION.contains("1.13");
    boolean FOURTEEN = VERSION.contains("1.14");
    boolean FIFTEEN = VERSION.contains("1.15");
    boolean SIXTEEN = VERSION.contains("1.16");
    boolean SEVENTEEN = VERSION.contains("1.17");
    boolean EIGHTEEN = VERSION.contains("1.18");
    boolean NINETEEN = VERSION.contains("1.19");
    boolean LEGACY = EIGHT || NINE || TEN || ELEVEN || TWELVE;
}
