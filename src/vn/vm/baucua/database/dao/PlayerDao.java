package vn.vm.baucua.database.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.socket.ThreadSocket;

public class PlayerDao  {

    public Player getPlayer(String username, String password) {
        try {
            String query = "select * from player where username = ? and `password` = sha2(?,224) ";
//            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                Player player = new Player();
                player.setId(results.getInt("id"));
                player.setUsername(results.getString("username"));
                player.setBalance(results.getLong("balance"));
                player.setFullname(results.getString("full_name"));

                results.close();
                statement.close();
                connection.close();
                return player;
            }
            return null;

        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException("Can't access to database");
        }
    }

    public boolean insertPlayer(String username, String password, String fullName){
            try {
                String sql = "INSERT INTO player(`username`, `password`, `full_name`, `balance`)"
                        + "VALUE(?,sha2(?,224),?,?)";
                Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, username);
                pre.setString(2, password);
                pre.setString(3, fullName);
                pre.setInt(4, 0);
                System.out.println(pre.toString());
                int rowInserted =  pre.executeUpdate();
                conn.commit();
                conn.close();
                if(rowInserted > 0){
                    return true;
                }
                return false;
            } catch (SQLException ex) {
                return false;
            }
    }
    // update
}
