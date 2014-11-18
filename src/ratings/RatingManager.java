package ratings;

import users.GameUser;
import users.UserAccountManager;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by jc4512 on 17/11/14.
 */
public class RatingManager implements Runnable {
    private UserAccountManager userAccountManager;

    //Used as a queue - oldest unconfirmed games are at the head and are
    //periodically removed, new unconfirmed games are added to the tail.
    private LinkedList<GameResult> pendingResults = new LinkedList<GameResult>();
    private final int PENDING_RESULT_TIMEOUT_MS = 20000;
    private final int REMOVE_OLD_PENDING_RESULTS_INTERVAL_MS = 60000;

    public RatingManager(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
        Thread ratingManagerThread = new Thread(this);
        ratingManagerThread.start();
    }

    //Submits a request to change two player's ratings.
    //Returns the submitter's new rating, pending approval.
    public synchronized int submitRating(GameUser reporter, GameUser opponent, GameResultType outcome) {
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

    private synchronized void removeOldPendingResults() {
        for (int i = 0; i < pendingResults.size(); i++) {
            if (((new Date()).getTime() -
                    pendingResults.get(i).getDate().getTime())
                    > PENDING_RESULT_TIMEOUT_MS) {
                pendingResults.remove(i);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            removeOldPendingResults();
            try {
                Thread.sleep(REMOVE_OLD_PENDING_RESULTS_INTERVAL_MS);
            } catch (InterruptedException e) {}
        }
    }

    //The outcome of the game from the point of view of the reporter.
    public enum GameResultType {
        WIN, LOSS, DRAW
    }
}
