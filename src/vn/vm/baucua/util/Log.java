package vn.vm.baucua.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Log {

    private static final String FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static void d(String tag, String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
        Date now = Calendar.getInstance().getTime();
        System.out.println(formatter.format(now) + " " + tag + ": " + msg);
    }

    public static void e(Exception e) {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
        Date now = Calendar.getInstance().getTime();
        System.err.println(formatter.format(now) + " Error: " + e.getMessage());
    }
}
