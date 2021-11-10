package vn.vm.baucua.data.entity;

public class RoomInfo {

    public int quantity;
    public String name;
    public int id;

    public RoomInfo(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }
}
