/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua;

import vn.vm.baucua.data.entity.Bat;
import vn.vm.baucua.game.Game;

/**
 *
 * @author Ronin
 */
public class Test {

    public static void main(String[] args) {
        Game game = new Game(seconds -> {
            System.out.println(seconds);
        });
        game.start();
        Bat bat = new Bat();
        bat.val1 = 2;
        bat.val4 = 3;
        game.setBat(0, bat);
    }

}
