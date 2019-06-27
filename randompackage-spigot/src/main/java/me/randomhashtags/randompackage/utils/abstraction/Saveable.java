package me.randomhashtags.randompackage.utils.abstraction;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class Saveable {
    protected File file;
    protected YamlConfiguration yml;
    public void load(File file) {
        if(!file.exists()) {
        }
        this.file = file;
        yml = YamlConfiguration.loadConfiguration(file);
    }
    public void save() {
        try {
            yml.save(file);
            file = new File(file.getPath());
            yml = YamlConfiguration.loadConfiguration(file);
        } catch(Exception e) {
            System.out.println(getYamlName());
            e.printStackTrace();
        }
    }
    public File getFile() { return file; }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return file.getName().split("\\.yml")[0]; }
    public void set(String path, Object value) {
        set(path, value, false);
    }
    public void set(String path, Object value, boolean save) {
        yml.set(path, value);
        if(save) save();
    }
}
