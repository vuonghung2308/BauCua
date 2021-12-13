package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.vm.baucua.data.entity.PersonalInfo;
import java.util.ArrayList;
import java.util.List;
import vn.vm.baucua.data.entity.RankRow;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.util.Log;

public class UserDao {

    public User getUser(String username, String password) {
        String query = "SELECT * FROM player "
                + "WHERE `username` = ? AND `password` = SHA2(?,224) limit 1";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        User user = null;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                user = new User();
                user.id = results.getInt("id");
                user.username = results.getString("username");
                user.fullname = results.getString("full_name");
                user.balance = results.getLong("balance");
                user.email = results.getString("email");
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

    public boolean insertUser(String username, String password, String fullName, String email) {
        if (!userExists(username)) {
            int rowInserted = 0;
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = null;
            String sql = "INSERT INTO player(`username`, `password`, `full_name`, `balance`, `email`)"
                    + "VALUE(?,SHA2(?,224),?,?,?)";
            try {
                connection = pool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, fullName);
                statement.setLong(4, 200000);
                statement.setString(5, email);
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
    public PersonalInfo getPersonalinfo(int id){
        System.out.println(id);
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        PersonalInfo p = new PersonalInfo();
        try {
            String sql = "SELECT count(id) FROM baucua.history where `player_id` = ?";
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();
            
            if (res.next()) {
                p.total = res.getLong("count(id)");
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
        try {
            String sql = "SELECT count(id) FROM baucua.history "
                + "WHERE `status` = ? AND `player_id` = ?";
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, 0);
            statement.setInt(2, id);
            ResultSet res = statement.executeQuery();
            
            if (res.next()) {
                p.lose_number = res.getLong("count(id)");
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
        try {
            String sql = "SELECT count(id) FROM baucua.history "
                + "WHERE `status` = ? AND `player_id` = ?";
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, 1);
            statement.setInt(2, id);
            ResultSet res = statement.executeQuery();
            
            if (res.next()) {
                p.win_number = res.getLong("count(id)");
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
        try {
            String sql = "SELECT * FROM baucua.player "
                + "WHERE `id` = ?";
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();
            
            if (res.next()) {
                p.email = res.getString("email");
                p.balance = res.getLong("balance");
                p.fullname = res.getString("full_name");
                p.username = res.getString("username");
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
        return p;
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
    
    public String getEmail(String username) {
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
                return res.getString("email");
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
        return null;
    }
    
    public void setPassword(String username, String newPassword) { // update ThanhND
        String sql = "UPDATE player SET `password` = SHA2(?,224) "
                + "WHERE `username` = ?";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newPassword);
            statement.setString(2, username);
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
    
    public List<RankRow> getRank(){ // update ThanhND
        String query = "SELECT `player`.`id`, `username`, `balance`, COUNT(`history`.`id`) AS `total`, SUM(if(`history`.`status` = 1, 1,0)) AS `win` " +
"                        FROM `player` " +
"                        LEFT JOIN `history` " +
"                        ON `player`.`id` = `history`.`player_id` " +
"                        GROUP BY `player`.`id` " +
"                        ORDER BY `balance` DESC " +
"                        LIMIT 10;";
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        List<RankRow> listRank = new ArrayList<>();
        try {
            connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                RankRow rankRow = new RankRow();
                rankRow.id = results.getInt("id");
                rankRow.username = results.getString("username");
                rankRow.balance = results.getLong("balance");
                rankRow.total = results.getInt("total");
                rankRow.win_number = results.getInt("win");
                listRank.add(rankRow);
                System.out.println("add row");
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
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
        return listRank;
    }
}
