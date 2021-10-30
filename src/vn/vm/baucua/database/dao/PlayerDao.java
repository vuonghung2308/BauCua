package vn.vm.baucua.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.database.ConnectionPool;

public class PlayerDao extends Dao<Player> {

    public Player getPlayer(String username, String password) {
        try {
            String query = "select * from player where `username` = ? and `password` = sha2(?, 224) ";
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = pool.getConnection();
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

}
