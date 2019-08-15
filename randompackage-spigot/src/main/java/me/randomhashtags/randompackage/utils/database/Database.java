package me.randomhashtags.randompackage.utils.database;

import me.randomhashtags.randompackage.utils.database.file.DatabaseFile;

public abstract class Database {
    private static Database database;
    public static void setDatabase(Database base) { database = base; }
    public static Database getDatabase() { return database; }

    public abstract DatabaseFile getFile(String folder, String file);
}
