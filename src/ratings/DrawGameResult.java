package ratings;

import users.GameUser;

import java.util.ArrayList;

/**
 * Created by jc4512 on 17/11/14.
 */
public class DrawGameResult extends GameResult {
    private GameUser user1;
    private GameUser user2;

    public DrawGameResult(GameUser user1, GameUser user2, GameUser reporter) {
        //To help with equality tests, user1 comes before user2 alphabetically.
        if (user1.getName().compareTo(user2.getName()) > 0) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
        firstReporter = reporter;
    }

    @Override
    public void calculateNewRatings() {
        int user1Rating = user1.getRating();
        int user2Rating = user2.getRating();

        double user1Transformed = Math.pow(10, (user1Rating / 400));
        double user2Transformed = Math.pow(10, (user2Rating / 400));

        double user1Expected = user1Transformed / (user1Transformed + user2Transformed);
        double user2Expected = user2Transformed / (user1Transformed + user2Transformed);

        user1Rating += DEVELOPMENT_COEFFICIENT * (0.5 - user1Expected);
        user2Rating += DEVELOPMENT_COEFFICIENT * (0.5 - user2Expected);

        user1.setPendingRating(user1Rating);
        user2.setPendingRating(user2Rating);
    }

    @Override
    public ArrayList<GameUser> getUsers() {
        ArrayList<GameUser> list = new ArrayList<GameUser>();
        list.add(user1);
        list.add(user2);
        return list;
    }

    @Override
    public String toString() {
        return user1.getName() + user2.getName();
    }
}
