package vn.vm.baucua.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.util.StroUtils;
import vn.vm.baucua.util.Log;

public class Client {

    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Timer timer;

    private User user;
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
        schedulePing();
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getId() {
        if (user == null) {
            return -1;
        }
        return user.id;
    }

    public User getUser() {
        return user;
    }

    public void closeSocket() throws IOException {
        dos.close();
        dis.close();
        is.close();
        os.close();
        socket.close();
        if (timer != null) {
            timer.cancel();
        }
    }

    public void initSocket() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
        dis = new DataInputStream(is);
        dos = new DataOutputStream(os);
    }

    public void send(Response response) {
        try {
            String responseJson = StroUtils.toStro(response);
            dos.write(responseJson.getBytes(StandardCharsets.UTF_8));
            printLog(responseJson, false);
            schedulePing();
            dos.flush();

        } catch (IOException e) {
            Log.e(e);
        }
    }

    public Request receive() throws IOException {
        byte[] data = new byte[5048];
        int bytes = dis.read(data);
        if (bytes < 0) {
            throw new IOException("connection closed");
        }

        schedulePing();
        String jsonRequest = new String(
                data, 0, bytes,
                StandardCharsets.UTF_8
        );

        printLog(jsonRequest, true);

        Request request = new Request(jsonRequest);
        if (request.content == null) {
            Response response = Response.error(
                    300, request, "no content"
            );
            send(response);
            return null;
        }
        schedulePing();
        return request;
    }

    private void printLog(String json, boolean req) {
        String remoteIp = socket
                .getRemoteSocketAddress()
                .toString();
        remoteIp = remoteIp.replace("/", "");
        String action = "request";
        if (req == false) {
            action = "response";
        }
        Log.d("IP: " + remoteIp + ", "
                + action, json);
    }

    private void schedulePing() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                send(Response.ping());
            }
        };
        timer.schedule(task, 60000);
    }
}
