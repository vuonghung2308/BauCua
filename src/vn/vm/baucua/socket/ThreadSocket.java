package vn.vm.baucua.socket;

import vn.vm.baucua.socket.pool.RoomPool;
import vn.vm.baucua.socket.pool.ClientPool;
import java.io.IOException;
import java.net.Socket;
import vn.vm.baucua.data.entity.Bet;
import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.request.GoRoomRequest;
import vn.vm.baucua.data.request.LoginRequest;
import vn.vm.baucua.data.request.RegisterRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.database.dao.UserDao;
import vn.vm.baucua.util.Log;

public class ThreadSocket extends Thread {

    private final UserDao userDao;
    private final Client client;
    private ClientPool clientPool;
    private RoomPool roomPool;
    private Room room;

    public ThreadSocket(Socket socket) {
        client = new Client(socket);
        userDao = new UserDao();
    }

    private void stopThreadSocket() throws IOException {
        if (client != null && client.getId() != -1) {
            Integer clientId = client.getId();
            if (room != null) {
                room.remove(client.getId());
                room = null;
            }
            clientPool.removeClient(clientId);
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
                if (handleLoginRequest(request)) {
                    while (true) {
                        if (!handleOtherRequest()) {
                            stopThreadSocket();
                            break;
                        }
                    }
                } else {
                    stopThreadSocket();
                }
            } else if (request.isRegisterRequest()) {
                handleRegister(request);
            }

        } catch (IOException ex) {
            Log.e(ex);
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
                case "chat": {
                    chat(request);
                    break;
                }
                case "chat_all": {
                    handleChatAll(request);
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
                case "bet": {
                    handleBat(request);
                    break;
                }
                case "ping": {
                    break;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean handleLoginRequest(Request request) throws IOException {
        LoginRequest data = (LoginRequest) request.getDataObject();
        User user = userDao.getUser(data.username, data.password);
        if (user != null) {
            Client c = clientPool.getClient(user.id);
            if (c != null) {
                sendError(request, 401,
                        "account is logged in on another device");
                return false;
            }
            client.setUser(user);
            clientPool.addClient(client);
            sendSuccess(request, user);
            sendRoomInfos();
            return true;
        } else {
            sendError(request, 400,
                    "wrong username or password");
            return false;
        }
    }

    private void handleBat(Request request) {
        Bet bet = (Bet) request.getDataObject();
        if (!room.isGameStarted()) {
            sendError(request, 900, "game has not started yet");
        } else {
            if (room.setBat(client.getId(), bet)) {
                sendSuccess(request);
            } else {
                sendError(request, 901, "not enough money");
            }
        }
    }

    private void handlePlay(Request request) {
        if (room.isHost(client.getId())) {
            if (room.canPlay()) {
                sendSuccess(request);
                room.playGame();
            } else {
                sendError(request, 1000, "has some one not ready");
            }
        } else {
            sendError(request, 1001, "you are not host");
        }
    }

    private void handleReady(Request request) {
        if (room.setReady(client.getId())) {
            sendSuccess(request);
        } else {
            sendError(request, 800, "you are ready");
        }
    }

    private void handleGoRoom(Request request) throws IOException {
        GoRoomRequest data = (GoRoomRequest) request.getDataObject();
        if (room != null) {
            sendError(request, 600, "you are in a room");
            return;
        }
        if (Room.count >= data.id) {
            room = roomPool.goRoom(data.id, client);
            if (room != null) {
                sendSuccess(request, room.getRoomDetail());
                room.sendRoomDetail(client.getId());
            } else {
                sendError(request, 601, "room full");
            }
        } else {
            sendError(request, 602, "room not exists");
        }
    }

    private void handleOutRoom(Request request) {
        if (room != null) {
            room.remove(client.getId());
            room = null;
            sendSuccess(request, roomPool.getRoomInfos());
        } else {
            sendError(request, 700, "you are not in any room");
        }
    }

    private void sendRoomInfos() throws IOException {
        Response res = Response.success(
                "list_room", roomPool.getRoomInfos());
        client.send(res);
    }

    private void handleRegister(Request request) throws IOException {
        RegisterRequest register = (RegisterRequest) request.getDataObject();
        String username = register.username;
        String password = register.password;
        String fullName = register.fullname;
        if (userDao.insertUser(username, password, fullName)) {
            sendSuccess(request);
        } else {
            sendError(request, 500, "account exists");
        }
        stopThreadSocket();
    }

    private void chat(Request request) {
        ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
        ChatMessage chatMessageSend = new ChatMessage(
                client.getId(), chatMessageReceive.message);
        Response res = Response.success(request, chatMessageSend);
        sendSuccess(request);
        room.sendOneClient(chatMessageReceive.id, res);
    }

    private void handleChatAll(Request request) {
        ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
        ChatMessage chatMessageSend = new ChatMessage(
                client.getId(), chatMessageReceive.message);
        Response res = Response.success(request, chatMessageSend);
        sendSuccess(request, chatMessageSend);
        room.sendToAll(res, client.getId());
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
