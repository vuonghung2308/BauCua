package vn.vm.baucua.data.entity;

public class User {

    public int id;
    public String username;
    public String fullname;
    public Long balance;

    public User() {
    }

    public User(
            Integer id, String username,
            String fullname, Long balance
    ) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.balance = balance;
    }
}
