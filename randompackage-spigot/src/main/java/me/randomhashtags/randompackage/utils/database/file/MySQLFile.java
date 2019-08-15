package me.randomhashtags.randompackage.utils.database.file;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class MySQLFile implements DatabaseFile {
    private String host, database;
    private int port;
    private Connection connection;
    public MySQLFile(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        try {
            final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(url, username, password);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public void disconnect() {
        try {
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Object g(String query) {
        try {
            final PreparedStatement p = connection.prepareStatement(query);
            final ResultSet r = p.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Object get(String query) { return g(query); }
    public String getString(String query) {
        final Object o = g(query);
        return o != null ? (String) o : null;
    }
    public boolean getBoolean(String query) {
        final Object o = g(query);
        return o != null && (boolean) o;
    }
    public int getInt(String query) {
        final Object o = g(query);
        return o != null ? (int) o : -1;
    }
    public double getDouble(String query) {
        final Object o = g(query);
        return o != null ? (double) o : -1;
    }
    public long getLong(String query) {
        final Object o = g(query);
        return o != null ? (long) o : -1;
    }
    public List<String> getStringList(String query) {
        final Object o = g(query);
        return o != null ? (List<String>) o : null;
    }

    public void save() {}

    public void set(String query, Object value) { }
}
