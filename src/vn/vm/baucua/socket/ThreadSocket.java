package vn.vm.baucua.socket;

import vn.vm.baucua.socket.pool.RoomPool;
import vn.vm.baucua.socket.pool.ClientPool;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.entity.RoomInfo;
import vn.vm.baucua.data.request.DataGoRoomRequest;
import vn.vm.baucua.data.request.DataLoginRequest;
import vn.vm.baucua.data.request.DataRegisterRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.DataError;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.database.dao.PlayerDao;
import vn.vm.baucua.util.JsonUtils;
import vn.vm.baucua.util.Log;

public class ThreadSocket extends Thread {

    private final Client client;
    private ClientPool clientPool;
    private RoomPool roomPool;
    private Room room; // room.send

    public ThreadSocket(Socket socket) {
        client = new Client(socket);
    }

    private void stopThreadSocket() throws IOException {
        if (client != null && client.getId() != -1) {
            Integer clientId = client.getId();
            clientPool.removeClient(clientId);
            if (room != null) {
                room.remove(client.getId());
                updateRoomMemberToOther();
                room = null;
            }
        }
        client.closeSocket();
    }

    private void initThreadSocket() throws IOException {
        clientPool = ClientPool.getInstance();
        roomPool = RoomPool.getInstance();
        client.initSocket();
    }

    @Override
    public void run() {
        try {
            initThreadSocket();
            Request request = client.receive();
            System.out.println("SERVER START");
            System.out.println(request.content);
            if (request.isLoginRequest()) {
                boolean isCorrectInfo = handleLoginRequest(request);
                if (isCorrectInfo) {
                    while (true) {
                        boolean disconnect = !handleOtherRequest();
                        if (disconnect) {
                            stopThreadSocket();
                            break;
                        }
                    }
                } else {
                    stopThreadSocket();
                }
            } else if (request.isSignUpRequest()) {
                register(request);
            }

        } catch (IOException ex) {
            Log.e(ex);
        }
    }

    private boolean handleLoginRequest(Request request) throws IOException {
        DataLoginRequest data = (DataLoginRequest) request.getDataObject();

        PlayerDao playerDao = new PlayerDao();
        Player player = playerDao.getPlayer(data.username, data.password);

        Response response = new Response();
        response.content = request.content;

        if (player != null) {
            Client c = clientPool.getClient(player.id);
            if (c != null) {
                sendError(
                        request.content, 2001,
                        "account is logged in on another device"
                );
                return false;
            }
            client.setPlayer(player);
            clientPool.addClient(client);
            response.setData(player);
            client.send(response);
            sendRoomInfos();
            return true;
        } else {
            sendError(
                    request.content, 2000,
                    "wrong username or password"
            );
            return false;
        }
    }

    private boolean handleOtherRequest() {
        try {
            Request request = client.receive();
            if (request == null) {
                return true;
            }
            switch (request.content) {
                case "go-room": {
                    handleGoRoom(request);
                    break;
                }
                case "list-room": {
                    sendRoomInfos();
                    break;
                }
                case "out-room": {
                    outRoom(request);
                    break;
                }
                case "chat": { // chat private
                    chat(request);
                }
                case "chat-all": {
                    chatAll(request);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void handleGoRoom(Request request) throws IOException {
        DataGoRoomRequest data = (DataGoRoomRequest) request.getDataObject();
        Response response = new Response(request.content);
        if (room != null) {
            sendError(request.content, 3002, "you are in a room");
            return;
        }
        if (Room.count >= data.roomId) {
            room = roomPool.goRoom(data.roomId, client);
            if (room != null) {
                response.setData(room.getPlayes());
                updateRoomMemberToOther();
                client.send(response);
            } else {
                sendError(request.content, 3000, "room full");
            }
        } else {
            sendError(request.content, 3001, "room not exists");
        }
    }

    private void updateRoomMemberToOther() throws IOException {
        Response playersResponse = new Response(
                "room-member", room.getPlayes()
        );
        room.sendToAll(playersResponse, client.getId());
    }

    private void sendRoomInfos() throws IOException {
        Response res = new Response(
                "list-room", roomPool.getRoomInfos()
        );
        client.send(res);
    }

    private void outRoom(Request request) throws IOException {
        if (room != null) {
            room.remove(client.getId());
            updateRoomMemberToOther();
            Response response = new Response(
                    request.content, roomPool.getRoomInfos()
            );
            client.send(response);
            room = null;
        } else {
            sendError(request.content, 4000, "you are not in any room");
        }
    }

    private void sendError(String content, int code, String msg) throws IOException {
        DataError error = new DataError(code, msg);
        Response response = new Response(content, error);
        client.send(response);
    }
    
    private void register(Request request) {
        try {
            System.out.println("client register");
            DataRegisterRequest register = (DataRegisterRequest) request.getDataObject();
            String username = register.username;
            String password = register.password;
            String fullName = register.fullName;
            PlayerDao playerDao = new PlayerDao();
            Response response;
            if(playerDao.insertPlayer(username, password, fullName)){
                response = new Response("register", username, 200);
            }
            else{
                response = new Response("register", username, 4000);
            }
            client.send(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void chat(Request request) { // chat private
        ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
        ChatMessage chatMessageSend = new ChatMessage(client.getPlayer().id, chatMessageReceive.message);
        Response response = new Response("chat", chatMessageSend, 200);
        room.sendOneClient(chatMessageReceive.id, response);
    }

    private void chatAll(Request request) // chat all
        {try {
            ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
            ChatMessage chatMessageSend = new ChatMessage(client.getPlayer().id, chatMessageReceive.message);
            Response response = new Response("chat", chatMessageSend, 200);
            room.sendToAll(response, this.client.getId());
        } catch (IOException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
