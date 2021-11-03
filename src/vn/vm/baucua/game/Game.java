package vn.vm.baucua.game;

import java.util.ArrayList;
import vn.vm.baucua.data.entity.Result;
import vn.vm.baucua.data.entity.Bat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.data.response.ResultResponse;
import vn.vm.baucua.database.dao.UserrDao;

public class Game {

    private final HashMap<Integer, Player> players;
    private final UserrDao userDao;
    private final Callback cb;
    private final Task task;
    private Timer timer;
    private Result result;
    private int duration;
    public boolean isStarted;

    public Game(Callback callback) {
        players = new HashMap<>();
        userDao = new UserrDao();
        task = new Task();
        isStarted = false;
        cb = callback;
    }

    public void start() {
        isStarted = true;
        timer = new Timer();
        duration = 15;
        timer.schedule(task, 0, 1000);
        players.forEach((id, player) -> {
            player.difference = null;
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

    public boolean setBat(int id, Bat bat) {
        Player player = players.get(id);
        long amount = bat.getSum() * 10000L;
        if (player.balance >= amount) {
            player.bat = bat;
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
        result = new Result(res);
        players.forEach((id, player) -> {
            long difference = getDifference(
                    res, player.bat.toArray()
            );
            player.difference = difference;
            player.balance += difference;
            userDao.setBalance(id, player.balance);
        });
    }

    public boolean canPlay() {
        return players.values().stream().noneMatch((player)
                -> (player.status == false)
        );
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

    class Task extends TimerTask {

        @Override
        public void run() {
            if (duration == 10) {
                play();
            }
            if (duration == 0) {
                timer.cancel();
                isStarted = false;
            }
            cb.doSomeThing(duration);
            duration--;
        }
    }

}
