package ratings;

import users.GameUser;

/**
 * Created by jc4512 on 17/11/14.
 */
public class WinLossGameResult extends GameResult {
    private GameUser winner;
    private GameUser loser;

    public WinLossGameResult(GameUser winner, GameUser loser) {
        this.winner = winner;
        this.loser = loser;
    }


    @Override
    public void calculateNewRatings() {

    }
}
