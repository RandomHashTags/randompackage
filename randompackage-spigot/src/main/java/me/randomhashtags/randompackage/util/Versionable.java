package me.randomhashtags.randompackage.util;

import org.bukkit.Bukkit;

public interface Versionable {
    boolean EIGHT = Bukkit.getVersion().contains("1.8");
    boolean NINE = Bukkit.getVersion().contains("1.9");
    boolean TEN = Bukkit.getVersion().contains("1.10");
    boolean ELEVEN = Bukkit.getVersion().contains("1.11");
    boolean TWELVE = Bukkit.getVersion().contains("1.12");
    boolean THIRTEEN = Bukkit.getVersion().contains("1.13");
    boolean FOURTEEN = Bukkit.getVersion().contains("1.14");
    boolean FIFTEEN = Bukkit.getVersion().contains("1.15");
    boolean SIXTEEN = Bukkit.getVersion().contains("1.16");
    boolean SEVENTEEN = Bukkit.getVersion().contains("1.17");
    boolean EIGHTEEN = Bukkit.getVersion().contains("1.18");
    boolean NINETEEN = Bukkit.getVersion().contains("1.19");
    boolean TWENTY = Bukkit.getVersion().contains("1.20");
    boolean LEGACY = EIGHT || NINE || TEN || ELEVEN || TWELVE;
}
