package vn.vm.baucua.socket;

import vn.vm.baucua.socket.pool.ClientPool;
import java.io.IOException;
import java.net.Socket;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.util.Log;

public class ThreadSocket extends Thread {

    private final Client client;
    private ClientPool clientPool;

    public ThreadSocket(Socket socket) {
        client = new Client(socket);
    }

    private void stopThreadSocket() throws IOException {
        if (client != null && client.getId() != -1) {
            Integer clientId = client.getId();
            clientPool.removeClient(clientId);
            client.outRoom();
        }
        client.closeSocket();
    }

    private void initThreadSocket() throws IOException {
        clientPool = ClientPool.getInstance();
        client.initSocket();
    }

    @Override
    public void run() {
        try {
            initThreadSocket();
            Request request = client.receive();

            if (request.isLoginRequest()) {
                if (client.handleLoginRequest(request)) {
                    while (true) {
                        if (!handleOtherRequest()) { // còn rank request
                            stopThreadSocket();
                            break;
                        }
                    }
                } else {
                    stopThreadSocket();
                }
            } else if (request.isRegisterRequest()) { // done
                client.handleRegister(request);
                stopThreadSocket();
            } else if(request.isForgotPassword()){ // done
                client.handleForgot(request);
                stopThreadSocket();
            }else if(request.isCreateNewPassword()){
                client.handleCreate(request);
                stopThreadSocket();
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
                    client.handleGoRoom(request);
                    break;
                }
                case "list_room": {
                    client.sendRoomInfos(request);
                    break;
                }
                case "out_room": {
                    client.handleOutRoom(request);
                    break;
                }
                case "kick": {
                    client.handleKick(request);
                    break;
                }
                case "chat": {
                    client.handleChat(request);
                    break;
                }
                case "chat_all": {
                    client.handleChatAll(request);
                    break;
                }
                case "ready": {
                    client.handleReady(request);
                    break;
                }
                case "unready": {
                    client.handleUnready(request);
                    break;
                }
                case "play": {
                    client.handlePlay(request);
                    break;
                }
                case "bet": {
                    client.handleBat(request);
                    break;
                }
                case "info":
                    client.sendPersonalInfo();
                    break;
                case "ping": {
                    break;
                }
                case "rank": { // chưa xong
                    client.handRank(request);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
