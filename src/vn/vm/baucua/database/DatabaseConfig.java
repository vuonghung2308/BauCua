package vn.vm.baucua.database;

import vn.vm.baucua.App;

public class DatabaseConfig {

    public static final String SERVER_IP = "40.90.172.165";
    public static final String LOCAL_HOST = "127.0.0.1";   
    public static final String DB_NAME = "baucua";
    public static final String DB_PORT = "3306";
    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "admin@1234";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final int DB_MIN_CONNECTIONS = 2;
    public static final int DB_MAX_CONNECTIONS = 5;
    public static final String HOST = App.debug ? SERVER_IP : LOCAL_HOST;
    public static final String CONNECTION_URL = "jdbc:mysql://"
            + HOST + ":" + DB_PORT + "/" + DB_NAME;
}
