package me.randomhashtags.randompackage.utils.database;

import me.randomhashtags.randompackage.utils.database.file.DatabaseFile;
import me.randomhashtags.randompackage.utils.database.file.JSONFile;
import me.randomhashtags.randompackage.utils.database.file.MySQLFile;
import me.randomhashtags.randompackage.utils.database.file.YamlFile;

import java.io.File;

public class Database {
    private static DatabaseType type;
    public static void setDatabase(DatabaseType type) { Database.type = type; }
    public static DatabaseType getType() { return type; }

    public DatabaseFile getFile(String folder, String file) { return getFile(folder, file, null, -1, null, null, null); }
    public DatabaseFile getFile(String folder, String file, String host, int port, String database, String username, String password) {
        switch (type) {
            case YAML: return getYaml(folder, file);
            case JSON: return getJson(folder, file);
            case MYSQL: return getMySQL(folder, file, host, port, database, username, password);
            default: return null;
        }
    }

    public YamlFile getYaml(String folder, String file) {
        final File f = new File(folder, file);
        return f.exists() ? new YamlFile(f) : null;
    }
    public JSONFile getJson(String folder, String file) {
        final File f = new File(folder, file);
        return f.exists() ? new JSONFile(f) : null;
    }
    public MySQLFile getMySQL(String folder, String file, String host, int port, String database, String username, String password) {
        if(host != null && username != null && password != null) {
            final File f = new File(folder, file);
            return f.exists() ? new MySQLFile(host, port, database, username, password) : null;
        } else {
            return null;
        }
    }
}
