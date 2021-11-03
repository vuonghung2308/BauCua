package vn.vm.baucua.socket;

import vn.vm.baucua.socket.pool.RoomPool;
import vn.vm.baucua.socket.pool.ClientPool;
import java.io.IOException;
import java.net.Socket;
import vn.vm.baucua.data.entity.Bat;
import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.request.GoRoomRequest;
import vn.vm.baucua.data.request.LoginRequest;
import vn.vm.baucua.data.request.RegisterRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.database.dao.UserrDao;
import vn.vm.baucua.util.Log;

public class ThreadSocket extends Thread {

    private final Client client;
    private ClientPool clientPool;
    private RoomPool roomPool;
    private Room room;

    public ThreadSocket(Socket socket) {
        client = new Client(socket);
    }

    private void stopThreadSocket() throws IOException {
        if (client != null && client.getId() != -1) {
            Integer clientId = client.getId();
            clientPool.removeClient(clientId);
            if (room != null) {
                room.remove(client.getId());
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
                handleRegister(request);
            }

        } catch (IOException ex) {
            Log.e(ex);
        }
    }

    private boolean handleLoginRequest(Request request) throws IOException {
        LoginRequest data = (LoginRequest) request.getDataObject();

        UserrDao playerDao = new UserrDao();
        User user = playerDao.getUser(data.username, data.password);

        if (user != null) {
            Client c = clientPool.getClient(user.id);
            if (c != null) {
                sendError(request, 2001,
                        "account is logged in on another device");
                return false;
            }
            client.setUser(user);
            clientPool.addClient(client);
            sendSuccess(request, user);
            sendRoomInfos();
            return true;
        } else {
            sendError(request, 2000,
                    "wrong username or password");
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
                case "go_room": {
                    handleGoRoom(request);
                    break;
                }
                case "list_room": {
                    sendRoomInfos();
                    break;
                }
                case "out_room": {
                    handleOutRoom(request);
                    break;
                }
                case "chat": { // chat private
                    chat(request);
                    break;
                }
                case "chat_all": {
                    chatAll(request);
                    break;
                }
                case "ready": {
                    handleReady(request);
                    break;
                }
                case "play": {
                    handlePlay(request);
                    break;
                }
                case "bat": {
                    handleBat(request);
                    break;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void handleReady(Request request) {
        if (room.setReady(client.getId())) {
            sendSuccess(request);
        } else {
            sendError(request, 6000, "you are ready");
        }
    }

    private void handleBat(Request request) {
        Bat bat = (Bat) request.getDataObject();
        if (!room.isGameStarted()) {
            sendError(request, 7001, "game has not started yet");
        } else {
            if (room.setBat(client.getId(), bat)) {
                sendSuccess(request);
            } else {
                sendError(request, 7000, "not enough money");
            }
        }
    }

    private void handlePlay(Request request) {
        if (room.isHost(client.getId())) {
            if (room.canPlay()) {
                sendSuccess(request);
                room.playGame();
            } else {
                sendError(request, 5000, "has some one not ready");
            }
        } else {
            sendError(request, 5001, "you are not host");
        }
    }

    private void handleGoRoom(Request request) throws IOException {
        GoRoomRequest data = (GoRoomRequest) request.getDataObject();
        if (room != null) {
            sendError(request, 3002, "you are in a room");
            return;
        }
        if (Room.count >= data.room_id) {
            room = roomPool.goRoom(data.room_id, client);
            if (room != null) {
                sendSuccess(request, room.getRoomDetail());
                room.sendRoomDetail(client.getId());
            } else {
                sendError(request, 3000, "room full");
            }
        } else {
            sendError(request, 3001, "room not exists");
        }
    }

    private void sendRoomInfos() throws IOException {
        Response res = Response.success(
                "list_room", roomPool.getRoomInfos());
        client.send(res);
    }

    private void handleOutRoom(Request request) throws IOException {
        if (room != null) {
            room.remove(client.getId());
            room.sendRoomDetail(-1);
            room = null;
        } else {
            sendError(request, 4000, "you are not in any room");
        }
    }

    private void handleRegister(Request request) {
        RegisterRequest register = (RegisterRequest) request.getDataObject();
        String username = register.username;
        String password = register.password;
        String fullName = register.fullName;
        UserrDao playerDao = new UserrDao();
        if (playerDao.insertUser(username, password, fullName)) {
            sendSuccess(request);
        } else {
            sendError(request, 7000, "user exists");
        }
    }

    private void chat(Request request) { // chat private
        ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
        ChatMessage chatMessageSend = new ChatMessage(client.getUser().id, chatMessageReceive.message);
        Response response = new Response("chat", chatMessageSend, 200);
        room.sendOneClient(chatMessageReceive.id, response);
    }

    private void chatAll(Request request) {// chat all
    }

    private void sendError(Request request, int code, String msg) {
        Response response = Response.error(code, request, msg);
        client.send(response);
    }

    private void sendSuccess(Request request, Object data) {
        client.send(Response.success(request, data));
    }

    private void sendSuccess(Request request) {
        client.send(Response.success(request));
    }
}
