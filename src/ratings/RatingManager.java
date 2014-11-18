package ratings;

import users.GameUser;
import users.UserAccountManager;

import java.util.LinkedList;

/**
 * Created by jc4512 on 17/11/14.
 */
public class RatingManager {
    private UserAccountManager userAccountManager;

    //Used as a queue - oldest unconfirmed games are at the head and are
    //periodically removed, new unconfirmed games are added to the tail.
    private LinkedList<GameResult> pendingResults = new LinkedList<GameResult>();
    private final int PENDING_RESULT_TIMEOUT_MS = 20000;

    public RatingManager(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
    }

    //Submits a request to change two player's ratings.
    //Returns the submitter's new rating, pending approval.
    public int submitRating(GameUser reporter, GameUser opponent, GameResultType outcome) {
        GameResult result = null;
        switch (outcome) {
            case WIN:
                result = new WinLossGameResult(reporter, opponent, reporter);
                break;
            case LOSS:
                result = new WinLossGameResult(opponent, reporter, reporter);
                break;
            case DRAW:
                result = new DrawGameResult(reporter, opponent, reporter);
                break;
        }

        int indexOfPendingResult = pendingResults.indexOf(result);
        if (indexOfPendingResult >= 0) {
            GameResult pendingResult = pendingResults.get(indexOfPendingResult);
            if (pendingResult.firstReporter.equals(reporter)) {
                //User has sent request more than once, perhaps maliciously.
                //TODO: report
            } else {
                pendingResults.remove(indexOfPendingResult);
                result.process(userAccountManager);
            }
        } else {
            result.calculateNewRatings();
            pendingResults.addLast(result);
        }

        return reporter.getPendingRating();
    }

    //The outcome of the game from the point of view of the reporter.
    public enum GameResultType {
        WIN, LOSS, DRAW
    }
}
