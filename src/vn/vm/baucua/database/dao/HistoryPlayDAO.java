/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.util.Log;

/**
 *
 * @author dinhv
 */
public class HistoryPlayDAO {
    public boolean insertHistory(int playerID, int status, long different,String time ) {
       
        int rowInserted = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        String sql = "INSERT INTO history_play(`player_id`, `status`, `time`, `different`)"
                + "VALUE(?,SHA2(?,224),?,?)";
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, playerID);
            statement.setInt(2, status);
            statement.setLong(3, different);
            statement.setString(4, time);
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
