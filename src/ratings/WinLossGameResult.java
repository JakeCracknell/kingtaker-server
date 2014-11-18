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
        int winnerRating = winner.getRating();
        int loserRating = loser.getRating();

        winnerRating += 10;
        loserRating -= 10;

        winner.setPendingRating(winnerRating);
        loser.setPendingRating(loserRating);
    }

    @Override
    public String toString() {
        return winner.getName() + loser.getName();
    }
}
