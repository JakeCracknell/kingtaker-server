package ratings;

import users.GameUser;

import java.util.ArrayList;

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

        double winnerTransformed = Math.pow(10, (winnerRating / 400));
        double loserTransformed = Math.pow(10, (loserRating / 400));

        double winnerExpected = winnerTransformed / (winnerTransformed + loserTransformed);
        double loserExpected = loserTransformed / (winnerTransformed + loserTransformed);

        winnerRating += DEVELOPMENT_COEFFICIENT * (1 - winnerExpected);
        loserRating += DEVELOPMENT_COEFFICIENT * (0 - loserExpected);

        winner.setPendingRating(winnerRating);
        loser.setPendingRating(loserRating);
    }

    @Override
    public ArrayList<GameUser> getUsers() {
        ArrayList<GameUser> list = new ArrayList<GameUser>();
        list.add(winner);
        list.add(loser);
        return list;
    }

    @Override
    public String toString() {
        return winner.getName() + loser.getName();
    }
}
