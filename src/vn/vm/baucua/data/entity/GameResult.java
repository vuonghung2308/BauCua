/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua.data.entity;

/**
 *
 * @author User
 */
public class GameResult {
    private long id;
    private long playerId;
    private int status;
    private long different;
    private long time;

    public GameResult(long id, long playerId, int status, long different, long time) {
        this.id = id;
        this.playerId = playerId;
        this.status = status;
        this.different = different;
        this.time = time;
    }
    
    
}
