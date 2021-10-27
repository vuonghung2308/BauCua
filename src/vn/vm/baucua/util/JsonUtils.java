package vn.vm.baucua.util;

import com.google.gson.Gson;

public class JsonUtils {

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static <T extends Object> T fromJson(String json, Class<T> classOfT) {
        return new Gson().fromJson(json, classOfT);
    }
}
