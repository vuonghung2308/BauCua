/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.request.ForgotPasswordRequest;
import vn.vm.baucua.data.request.GoRoomRequest;
import vn.vm.baucua.data.request.LoginRequest;
import vn.vm.baucua.data.request.RegisterRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.util.StroUtils;

/**
 *
 * @author User
 */
public class Client3 {
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean serverTest = false;
        String server_ip = "40.90.172.165";
        String localhost = "localhost";
        String host = serverTest ? server_ip : localhost;
        Integer port = 1111;

        String username = "thanhngo";
        String password = username + "@1234";
        String email = "mutrangaoden@gmail.com";
        String newPass = "123456";
        String key = "425976";

        try (Socket socket = new Socket(host, port)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            DataInputStream dis = new DataInputStream(is);
            System.out.println("Connected to ip: " + host + ", port: " + port);
//
//
            if (!testLogin(dos, dis, username, "thanh123456")) {
                return;
            }
                
//            testForgotpass(dos, dis, username, email);
//            testRank(dos, dis);
//            testCreateNewPass(dos, dis, username, newPass, key);
//            testSubmitCode(dos, dis, username, email);
//            testSubmitPass(dos, dis, username, email);

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

    private static void testForgotpass(DataOutputStream dos, DataInputStream dis, String username, String email) throws IOException {
        long startTime = System.currentTimeMillis();
        ForgotPasswordRequest data = new ForgotPasswordRequest();
        data.email = email;
        data.username = username;
        Request request = new Request();
        request.content = "forgot_password";
        request.data = StroUtils.toStro(data);
        String jsonReq = StroUtils.toStro(request);
        
        dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));
/////////////////////////////////////////////////
        byte[] bytes = new byte[2048];
        int byteRec = dis.read(bytes);
        String jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        
        System.out.println(jsonRes);
//        Response response = StroUtils.fromStro(jsonRes, Response.class);
        if(true){
            System.out.println("Nhap code: ");
            Scanner input = new Scanner(System.in);
            String code = input.next();
            
            request = new Request();
            request.content = "submit_code";
            request.data = code;
            jsonReq = StroUtils.toStro(request);
            dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));
            
//            response = StroUtils.fromStro(jsonRes, Response.class);
            if(true){
                System.out.println("Nhap code: ");
                input = new Scanner(System.in);
                String newPass = input.nextLine();

                request = new Request();
                request.content = "submit_pass";
                request.data = newPass;
                jsonReq = StroUtils.toStro(request);
                dos.write(jsonReq.getBytes(StandardCharsets.UTF_8));
                
                bytes = new byte[2048];
                byteRec = dis.read(bytes);
                jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);

                System.out.println("------------------------------------------------");
                System.out.println("Request: " + jsonReq);
                System.out.println("Response: " + jsonRes);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time excute: " + (endTime - startTime) + "ms");
    }

    private static void testRank(DataOutputStream dos, DataInputStream dis) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = new Request();
        request.content = "rank";
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
            return ;
        }
        jsonRes = new String(bytes, 0, byteRec, StandardCharsets.UTF_8);
        System.out.println("List rank: " + jsonRes);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Time excute: " + (endTime - startTime) + "ms");
    }

    private static void testSubmitCode(DataOutputStream dos, DataInputStream dis, String username, String email) {
        
        
        
        
    }

    private static void testSubmitPass(DataOutputStream dos, DataInputStream dis, String username, String email) {
        
    }


}
