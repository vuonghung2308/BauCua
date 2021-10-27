package vn.vm.baucua;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.socket.ThreadSocket;

public class App {

    public static boolean debug = true;

    public static void main(String[] args) throws IOException {
        if (args.length != 0 && args[0].equals("-m")) {
            debug = !args[1].equals("app");
        }

        connectToDatabase();
        Logger.getRootLogger().setLevel(Level.OFF);
        ServerSocket server = new ServerSocket(1111);

        System.out.println("Start listen on address: "
                + server.getInetAddress().getHostAddress()
                + ", port: " + server.getLocalPort()
        );

        while (true) {
            Socket socket = server.accept();
            new ThreadSocket(socket).start();
        }
    }

    private static void connectToDatabase() {
        Thread connectThread = new Thread(() -> {
            ConnectionPool.getInstance();
            System.out.println("Connected to database.");
        });
        connectThread.start();
    }
}
