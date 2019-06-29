package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractPet;

import java.io.File;
import java.util.HashMap;

public class Pet extends AbstractPet {
    public static HashMap<String, Pet> pets;

    public Pet(File f) {
        if(pets == null) pets = new HashMap<>();
        load(f);
        pets.put(getYamlName(), this);
    }

    public static void deleteAll() {
        pets = null;
    }
}
