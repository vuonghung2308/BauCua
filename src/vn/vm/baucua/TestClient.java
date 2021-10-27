/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import vn.vm.baucua.data.request.DataLoginRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.util.JsonUtils;

public class TestClient {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        String ip = "40.90.172.165";
        String localhost = "localhost";
        Integer port = 1111;

        try (Socket socket = new Socket(localhost, port)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            DataInputStream dis = new DataInputStream(is);

            DataLoginRequest data = new DataLoginRequest();
            data.username = "manhhung";
            data.password = "manhhung@1234";
            Request request = new Request();
            request.content = "login";
            request.data = JsonUtils.toJson(data);
            String jsonReq = JsonUtils.toJson(request);
            dos.writeUTF(jsonReq);
            dos.flush();
            String response = dis.readUTF();
            System.out.println("ip: " + ip + ", port: " + port);
            System.out.println("Request: " + jsonReq);
            System.out.println("Response: " + response);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Time call: " + elapsedTime + " ms");
        }
    }
}
