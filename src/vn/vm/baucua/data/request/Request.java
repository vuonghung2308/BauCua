package vn.vm.baucua.data.request;

import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.entity.Bat;
import vn.vm.baucua.util.JsonUtils;

public class Request {

    public String content;
    public String data;

    public Request(String json) {
        Request req = JsonUtils.fromJson(json, Request.class);
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
            case "bat":
                return dataFromJson(Bat.class);
        }
        return null;
    }

    public <T> Object dataFromJson(Class<T> aClass) {
        return JsonUtils.fromJson(data, aClass);
    }

    public boolean isLoginRequest() {
        return content.equals("login");
    }

    public boolean isSignUpRequest() {
        return content.equals("register");
    }
}
