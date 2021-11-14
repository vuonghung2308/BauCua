package vn.vm.baucua.util;

public class ThreadUtils {

    public static void runInNewThread(Runnable runnable) {
        Thread connectThread = new Thread(runnable);
        connectThread.start();
    }
}
