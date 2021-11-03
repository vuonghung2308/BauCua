package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.util.Log;

public class UserrDao {

    public User getUser(String username, String password) {
        try {
            String query = "SELECT * FROM player WHERE `username` = ? AND `password` = SHA2(?,224) ";
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                User user = new User();
                user.id = results.getInt("id");
                user.username = results.getString("username");
                user.fullname = results.getString("full_name");
                user.balance = results.getLong("balance");

                results.close();
                statement.close();
                connection.close();
                return user;
            }
            return null;

        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException("Can't access to database");
        }
    }

    public boolean insertUser(String username, String password, String fullName) {
        try {
            String sql = "INSERT INTO player(`username`, `password`, `full_name`, `balance`)"
                    + "VALUE(?,sha2(?,224),?,?)";
            ConnectionPool pool = ConnectionPool.getInstance();
            int rowInserted;
            try (Connection connection = pool.getConnection()) {
                PreparedStatement pre = connection.prepareStatement(sql);
                pre.setString(1, username);
                pre.setString(2, password);
                pre.setString(3, fullName);
                pre.setInt(4, 0);
                rowInserted = pre.executeUpdate();
                connection.commit();
            }
            return rowInserted > 0;
        } catch (SQLException ex) {
            Log.e(ex);
            return false;
        }
    }

    public void setBalance(int id, long balance) {
        try {
            String sql = "UPDATE player SET `balance` = ? "
                    + "WHERE `id` = ?";
            ConnectionPool pool = ConnectionPool.getInstance();
            try (Connection connection = pool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setLong(1, balance);
                statement.setLong(2, id);
                statement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            Log.e(e);
        }
    }
}
