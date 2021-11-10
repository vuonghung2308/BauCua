package vn.vm.baucua.socket.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.vm.baucua.data.entity.RoomInfo;
import vn.vm.baucua.socket.Client;
import vn.vm.baucua.socket.Room;

public class RoomPool {

    private static RoomPool pool;
    private final HashMap<Integer, Room> rooms;

    private RoomPool() {
        rooms = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            Room room = new Room();
            rooms.put(room.getId(), room);
        }
    }

    public Room goRoom(int roomId, Client client) {
        Room room = rooms.get(roomId);
        if (room != null) {
            if (room.numberClient() < 5) {
                room.addClient(client);
                return room;
            }
            return null;
        } else {
            return null;
        }
    }

    public void outRoom(int roomId, int playerId) {
        Room room = rooms.get(roomId);
        room.remove(playerId);
    }

    public List<RoomInfo> getRoomInfos() {
        List<Room> list = new ArrayList<>(rooms.values());
        List<RoomInfo> roomInfos = new ArrayList<>();
        list.forEach(room -> {
            roomInfos.add(room.getRoomInfo());
        });
        return roomInfos;
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    public static RoomPool getInstance() {
        if (pool == null) {
            pool = new RoomPool();
        }
        return pool;
    }
}
