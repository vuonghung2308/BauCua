package vn.vm.baucua.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import vn.vm.baucua.data.entity.Bet;
import vn.vm.baucua.data.request.GoRoomRequest;
import vn.vm.baucua.data.request.LoginRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.util.StroUtils;

public class Client2 {

    public static void main(String[] args) throws IOException {
        boolean serverTest = false;
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

            testLogin(dos, dis, username, password);
            testGoRoom(dos, dis);

            Scanner scanner = new Scanner(System.in);
            int i = scanner.nextInt();
            if (i == 1) {
                testReady(dos, dis);
            }

            i = scanner.nextInt();
            if (i == 1) {
                testSetBat(dos, dis);
            }

//            testPlayGame(dos, dis);
            while (true) {
                byte[] bytes = new byte[2048];
                int byteRec = dis.read(bytes);
                System.out.println("------------------------------------------------");
                String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
                System.out.println("Server: " + jsonRes);
            }
        }
    }

    private static void read(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[2048];
        int byteRec = dis.read(bytes);
        System.out.println("------------------------------------------------");
        String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        System.out.println("Server: " + jsonRes);
    }

    private static boolean testLogin(
            DataOutputStream dos,
            DataInputStream dis,
            String username, String password
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        LoginRequest data = new LoginRequest();
        data.username = username;
        data.password = password;
        Request request = new Request();
        request.content = "login";
        request.data = StroUtils.toStro(data);
        String jsonReq = StroUtils.toStro(request);

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
        request.content = "go_room";
        GoRoomRequest data = new GoRoomRequest();
        data.id = 1;
        request.data = StroUtils.toStro(data);
        String jsonReq = StroUtils.toStro(request);

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

    private static void testPlayGame(
            DataOutputStream dos,
            DataInputStream dis
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "play";
        String jsonReq = StroUtils.toStro(request);

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

    private static void testReady(
            DataOutputStream dos,
            DataInputStream dis
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "ready";
        String jsonReq = StroUtils.toStro(request);

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

    private static void testSetBat(
            DataOutputStream dos,
            DataInputStream dis
    ) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "bet";
        Bet bat = new Bet();
        bat.val2 = 2;
        bat.val5 = 3;
        request.data = StroUtils.toStro(bat);
        String jsonReq = StroUtils.toStro(request);

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
