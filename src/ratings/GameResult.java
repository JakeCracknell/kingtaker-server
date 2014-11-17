package ratings;
import ;import users.UserAccountManager;

/**
 * Created by jc4512 on 17/11/14.
 */
public abstract class GameResult {
    public abstract void calculateNewRatings();
    public abstract String toString();

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

        return this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
