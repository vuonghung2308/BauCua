/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.vm.baucua.data.entity.ForgotKey;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.util.Log;

/**
 *
 * @author User
 */
public class ForgotKeyDao {
    public void insertKey(String username, String key){
            int rowInserted = 0;
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = null;
            String sql = "INSERT INTO forgot_key(`username`, `key`, `time`)"
                    + "VALUE(?,SHA2(?,224),?)";
            try {
                connection = pool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, key);
                statement.setString(3,(""+ System.currentTimeMillis()));
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
        }
    
    public void updateKey(String username, String key){
        String sql = "UPDATE forgot_key SET `key` = SHA2(?,224), `time` = ? "
                + "WHERE `username` = ?";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, key);
            statement.setString(2, (""+ System.currentTimeMillis()));
            statement.setString(3,username);
            int tmp = statement.executeUpdate();
            System.out.println("-------------------------------------------------------so bản gi bi xóa:" + tmp);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e(e);
            }
        }
    }
    public void deleteKey(String username){
        String sql = "DELETE FROM forgot_key WHERE `username` = ? ";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean userExists = false;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e(e);
            }
        }
    }
    public long checkKey(String username, String key){
        String sql = "SELECT * FROM forgot_key WHERE `username` = ? and `key` = SHA2(?,224) limit 1";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean userExists = false;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, key);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return Long.parseLong(res.getString(3));
            }
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
        return -1;
    }
    
    public boolean checkUser(String username){
        String sql = "SELECT * FROM forgot_key WHERE `username` = ? limit 1";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean userExists = false;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return true;
            }
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
        return false;
    }
    
}
