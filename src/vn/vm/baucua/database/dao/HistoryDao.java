package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.util.Log;

public class HistoryDao {

    public boolean insertHistory(int id, int status, long difference, Date time) {

        int rowInserted = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Connection connection = null;
        String sql = "INSERT INTO history(`player_id`, `status`, `time`, `difference`)"
                + "VALUE(?,?,?,?)";
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setInt(2, status);
            statement.setString(3, format.format(time));
            statement.setLong(4, difference);
            rowInserted = statement.executeUpdate();

        } catch (SQLException ex) {
            Log.e(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e(e);
            }
        }
        return rowInserted > 0;

    }
}
