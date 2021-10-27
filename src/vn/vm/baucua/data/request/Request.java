package vn.vm.baucua.data.request;

import vn.vm.baucua.util.JsonUtils;

public class Request {

    public String content;
    public String data;

    public Request() {
    }

    public Request(String json) {
        Request req = JsonUtils.fromJson(json, Request.class);
        this.content = req.content;
        this.data = req.data;
    }

    public Object getDataObject() {
        switch (content) {
            case "login":
                return JsonUtils.fromJson(data, DataLoginRequest.class);
        }
        return null;
    }

    public boolean isLoginRequest() {
        return content.equals("login");
    }

    public boolean isSignUpRequest() {
        return content.equals("signup");
    }
}
