package vn.vm.baucua.data.entity;

public class Player {

    public int id;
    public String username;
    public String fullname;
    public boolean status;
    public long difference;
    public long balance;
    public Bet bet;

    public Player(User user) {
        id = user.id;
        username = user.username;
        fullname = user.fullname;
        balance = user.balance;
        bet = new Bet();
        difference = 0;
    }
}
