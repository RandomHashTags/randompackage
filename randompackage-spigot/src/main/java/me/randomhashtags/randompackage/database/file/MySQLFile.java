package me.randomhashtags.randompackage.database.file;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class MySQLFile /*implements DatabaseFile*/ { // TODO: fix dis
    private final String host;
    private final String database;
    private final int port;
    private Connection connection;
    public MySQLFile(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;

        try {
            connect(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }

    public boolean isOpen() throws Exception {
        return connection != null && !connection.isClosed();
    }
    public void connect(String username, String password) throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("[RandomPackage] MySQL jdbc Driver not installed!");
            return;
        }
        if(isOpen()) {
            synchronized (this) {
                if(isOpen()) {
                    try {
                        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
                        connection = DriverManager.getConnection(url, username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void disconnect() {
        try {
            if(isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResultSet g(String query) {
        try {
            final PreparedStatement p = connection.prepareStatement(query);
            return p.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Object get(String query, String path) {
        try {
            final ResultSet result = g(query);
            return result != null ? result.getObject(path) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getString(String query, String path) {
        final Object o = get(query, path);
        return o != null ? (String) o : null;
    }
    public boolean getBoolean(String query, String path) {
        final Object o = get(query, path);
        return o != null && (boolean) o;
    }
    public int getInt(String query, String path) {
        final Object o = get(query, path);
        return o != null ? (int) o : 0;
    }
    public double getDouble(String query, String path) {
        final Object o = get(query, path);
        return o != null ? (double) o : 0;
    }
    public long getLong(String query, String path) {
        final Object o = get(query, path);
        return o != null ? (long) o : 0;
    }
    public List<String> getStringList(String query, String path) {
        final Object o = get(query, path);
        return o != null ? (List<String>) o : null;
    }
    public BigDecimal getBigDecimal(String query, String path) {
        final Object o = get(query, path);
        return o != null ? BigDecimal.valueOf((double) o) : BigDecimal.ZERO;
    }

    public void save() {}

    public void set(String query, String path, String values) {
        try {
            final PreparedStatement p = connection.prepareStatement(query);
            p.executeUpdate("INSERT INTO " + path + " VALUES " + values + ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void convertTo(Class<? extends DatabaseFile> file) {
        if(file.isInstance(YamlFile.class)) {
        } else if(file.isInstance(JSONFile.class)) {
        }
    }
}
