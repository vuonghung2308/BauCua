package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.util.Log;

public class UserDao {

    public User getUser(String username, String password, String email) {
        if (username == null) {
            username = "";
        }
        if (email == null) {
            email = "";
        }
        String query = "SELECT * FROM player "
                + "WHERE (`username` = ? OR `email` = ?) "
                + "AND `password` = SHA2(?,224) limit 1";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        User user = null;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                user = new User();
                user.id = results.getInt("id");
                user.username = results.getString("username");
                user.fullname = results.getString("full_name");
                user.balance = results.getLong("balance");
            }
        } catch (SQLException | NullPointerException e) {
            Log.e(e);
            throw new RuntimeException("Can't access to database");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e(e);
            }
        }
        return user;
    }

    public boolean userExists(String username) {
        String sql = "SELECT * FROM player WHERE `username` = ? limit 1";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean userExists = false;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                userExists = true;
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
        return userExists;
    }

    public boolean insertUser(String username, String password, String fullName) {
        if (!userExists(username)) {
            int rowInserted = 0;
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = null;
            String sql = "INSERT INTO player(`username`, `password`, "
                    + "`full_name`, `balance`) VALUE(?,SHA2(?,224),?,?)";
            try {
                connection = pool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, fullName);
                statement.setLong(4, 200000);
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
        } else {
            return false;
        }
    }

    public void setBalance(int id, long balance) {
        String sql = "UPDATE player SET `balance` = ? "
                + "WHERE `id` = ?";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, balance);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            Log.e(e);
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
}
