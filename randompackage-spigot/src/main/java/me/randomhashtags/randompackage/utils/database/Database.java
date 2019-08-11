package me.randomhashtags.randompackage.utils.database;

import me.randomhashtags.randompackage.utils.database.files.YamlFile;

public abstract class Database {
    private static Database database;
    protected void setDatabase(Database base) { database = base; }
    public Database getDatabase() { return database; }

    public abstract YamlFile getFile(String folder, String file);
}
