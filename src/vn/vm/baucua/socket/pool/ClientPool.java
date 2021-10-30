package vn.vm.baucua.socket.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.socket.Client;

public class ClientPool {

    private static ClientPool pool;
    private HashMap<Integer, Client> clients = null;

    private ClientPool() {
        clients = new HashMap<>();
    }

    public static ClientPool getInstance() {
        if (pool == null) {
            pool = new ClientPool();
        }
        return pool;
    }

    public List<Player> getPlayers(List<Integer> ids) {
        ArrayList<Player> players = new ArrayList<>();
        ids.forEach((id) -> {
            players.add(getClient(id).getPlayer());
        });
        return players;
    }

    public List<Client> getClients(List<Integer> ids) {
        ArrayList<Client> list = new ArrayList<>();
        ids.forEach((id) -> {
            list.add(getClient(id));
        });
        return list;
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients.values());
    }

    public Client getClient(Integer id) {
        return clients.get(id);
    }

    public void addClient(Client client) {
        clients.put(client.getId(), client);
    }

    public void removeClient(int id) {
        clients.remove(id);
    }
}
