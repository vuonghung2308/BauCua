package vn.vm.baucua.database;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.sql.Connection;
import java.sql.SQLException;
import vn.vm.baucua.util.Log;

public class ConnectionPool {

    private static ConnectionPool datasource;
    private BoneCP boneCP;

    private ConnectionPool() {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
        } catch (ClassNotFoundException e) {
            Log.e(e);
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
            Log.e(e);
        }
    }

    public static ConnectionPool getInstance() {
        if (datasource == null) {
            datasource = new ConnectionPool();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() {
        try {
            Connection con= boneCP.getConnection();
            con.setAutoCommit(true);
            return con;
        } catch (SQLException e) {
            Log.e(e);
            return null;
        }
    }

    public BoneCP getBoneCP() {
        return boneCP;
    }

}
