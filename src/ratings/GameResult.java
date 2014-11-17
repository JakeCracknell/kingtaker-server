package ratings;
import ;import users.UserAccountManager;

/**
 * Created by jc4512 on 17/11/14.
 */
public abstract class GameResult {
    public abstract void calculateNewRatings();

    public void process(UserAccountManager userAccountManager) {
        int winnerRating;
        int loserRating;
        userAccountManager.updateUserRatings(winner, winnerRating, loser, loserRating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameResult that = (GameResult) o;

        if (loser != null ? !loser.equals(that.loser) : that.loser != null) return false;
        if (winner != null ? !winner.equals(that.winner) : that.winner != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = winner != null ? winner.hashCode() : 0;
        result = 31 * result + (loser != null ? loser.hashCode() : 0);
        return result;
    }
}
