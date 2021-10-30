package vn.vm.baucua;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import vn.vm.baucua.data.request.DataGoRoomRequest;
import vn.vm.baucua.data.request.DataLoginRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.util.JsonUtils;

public class TestClient {

    public static void main(String[] args) throws IOException {
        boolean serverTest = true;
        String server_ip = "40.90.172.165";
        String localhost = "localhost";
        String host = serverTest ? server_ip : localhost;
        Integer port = 1111;

        String username = "ductoan";
        String password = username + "@1234";

        try (Socket socket = new Socket(host, port)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            DataInputStream dis = new DataInputStream(is);
            System.out.println("Connected to ip: " + host + ", port: " + port);

            if (!testLogin(dos, dis, username, password)) {
                return;
            }
            testListRoom(dos, dis);
            testGoRoom(dos, dis);
//            testGoRoom(dos, dis);
//            testOutRoom(dos, dis);

            while (true) {
                byte[] bytes = new byte[2048];
                int byteRec = dis.read(bytes);
                System.out.println("------------------------------------------------");
                String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
                System.out.println("Server: " + jsonRes);
            }
        }
    }

    private static boolean testLogin(
            DataOutputStream dos,
            DataInputStream dis,
            String username, String password
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        DataLoginRequest data = new DataLoginRequest();
        data.username = username;
        data.password = password;
        Request request = new Request();
        request.content = "login";
        request.data = JsonUtils.toJson(data);
        String jsonReq = JsonUtils.toJson(request);

        dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));

        byte[] bytes = new byte[2048];
        int byteRec = dis.read(bytes);
        String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);

        System.out.println("------------------------------------------------");
        System.out.println("Request: " + jsonReq);
        System.out.println("Response: " + jsonRes);

        bytes = new byte[2048];
        byteRec = dis.read(bytes);
        if (byteRec == -1) {
            return false;
        }
        jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        System.out.println("List room: " + jsonRes);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time call: " + elapsedTime + " ms");
        return true;
    }

    private static void testGoRoom(
            DataOutputStream dos,
            DataInputStream dis
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "go-room";
        DataGoRoomRequest data = new DataGoRoomRequest();
        data.roomId = 1;
        request.data = JsonUtils.toJson(data);
        String jsonReq = JsonUtils.toJson(request);

        dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));

        byte[] bytes = new byte[2048];
        int byteRec = dis.read(bytes);
        String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        System.out.println("------------------------------------------------");

        System.out.println("Request: " + jsonReq);
        System.out.println("Response: " + jsonRes);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time call: " + elapsedTime + " ms");

    }

    private static void testOutRoom(
            DataOutputStream dos,
            DataInputStream dis
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "out-room";
        String jsonReq = JsonUtils.toJson(request);

        dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));

        byte[] bytes = new byte[2048];
        int byteRec = dis.read(bytes);
        String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        System.out.println("------------------------------------------------");

        System.out.println("Request: " + jsonReq);
        System.out.println("Response: " + jsonRes);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time call: " + elapsedTime + " ms");
    }

    private static void testListRoom(
            DataOutputStream dos,
            DataInputStream dis
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "list-room";
        String jsonReq = JsonUtils.toJson(request);

        dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));

        byte[] bytes = new byte[2048];
        int byteRec = dis.read(bytes);
        String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        System.out.println("------------------------------------------------");

        System.out.println("Request: " + jsonReq);
        System.out.println("Response: " + jsonRes);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time call: " + elapsedTime + " ms");
    }

}
