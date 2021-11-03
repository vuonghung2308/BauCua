package vn.vm.baucua.data.response;

public class TimeResponse {

    public int time_left;

    public TimeResponse(int time_left) {
        this.time_left = time_left;
    }

    public static Response get(int seconds) {
        TimeResponse response = new TimeResponse(seconds);
        return Response.success("timer", response);
    }
}
