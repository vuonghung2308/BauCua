package vn.vm.baucua.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.vm.baucua.data.entity.Bet;
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
        name = Integer.toString((100 + id));
        clients = new HashMap<>();
        cb = (int seconds) -> {
            sendTime(seconds);
            if (seconds == 0) {
                sendGameResult();
                updateClient();
            }
        };
        game = new Game(cb);
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

    public RoomDetailResponse getRoomDetail() {
        RoomDetailResponse detail = new RoomDetailResponse();
        detail.players = game.getPlayers();
        detail.quantity = clients.size();
        detail.host_id = hostId;
        detail.name = name;
        detail.id = id;
        return detail;
    }

    public void sendToAll(Response response, int ignore) {
        clients.values().forEach((client) -> {
            if (client.getId() != ignore) {
                client.send(response);
            }
        });
    }

    private void sendTime(int seconds) {
        Response response = TimeResponse.get(seconds);
        sendToAll(response, -1);
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

    public boolean setReady(int id) {
        boolean setOke = game.setReady(id);
        if (setOke) {
            sendRoomDetail(id);
            return true;
        }
        return false;
    }

    private void updateClient() {
        game.getPlayers().forEach(player -> {
            Client client = clients.get(player.id);
            User user = client.getUser();
            user.balance = player.balance;

        });
    }

    public boolean setBat(int id, Bet bat) {
        if (game.setBat(id, bat)) {
            sendRoomDetail(id);
            return true;
        }
        return false;
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients.values());
    }

    public RoomInfo getRoomInfo() {
        return new RoomInfo(
                id, name, clients.size()
        );
    }

    public void remove(int id) {
        clients.remove(id);
        game.remove(id);
        sendRoomDetail(-1);
    }

    public List<Player> getUsers() {
        return game.getPlayers();
    }

    public boolean isHost(int id) {
        return id == hostId;
    }

    public boolean isGameStarted() {
        return game.isStarted;
    }

    public boolean canPlay() {
        return game.canPlay(hostId);
    }

    public int numberClient() {
        return clients.size();
    }

    public void playGame() {
        game.start();
    }

    public Integer getId() {
        return id;
    }
}
