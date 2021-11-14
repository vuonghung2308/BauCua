package vn.vm.baucua;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import vn.vm.baucua.database.ConnectionPool;
import vn.vm.baucua.socket.ThreadSocket;
import vn.vm.baucua.util.Log;
import vn.vm.baucua.util.ThreadUtils;

public class App {

    public static boolean debug = true;

    public static void main(String[] args) throws IOException {
        Logger.getRootLogger().setLevel(Level.OFF);
        if (args.length != 0 && args[0].equals("-m")) {
            debug = !args[1].equals("app");
        }

        ServerSocket server = new ServerSocket(1111);
        connectToDatabase();

        String message = "Start listen on address: "
                + server.getInetAddress().getHostAddress()
                + ", port: " + server.getLocalPort();
        Log.d("App", message);

        while (true) {
            Socket socket = server.accept();
            new ThreadSocket(socket).start();
        }
    }

    private static void connectToDatabase() {
        ThreadUtils.runInNewThread(() -> {
            ConnectionPool.getInstance();
            Log.d("App", "Connected to database.");
        });
    }
}
