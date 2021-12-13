/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vm.baucua.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.util.Pair;
import vn.vm.baucua.database.dao.ForgotKeyDao;
/**
 *
 * @author User
 */
public class KeyForgotUtil {
    
    private ForgotKeyDao dao;

    public KeyForgotUtil() {
        this.dao = new ForgotKeyDao();
    }
    
    public int checkKey(String username, String key){
            long currentTime = System.currentTimeMillis();
            long tmp = dao.checkKey(username, key);
            if(tmp == -1){
                return 1201;
            }
            if((tmp + 5*60*1000) >= currentTime ){
                dao.deleteKey(username);
                return 200;
            }
            return 1200;
    }
    public String genKey(String username){
        System.out.println("---------------------------------------------------------gen key");
        Random r = new Random();
        int tmp = r.nextInt(990000)+100000;
        long time = System.currentTimeMillis() + 5*60*1000;
        if(dao.checkUser(username)){
            dao.updateKey(username, (""+tmp));
            System.out.println("---------------------------------------------------------update key");
        }else{
            dao.insertKey(username, (""+tmp));
            System.out.println("-----------------------------------------------------------insert key");
        }
        return tmp + "";
    }

}
