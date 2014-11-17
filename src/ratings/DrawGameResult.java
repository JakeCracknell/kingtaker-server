package ratings;

import users.GameUser;

/**
 * Created by jc4512 on 17/11/14.
 */
public class DrawGameResult extends GameResult {
    private GameUser user1;
    private GameUser user2;

    public DrawGameResult(GameUser user1, GameUser user2) {
        //To help with equality tests, user1 comes before user2 alphabetically.
        if (user1.getName().compareTo(user2.getName()) > 0) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    @Override
    public void calculateNewRatings() {

    }

    @Override
    public String toString() {
        return user1.getName() + user2.getName();
    }
}
