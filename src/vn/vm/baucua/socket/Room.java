package vn.vm.baucua.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.vm.baucua.data.entity.Bat;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.entity.RoomInfo;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.response.RoomDetailResponse;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.data.response.TimeResponse;
import vn.vm.baucua.game.Callback;
import vn.vm.baucua.game.Game;

public class Room {

    private final HashMap<Integer, Client> clients;

    static Integer count = 0;
    private final Integer id;
    private final String name;
    private final Callback cb;
    private final Game game;
    private int hostId;

    public Room() {
        id = count++;
        name = Integer.toString((1000 + id));
        clients = new HashMap<>();
        cb = (int seconds) -> {
            sendTime(seconds);
            if (seconds == 10) {
                sendGameResult();
            }
        };
        game = new Game(cb);
    }

    private void sendTime(int seconds) {
        Response response = TimeResponse.get(seconds);
        sendToAll(response, -1);
    }

    public Integer getId() {
        return id;
    }

    public void sendToAll(Response response, int ignore) {
        clients.values().forEach((client) -> {
            if (client.getId() != ignore) {
                client.send(response);
            }
        });
    }

    public RoomDetailResponse getRoomDetail() {
        RoomDetailResponse detail = new RoomDetailResponse();
        detail.players = game.getPlayers();
        detail.quantity = clients.size();
        detail.host_id = hostId;
        detail.name = name;
        detail.id = id;
        return detail;
    }

    public List<Player> getUsers() {
        return game.getPlayers();
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients.values());
    }

    public RoomInfo getRoomInfo() {
        return new RoomInfo(
                id, name, clients.size()
        );
    }

    public void addClient(Client client) {
        User user = client.getUser();
        if (clients.isEmpty()) {
            hostId = client.getId();
            game.addPlayer(user, true);
        } else {
            game.addPlayer(user);
        }
        clients.put(client.getId(), client);
    }

    public void remove(int playerId) {
        clients.remove(playerId);
        sendRoomDetail(-1);
    }

    public int numberClient() {
        return clients.size();
    }

    public void sendOneClient(int id, Response response) {
        Client clientReceive = clients.get(id);
        clientReceive.send(response);
    }

    private void sendGameResult() {
        Response response = game.getResultResponse();
        sendToAll(response, -1);
    }

    public void sendRoomDetail(int ignore) {
        Response playersResponse = Response.success(
                "room_detail", getRoomDetail());
        sendToAll(playersResponse, ignore);
    }

    public boolean isHost(int id) {
        return id == hostId;
    }

    public boolean setReady(int id) {
        boolean setOke = game.setReady(id);
        if (setOke) {
            sendRoomDetail(id);
            return true;
        }
        return false;
    }

    public boolean isGameStarted() {
        return game.isStarted;
    }

    public boolean setBat(int id, Bat bat) {
        if (game.setBat(id, bat)) {
            sendRoomDetail(id);
            return true;
        }
        return false;
    }

    public boolean canPlay() {
        return game.canPlay();
    }

    public void playGame() {
        game.start();
    }
}
