package vn.vm.baucua.util;

import java.util.Random;
import vn.vm.baucua.database.dao.ForgotKeyDao;

public class KeyForgotUtil {

    private final ForgotKeyDao dao;

    public KeyForgotUtil() {
        this.dao = new ForgotKeyDao();
    }

    public int checkKey(String username, String key) {
        long currentTime = System.currentTimeMillis();
        long tmp = dao.checkKey(username, key);
        if (tmp == -1) {
            return 1201;
        }
        if ((tmp + 5 * 60 * 1000) >= currentTime) {
            dao.deleteKey(username);
            return 200;
        }
        return 1200;
    }

    public String genKey(String username) {
        Random r = new Random();
        int tmp = r.nextInt(990000) + 100000;
        if (dao.checkUser(username)) {
            dao.updateKey(username, ("" + tmp));
        } else {
            dao.insertKey(username, ("" + tmp));
        }
        return tmp + "";
    }

}
