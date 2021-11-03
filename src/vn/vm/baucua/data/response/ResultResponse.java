package vn.vm.baucua.data.response;

import java.util.List;
import vn.vm.baucua.data.entity.Player;
import vn.vm.baucua.data.entity.Result;

public class ResultResponse {

    public List<Player> players;
    public Result result;

    public ResultResponse(
            List<Player> players,
            Result resut
    ) {
        this.players = players;
        this.result = resut;
    }

}
