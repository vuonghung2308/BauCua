package vn.vm.baucua.stro;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Utils {

    public static Class<?> getGenericClass(Type type) {
        ParameterizedType newType = (ParameterizedType) type;
        Type[] types = newType.getActualTypeArguments();
        Type paramType = types[0];
        return (Class<?>) paramType;
    }
}