package vn.vm.baucua.stro;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {

    public String key;
    public String value;

    public Item(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getType() {
        if (value.contains("false") || value.contains("true")) {
            return BOOLEAN;
        }
        if (value.charAt(0) == '[') {
            return ARRAY;
        }
        if (value.charAt(0) == '{') {
            return OBJECT;
        }
        if (value.charAt(0) == '\'') {
            return STRING;
        }
        String regrex = "[+-]?([0-9]*[.])?[0-9]+";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            if (value.contains(".")) {
                return FLOAT;
            } else {
                return INTEGER;
            }
        }
        return UNKNOWN;
    }

    public long getInteger() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public float getFloat() {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0F;
        }
    }

    public boolean getBoolean() {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }

    public String getString() {
        String string = value.replaceAll("\\\\'", "'");
        return string.substring(1, string.length() - 1);
    }

    public String getObject() {
        return value.substring(1, value.length() - 1);
    }

    public List<String> getArray() {
        ArrayList<String> objects = new ArrayList<>();
        String newString = String.valueOf(value);
        newString = newString.substring(1, newString.length() - 1);
        while (newString.length() > 0) {
            int indexOfEndObject = BracketUtils.curlyBracket(newString);
            String object = newString.substring(0, indexOfEndObject);
            objects.add(object);
            if (indexOfEndObject != newString.length()) {
                newString = newString.substring(
                        indexOfEndObject + 1, newString.length());
            } else {
                newString = "";
            }
        }
        return objects;
    }

    public static List<Item> getItems(String string) {
        ArrayList<Item> items = new ArrayList<>();
        if (string.charAt(0) == '{') {
            string = string.substring(1, string.length() - 1);
        }
        while (string.length() > 0) {
            int indexOfEndKey = string.indexOf(":");
            int indexOfEndValue = 0;
            char character = string.charAt(indexOfEndKey + 1);
            if (character != '[' && character != '{' && character != '\'') {
                indexOfEndValue = string.indexOf(",");
            } else {
                switch (character) {
                    case '[':
                        indexOfEndValue = BracketUtils.squareBracket(
                                string.substring(indexOfEndKey + 1));
                        indexOfEndValue = indexOfEndValue + indexOfEndKey + 1;
                        break;
                    case '{':
                        indexOfEndValue = BracketUtils.curlyBracket(
                                string.substring(indexOfEndKey + 1));
                        indexOfEndValue = indexOfEndValue + indexOfEndKey + 1;
                        break;
                    default:
                        for (int i = indexOfEndKey + 2; i < string.length(); i++) {
                            if (string.charAt(i) == '\'' && string.charAt(i - 1) != '\\') {
                                indexOfEndValue = i + 1;
                                break;
                            }
                        }
                        break;
                }
            }
            if (indexOfEndValue == -1) {
                indexOfEndValue = string.length();
            }
            String key = string.substring(0, indexOfEndKey);
            String value = string.substring(
                    indexOfEndKey + 1, indexOfEndValue);
            if (indexOfEndValue != string.length()) {
                string = string.substring(
                        indexOfEndValue + 1, string.length());
            } else {
                string = "";
            }
            items.add(new Item(key, value));
        }
        return items;
    }

    public static final int INTEGER = 0;
    public static final int STRING = 1;
    public static final int FLOAT = 2;
    public static final int ARRAY = 3;
    public static final int OBJECT = 4;
    public static final int BOOLEAN = 5;
    public static final int UNKNOWN = 6;

}
