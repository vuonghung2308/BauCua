package vn.vm.baucua.socket;

import java.net.Socket;
import vn.vm.baucua.data.entity.Player;

public class Client {

    private Player player;
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Client(Player player, Socket socket) {
        this.player = player;
        this.socket = socket;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getId() {
        return player.getId();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
