package vn.vm.baucua.data.entity;

public class Result {

    public int rs1;
    public int rs2;
    public int rs3;

    public Result(int[] res) {
        rs1 = res[0];
        rs2 = res[1];
        rs3 = res[2];
    }
}
