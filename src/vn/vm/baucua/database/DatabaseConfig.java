package vn.vm.baucua.database;

import vn.vm.baucua.App;

public class DatabaseConfig {

    public static final String SERVER_IP = "40.90.172.165";
    public static final String LOCAL_HOST = "localhost";
    public static final String DB_NAME = "baucua";
    public static final String DB_PORT = "3306";
    public static final String USER_NAME = App.debug ? "admin" : "root";
    public static final String PASSWORD = App.debug ? "admin@1234" : "123456789";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final int DB_MIN_CONNECTIONS = 2;
    public static final int DB_MAX_CONNECTIONS = 5;
    public static final String HOST = App.debug ? SERVER_IP : LOCAL_HOST;
    public static final String CONNECTION_URL = "jdbc:mysql://"
            + HOST + ":" + DB_PORT + "/" + DB_NAME;
}
