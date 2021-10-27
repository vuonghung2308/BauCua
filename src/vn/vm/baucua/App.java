package vn.vm.baucua;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.socket.ThreadSocket;

public class App {

    public static void main(String[] args) throws IOException {
        connectDatabase();
        Logger.getRootLogger().setLevel(Level.OFF);
        ServerSocket server = new ServerSocket(1111);

        System.out.println("Start listen on ip: "
                + server.getInetAddress() + ", port: "
                + server.getLocalPort()
        );
        
        while (true) {
            Socket socket = server.accept();
            new ThreadSocket(socket).start();
        }
    }

    private static void connectDatabase() {
        Thread connectThread = new Thread(() -> {
            ConnectionPool.getInstance();
            System.out.println("Connected to database.");
        });
        connectThread.start();
    }
}
