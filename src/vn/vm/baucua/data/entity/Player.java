package vn.vm.baucua.data.entity;

public class Player {

    public int id;
    public String username;
    public String fullname;
    public Long balance;

    public Player(
            Integer id, String username,
            String fullname, Long balance
    ) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.balance = balance;
    }

    public Player() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

}
