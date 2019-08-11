package me.randomhashtags.randompackage.utils.database;

import me.randomhashtags.randompackage.utils.database.files.YamlFile;

import java.io.File;

public class DefaultDatabase extends Database {
    public YamlFile getFile(String folder, String file) {
        final File f = new File(folder, file);
        return f.exists() ? new YamlFile(f) : null;
    }
}
