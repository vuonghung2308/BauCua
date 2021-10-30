package vn.vm.baucua.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.DataError;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.util.JsonUtils;
import vn.vm.baucua.util.Log;

public class Client {

    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Player player;
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Player getPlayer() {
        return player;
    }

    public int getId() {
        if (player == null) {
            return -1;
        }
        return player.id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void closeSocket() throws IOException {
        dos.close();
        dis.close();
        is.close();
        os.close();
        socket.close();
    }

    public void initSocket() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
        dis = new DataInputStream(is);
        dos = new DataOutputStream(os);
    }

    public void send(Response response) throws IOException {
        String responseJson = JsonUtils.toJson(response);
        dos.write(responseJson.getBytes(StandardCharsets.UTF_8));
        dos.flush();
    }

    public Request receive() throws IOException {
        byte[] data = new byte[5048];
        int bytes = dis.read(data);
        if (bytes < 0) {
            return null;
        }
        String jsonRequest = new String(
                data, 0, bytes,
                StandardCharsets.UTF_8
        );
        Request request = new Request(jsonRequest);
        String remoteIp = socket.getRemoteSocketAddress().toString();
        remoteIp = remoteIp.replace("/", "");
        Log.d("IP: " + remoteIp + ", request", jsonRequest);
        if (request.content == null) {
            DataError error = new DataError(398, "no content");
            Response response = new Response("error", error);
            send(response);
            return null;
        }
        return request;
    }
}
