package vn.vm.baucua.data.response;

public class DataError {

    public int code = 400;
    public String message;

    public DataError(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
