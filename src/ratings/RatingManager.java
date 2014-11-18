package ratings;

import users.GameUser;
import users.UserAccountManager;

import java.util.LinkedList;

/**
 * Created by jc4512 on 17/11/14.
 */
public class RatingManager {
    public boolean WRITE_LOCK = false;

    private UserAccountManager userAccountManager;

    private LinkedList<GameResult> pendingResults = new LinkedList<GameResult>();

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

        result.calculateNewRatings();

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
            pendingResults.add(result);
        }

        return reporter.getPendingRating();
    }

    //The outcome of the game from the point of view of the reporter.
    public enum GameResultType {
        WIN, LOSS, DRAW
    }
}
