package ratings;
import users.GameUser;
import users.UserAccountManager;

import java.util.ArrayList;

/**
 * Created by jc4512 on 17/11/14.
 */
public abstract class GameResult {
    protected final int DEVELOPMENT_COEFFICIENT = 32;

    public abstract void calculateNewRatings();
    public abstract ArrayList<GameUser> getUsers();
    public abstract String toString();

    public void process(UserAccountManager userAccountManager) {
        // For each user associated with this game result, turn their pending
        // rating into their actual rating and make this change on the DB also.
        for (GameUser gameUser : getUsers()) {
            gameUser.setRating();
            userAccountManager.updateUser(gameUser);
        }
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
