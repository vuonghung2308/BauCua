package vn.vm.baucua.data.entity;

public class ChatMessage {

    public int id;
    public String message;

    public ChatMessage(int id, String msg) {
        this.message = msg;
        this.id = id;
    }

    public ChatMessage() {
    }
}
