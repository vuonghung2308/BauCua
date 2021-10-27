package vn.vm.baucua.socket;

import java.util.ArrayList;

public class ClientPool {
   
    private static ArrayList<Client> clients = null;
    
    private static void init() {
        if(clients == null) {
            clients = new ArrayList<>();
        }
    }

    public static Client getClient(Integer id) {
        init();
        for (Client cs : clients) {
            if (cs.getId().equals(id)) {
                return cs;
            }
        }
        return null;
    }

    public static void addClient(Client client) {
        init();
        clients.add(client);
    }

    public static ArrayList<Client> getClients() {
        init();
        return clients;
    }

    public static void removeClient(Integer id) {
        init();
        Client client = null;
        for (Client cs : clients) {
            if (cs.getId().equals(id)) {
                client = cs;
            }
        }
        if (client != null) {
            clients.remove(client);
        }
    } 
}
