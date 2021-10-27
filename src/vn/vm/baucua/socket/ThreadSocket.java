package vn.vm.baucua.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.request.DataLoginRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.DataError;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.database.dao.PlayerDao;
import vn.vm.baucua.util.JsonUtils;

public class ThreadSocket extends Thread {

    private final Socket socket;
    private Client client;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ThreadSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            initSocket();

            String jsonRequest = dis.readUTF();
            Request request = new Request(jsonRequest);

            if (request.isLoginRequest()) {
                boolean isCorrectInfo = handleLoginRequest(request);
                if (isCorrectInfo) {
                    while (true) {
                        boolean disconnect = !handleOtherRequest();
                        if (disconnect) {
                            closeSocket();
                            break;
                        }
                    }
                }
            } else if (request.isSignUpRequest()) {
                // handle signup here
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void closeSocket() throws IOException {
        ClientPool.removeClient(client.getId());
        dos.close();
        dis.close();
        is.close();
        os.close();
        socket.close();
    }

    private void initSocket() throws IOException {
        client = new Client(socket);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        dis = new DataInputStream(is);
        dos = new DataOutputStream(os);
    }

    private boolean handleLoginRequest(Request request) throws IOException {
        DataLoginRequest data = (DataLoginRequest) request.getDataObject();

        PlayerDao playerDao = new PlayerDao();
        Player player = playerDao.getPlayer(data.username, data.password);

        Response response = new Response();
        response.content = request.content;

        if (player != null) {
            String jsonPlayer = JsonUtils.toJson(player);
            response.data = jsonPlayer;;
            client.setPlayer(player);
            ClientPool.addClient(client);
            String responseJson = JsonUtils.toJson(response);
            dos.writeUTF(responseJson);
            dos.flush();
            return true;
        } else {
            DataError error = new DataError();
            error.message = "wrong username or password";
            String jsonError = JsonUtils.toJson(error);
            response.data = jsonError;
            String responseJson = JsonUtils.toJson(response);
            dos.writeUTF(responseJson);
            dos.flush();
            socket.close();
            return false;
        }
    }

    private boolean handleOtherRequest() {
        try {
            String string = dis.readUTF();
            //handle other request here
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
