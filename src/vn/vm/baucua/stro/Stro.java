package vn.vm.baucua.stro;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import vn.vm.baucua.util.Log;

public class Stro {

    public <T extends Object> T fromStro(
            String json, Class<T> classOfT
    ) {
        try {
            T t = classOfT.newInstance();
            List<Item> items = Item.getItems(json);

            for (Item item : items) {
                Field field;
                try {
                    field = classOfT.getDeclaredField(item.key);
                } catch (NoSuchFieldException | SecurityException e) {
                    continue;
                }
                switch (item.getType()) {
                    case Item.FLOAT: {
                        field.set(t, item.getFloat());
                        break;
                    }
                    case Item.INTEGER: {
                        if (field.getType() == Integer.TYPE) {
                            field.setInt(t, (int) item.getInteger());
                        } else {
                            field.setLong(t, item.getInteger());
                        }
                        break;
                    }
                    case Item.STRING: {
                        field.set(t, item.getString());
                        break;
                    }
                    case Item.OBJECT: {
                        field.set(t, fromStro(
                                item.value, field.getType()));
                        break;
                    }
                    case Item.ARRAY: {
                        Class<?> genericClass = Utils
                                .getGenericClass(field.getGenericType());
                        ArrayList<Object> objects = new ArrayList<>();
                        item.getArray().forEach(string -> {
                            objects.add(fromStro(string, genericClass));
                        });
                        field.set(t, objects);
                        break;
                    }
                    case Item.UNKNOWN: {
                        break;
                    }
                }
            }
            return t;
        } catch (InstantiationException | IllegalAccessException ex) {
            Log.e(ex);
            return null;
        }
    }

    public String toStro(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        String res = "";
        if (!(object instanceof List)) {
            try {
                for (Field field : fields) {
                    String key = field.getName();
                    String value = null;
                    String values = "";
                    if (field.getType() == Boolean.TYPE) {
                        value = String.valueOf(field.getBoolean(object));
                    } else if (field.getType() == Integer.TYPE) {
                        value = String.valueOf(field.getInt(object));
                    } else if (field.getType() == Long.TYPE) {
                        value = String.valueOf(field.getLong(object));
                    } else if (field.getType() == String.class) {
                        if (field.get(object) == null) {
                            continue;
                        }
                        String originString = String.valueOf(field.get(object));
                        originString = originString.replaceAll("'", "\\\\'");
                        value = "'" + originString + "'";
                    } else if (field.getType().isAssignableFrom(List.class)) {
                        if (field.get(object) == null) {
                            continue;
                        }
                        List<Object> objects = (List<Object>) field.get(object);
                        if (objects.size() > 0) {
                            values = objects.stream().map(
                                    o -> toStro(o) + ",")
                                    .reduce(values, String::concat);
                            value = "[" + values.substring(0, values.length() - 1) + "]";
                        }
                    } else {
                        value = toStro(field.get(object));
                    }
                    if (value != null && !value.isEmpty()) {
                        res = res + key + ":" + value + ",";
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException
                    | SecurityException e) {
            }
            return "{" + res.substring(0, res.length() - 1) + "}";
        } else {
            List<Object> objects = (List<Object>) object;
            res = objects.stream().map(o -> toStro(o) + ",")
                    .reduce(res, String::concat);
            return "[" + res.substring(0, res.length() - 1) + "]";
        }
    }
}
