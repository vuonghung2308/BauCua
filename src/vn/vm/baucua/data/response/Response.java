package vn.vm.baucua.data.response;

import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.util.StroUtils;

public class Response {

    public String content;
    public String data;
    public String message;
    public int code;

    public Response(int code, String content, String message) {
        this.content = content;
        this.message = message;
        this.code = code;
    }

    public Response(String content, Object data, int code) {
        this.data = StroUtils.toStro(data);
        this.content = content;
        this.code = code;
    }

    public Response(String content, Integer code) {
        this.content = content;
        this.code = code;
    }

    public static Response error(int code, Request request, String msg) {
        return new Response(code, request.content, msg);
    }

    public static Response success(Request request, Object data) {
        return new Response(request.content, data, 200);
    }

    public static Response success(String content, Object data) {
        return new Response(content, data, 200);
    }

    public static Response success(Request request) {
        return new Response(request.content, 200);
    }

    public static Response ping() {
        return new Response("ping", 200);
    }
}
