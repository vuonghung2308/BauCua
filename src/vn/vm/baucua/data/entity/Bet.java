package vn.vm.baucua.data.entity;

public class Bet {

    public int val1;
    public int val2;
    public int val3;
    public int val4;
    public int val5;
    public int val6;

    public int[] toArray() {
        return new int[]{
            0, val1, val2, val3,
            val4, val5, val6
        };
    }

    public int getSum() {
        return val1 + val2 + val3
            + val4 + val5 + val6;
    }
}
