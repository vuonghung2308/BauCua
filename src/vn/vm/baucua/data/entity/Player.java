package vn.vm.baucua.data.entity;

public class Player {

    public int id;
    public String username;
    public String fullname;
    public boolean status;
    public Long difference;
    public Long balance;
    public Bat bat;

    public Player(User user) {
        id = user.id;
        username = user.username;
        fullname = user.fullname;
        balance = user.balance;
        bat = new Bat();
    }
}
