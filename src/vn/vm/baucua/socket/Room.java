package vn.vm.baucua.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.entity.RoomInfo;
import vn.vm.baucua.data.response.Response;

public class Room {

    private final HashMap<Integer, Client> clients;

    static Integer count = 0;
    private String name;
    private Integer id;

    public Room() {
        id = count++;
        name = "" + (1000 + id);
        clients = new HashMap<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sendToAll(Response response, int ignore) throws IOException {
        for (Client client : clients.values()) {
            if (client.getId() != ignore) {
                client.send(response);
            }
        }
    }

    public void sendToAll( // send result baucua
            Response response
    ) throws IOException {
        for (Client client : clients.values()) {
            client.send(response);
        }
    }

    public List<Player> getPlayes() {
        ArrayList<Player> players = new ArrayList<>();
        getClients().forEach(client -> {
            players.add(client.getPlayer());
        });
        return players;
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
        clients.put(client.getId(), client);
    }

    public void remove(int playerId) {
        clients.remove(playerId);
    }

    public int numberClient() {
        return clients.size();
    }
    
    public void sendOneClient(int id, Response response){
        try {
            Client clientReceive = clients.get(id);
            clientReceive.send(response);
        } catch (IOException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
