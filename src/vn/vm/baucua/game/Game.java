package vn.vm.baucua.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import vn.vm.baucua.data.entity.Result;
import vn.vm.baucua.data.entity.Bet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.data.response.ResultResponse;
import vn.vm.baucua.database.dao.UserDao;

public class Game {

    private final HashMap<Integer, Player> players;
    private final UserDao userDao;
    private final Callback cb;
    public boolean isStarted;
    private Timer timer;
    private Result result;
    private Integer duration;
    private Task task;

    public synchronized Integer getDuration() {
        return duration;
    }

    public synchronized void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Game(Callback callback) {
        players = new HashMap<>();
        userDao = new UserDao();
        isStarted = false;
        cb = callback;
    }

    public void start() {
        isStarted = true;
        timer = new Timer();
        task = new Task();
        duration = 20;
        timer.schedule(task, 0, 1000);
        players.forEach((id, player) -> {
            player.difference = 0L;
            player.bet = new Bet();
        });
    }

    public void addPlayer(User user) {
        Player player = new Player(user);
        players.put(user.id, player);
    }

    public void addPlayer(User user, boolean status) {
        Player player = new Player(user);
        player.status = status;
        players.put(user.id, player);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public boolean setBat(int id, Bet bat) {
        Player player = players.get(id);
        long amount = bat.getSum() * 10000L;
        if (player.balance >= amount) {
            player.bet = bat;
            return true;
        }
        return false;
    }

    public long getDifference(int[] res, int[] bat) {
        int[] item = {0, 0, 0, 0, 0, 0, 0};
        item[res[0]]++;
        item[res[1]]++;
        item[res[2]]++;
        long coin = 0;
        for (int i = 0; i <= 6; i++) {
            if (item[i] > 0) {
                coin += 10000 * bat[i] * item[i];
            } else {
                coin -= 10000 * bat[i];
            }
        }
        return coin;
    }

    private int[] makeResult() {
        Random random = new Random();
        int value_1 = Math.abs(random.nextInt()) % 6 + 1;
        int value_2 = Math.abs(random.nextInt()) % 6 + 1;
        int value_3 = Math.abs(random.nextInt()) % 6 + 1;
        return new int[]{value_1, value_2, value_3};
    }

    public Response getResultResponse() {
        ResultResponse data = new ResultResponse(
                getPlayers(), result
        );
        return Response.success("game_result", data);
    }

    public void play() {
        int[] res = makeResult();
        Date now = Calendar.getInstance().getTime();
        result = new Result(res);
        players.forEach((id, player) -> {
            long difference = getDifference(
                    res, player.bet.toArray()
            );
            player.difference = difference;
            player.balance += difference;
            int status = difference > 0 ? 1 : difference == 0 ? 0 : -1;
            userDao.setBalance(
                    id, player.balance,
                    status, difference, now
            );
        });
    }

    public boolean canPlay(int hostId) {
        boolean canPlay = true;
        if (!players.values().stream().filter(
                player -> player.id != hostId
        ).noneMatch(
                player -> player.status == false
        )) {
            return false;
        }
        return canPlay;
    }

    public boolean setReady(int id) {
        Player player = players.get(id);
        if (player.status == false) {
            player.status = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean setUnready(int id) {
        Player player = players.get(id);
        if (player.status == true) {
            player.status = false;
            return true;
        } else {
            return false;
        }
    }

    public void remove(int id) {
        players.remove(id);
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            if (getDuration() == 1) {
                play();
            }
            if (getDuration() == 0) {
                isStarted = false;
                players.values().forEach(p -> {
                    p.status = false;
                });
                cb.doSomeThing(getDuration());
            }
            if (getDuration() == -1) {
                cb.doSomeThing(getDuration());
                timer.cancel();
            } else {
                cb.doSomeThing(getDuration());
                setDuration(getDuration() - 1);
            }
        }
    }
}
