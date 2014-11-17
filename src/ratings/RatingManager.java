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

    //Submits a request to change two player's ratings.
    //Returns the submitter's new rating, pending approval.
    public int submitRating(GameUser reporter, GameUser opponent, GameResultType outcome) {
        GameResult result = null;
        switch (outcome) {
            case WIN:
                result = new WinLossGameResult(reporter, opponent);
                break;
            case LOSS:
                result = new WinLossGameResult(opponent, reporter);
                break;
            case DRAW:
                result = new DrawGameResult(reporter, opponent);
                break;
        }

        if (pendingResults.contains(result)) {
            pendingResults.remove(result);
            result.process(userAccountManager);
        } else {
            pendingResults.add(result);
        }
        return 0;
    }

    //The outcome of the game from the point of view of the reporter.
    public enum GameResultType {
        WIN, LOSS, DRAW
    }
}
