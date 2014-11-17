package ratings;

import users.GameUser;
import users.UserAccountManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jc4512 on 17/11/14.
 */
public class RatingManager {
    private final int DEVELOPMENT_COEFFICIENT = 40;

    private UserAccountManager userAccountManager;

    private Queue<GameResult> pendingResults = new ConcurrentLinkedQueue<GameResult>();

    public RatingManager(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
    }

    public void submitRating(GameUser reporter, GameUser opponent, GameResultType outcome) {
        GameResult result = new WinLossGameResult();
        result.winner = winner;
        result.loser = loser;

        if (pendingResults.contains(result)) {
            result.process();
        }

        pendingResults.add(result);
    }

    //The outcome of the game from the point of view of the reporter.
    public enum GameResultType {
        WIN, LOSS, DRAW
    }
}
