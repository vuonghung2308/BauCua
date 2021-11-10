package vn.vm.baucua.stro;

public class BracketUtils {

    public static int curlyBracket(String string) {
        int index = 1;
        int sum = 1;
        while (sum != 0) {
            if (string.charAt(index) == '{') {
                sum = sum + 1;
            } else if (string.charAt(index) == '}') {
                sum = sum - 1;
            }
            index++;
        }
        return index;
    }

    public static int squareBracket(String string) {
        int index = 1;
        int sum = 1;
        while (sum != 0) {
            if (string.charAt(index) == '[') {
                sum = sum + 1;
            } else if (string.charAt(index) == ']') {
                sum = sum - 1;
            }
            index++;
        }
        return index;
    }
}
