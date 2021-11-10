package vn.vm.baucua.data.request;

import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.entity.Bet;
import vn.vm.baucua.util.StroUtils;

public class Request {

    public String content;
    public String data;

    public Request(String string) {
        Request req = StroUtils.fromStro(string, Request.class);
        this.content = req.content;
        this.data = req.data;
    }

    public Request() {
    }

    public Object getDataObject() {
        switch (content) {
            case "login":
                return dataFromJson(LoginRequest.class);
            case "go_room":
                return dataFromJson(GoRoomRequest.class);
            case "register":
                return dataFromJson(RegisterRequest.class);
            case "chat":
                return dataFromJson(ChatMessage.class);
            case "chat_all":
                return dataFromJson(ChatMessage.class);
            case "bet":
                return dataFromJson(Bet.class);
        }
        return null;
    }

    private <T> Object dataFromJson(Class<T> aClass) {
        return StroUtils.fromStro(data, aClass);
    }

    public boolean isLoginRequest() {
        return content.equals("login");
    }

    public boolean isRegisterRequest() {
        return content.equals("register");
    }
}
