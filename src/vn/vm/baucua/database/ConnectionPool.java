package vn.vm.baucua.database;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static ConnectionPool datasource;
    private BoneCP boneCP;

    private ConnectionPool() {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }
        
        BoneCPConfig config = new BoneCPConfig();
        
        config.setJdbcUrl(DatabaseConfig.CONNECTION_URL);
        config.setUsername(DatabaseConfig.USER_NAME);
        config.setPassword(DatabaseConfig.PASSWORD);
        
        config.setMinConnectionsPerPartition(DatabaseConfig.DB_MIN_CONNECTIONS);
        config.setMaxConnectionsPerPartition(DatabaseConfig.DB_MAX_CONNECTIONS);
        config.setPartitionCount(1);
        
        try {
            boneCP = new BoneCP(config);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static ConnectionPool getInstance() {
        if (datasource == null) {
            System.out.println(DatabaseConfig.CONNECTION_URL);
            datasource = new ConnectionPool();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() {
        try {
            return boneCP.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BoneCP getBoneCP() {
        return boneCP;
    }

}
