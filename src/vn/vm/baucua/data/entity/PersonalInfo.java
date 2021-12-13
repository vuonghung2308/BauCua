/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.vm.baucua.data.entity;

/**
 *
 * @author toank
 */
public class PersonalInfo {
    public String fullname;
    public String username;
    public String email;
    public long balance;
    public long total;
    public long win_number;
    public long lose_number;

    public PersonalInfo(String fullname, String username, String email, long balance, long total, long win_number, long lose_number) {
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.total = total;
        this.win_number = win_number;
        this.lose_number = lose_number;
    }

    public PersonalInfo() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getWin_number() {
        return win_number;
    }

    public void setWin_number(long win_number) {
        this.win_number = win_number;
    }

    public long getLose_number() {
        return lose_number;
    }

    public void setLose_number(long lose_number) {
        this.lose_number = lose_number;
    }
    
}
