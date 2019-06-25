package me.randomhashtags.randompackage.utils.classes;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class Pet {
    public static HashMap<String, Pet> pets;

    private YamlConfiguration yml;
    private String ymlName;
    public Pet(File f) {
        if(pets == null) {
            pets = new HashMap<>();
        }
        yml = YamlConfiguration.loadConfiguration(f);
        ymlName = f.getName();
        pets.put(ymlName, this);
    }

    public static void deleteAll() {
        pets = null;
    }
}
