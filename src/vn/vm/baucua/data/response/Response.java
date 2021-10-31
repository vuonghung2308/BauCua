package vn.vm.baucua.data.response;

import vn.vm.baucua.util.JsonUtils;

public class Response {

    public String content;
    public String data;
    public String code;

    public Response() {
    }

    public Response(String content) {
        this.content = content;
    }

    public Response(String content, Object data) {
        this.data = JsonUtils.toJson(data);
        this.content = content;
    }
    
    public Response(String content, Object data, int code) {
        this.data = JsonUtils.toJson(data);
        this.content = content;
        this.code = code+"";
    }

    public void setData(Object data) {
        this.data = JsonUtils.toJson(data);
    }
    
}
