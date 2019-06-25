package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.RandomPackage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Updater {

    private static Updater instance;
    public static Updater getUpdater() {
        if(instance == null) instance = new Updater();
        return instance;
    }
    public boolean updateIsAvailable = true;

    public void checkForUpdate() {
        try {
            final URL checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=38501");
            final URLConnection con = checkURL.openConnection();
            final String v = RandomPackage.getPlugin.getDescription().getVersion(), newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            final boolean canUpdate = !v.equals(newVersion);
            updateIsAvailable = canUpdate;
            if(canUpdate) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &eUpdate available! &aYour version: &f" + v + "&a. Latest version: &f" + newVersion));
        } catch(Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &cCould not check for updates due to being unable to connect to SpigotMC!"));
        }
    }
}
