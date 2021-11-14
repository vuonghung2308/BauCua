package vn.vm.baucua.util;

import vn.vm.baucua.stro.Stro;

public class StroUtils {

    private static Stro stro;

    public static String toStro(Object object) {
        initInstance();
        return stro.toStro(object);
    }

    public static <T extends Object> T fromStro(
            String json, Class<T> classOfT
    ) {
        initInstance();
        return stro.fromStro(json, classOfT);
    }

    public static void initInstance() {
        if (stro == null) {
            stro = new Stro();
        }
    }
}
