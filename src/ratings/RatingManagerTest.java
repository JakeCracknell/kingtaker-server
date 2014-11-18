package ratings;

import org.junit.Before;
import org.junit.Test;
import users.GameUser;
import users.UserAccountManager;

import java.net.InetAddress;

import static org.junit.Assert.*;

public class RatingManagerTest {
    private UserAccountManager uam = new UserAccountManager();
    private RatingManager rm = new RatingManager(uam);
    private String testUserName1;
    private String testUserName2;

    @Before
    public void setUp() throws Exception {
        testUserName1 = getTestUsername();
        testUserName2 = getTestUsername() + "x";
        InetAddress testIP1 = InetAddress.getByAddress(new byte[]{8, 8, 8, 8});
        InetAddress testIP2 = InetAddress.getByAddress(new byte[]{8, 8, 4, 4});
        GameUser user1 = uam.registerUser(testUserName1, 0, testIP1);
        GameUser user2 = uam.registerUser(testUserName2, 0, testIP2);

        assert(uam.checkUserIsAuthenticated(user1));
        assert(uam.checkUserIsAuthenticated(user2));
    }

    public String getTestUsername() {
        return "test" + System.currentTimeMillis();
    }

    // Used for tests requiring more than 2 users. Thread sleep is used to
    // ensure the username is unique.
    public GameUser getNewAuthenticatedUser() throws Exception {
        Thread.sleep(2);
        return uam.registerUser(getTestUsername(), 0, InetAddress.getByAddress(new byte[]{8, 8, 8, 8}));
    }

    @Test
    public void testSubmitRatingReturnResultWin() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //winner and first to report
        GameUser user2 = uam.getUserByName(testUserName2); //loser and last to report
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.WIN);

        //Check pending rating returned is better than previous rating
        assertTrue(user1.getRating() < rating1);
        assertEquals(user1.getPendingRating(), rating1);
    }

    @Test
    public void testSubmitRatingReturnResultLoss() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //winner and first to report
        GameUser user2 = uam.getUserByName(testUserName2); //loser and last to report
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.LOSS);

        //Check pending rating returned is worse than previous rating
        assertTrue(user1.getRating() > rating1);
        assertEquals(user1.getPendingRating(), rating1);
    }

    @Test
    public void testSubmitRatingReturnResultDrawIncrease() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //first to report
        GameUser user2 = uam.getUserByName(testUserName2); //last to report
        user1.setRating(user2.getRating() * 2); //user1 > user2
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.DRAW);

        //Check pending rating returned is worse than previous rating
        assertTrue(user1.getRating() > rating1);
        assertEquals(user1.getPendingRating(), rating1);
    }

    @Test
    public void testSubmitRatingReturnResultDrawDecrease() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //first to report
        GameUser user2 = uam.getUserByName(testUserName2); //last to report
        user1.setRating(user2.getRating() / 2); //user1 > user2
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.DRAW);

        //Check pending rating returned is better than previous rating
        assertTrue(user1.getRating() < rating1);
        assertEquals(user1.getPendingRating(), rating1);
    }

    @Test
    public void testSubmitRatingReturnResultDrawEqual() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //winner and first to report
        GameUser user2 = uam.getUserByName(testUserName2); //loser and last to report

        assertEquals(user1.getRating(), user2.getRating());
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.DRAW);
        int rating2 = rm.submitRating(user2, user1, RatingManager.GameResultType.DRAW);

        //Check pending ratings have not changed following the DRAW
        assertEquals(user1.getPendingRating(), user2.getPendingRating());
        assertEquals(user1.getRating(), user1.getPendingRating());
    }

    @Test
    public void testSubmitRatingWinLossPositive() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //winner and first to report
        GameUser user2 = uam.getUserByName(testUserName2); //loser and last to report
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.WIN);
        int rating2 = rm.submitRating(user2, user1, RatingManager.GameResultType.LOSS);
        assertTrue(rating1 > rating2);

        //Supposed to be processed, pending ratings confirmed.

        assertEquals(rating1, user1.getRating());
        assertEquals(rating2, user2.getRating());
    }

    @Test
    public void testSubmitRatingDrawPositive() throws Exception {
        GameUser user1 = uam.getUserByName(testUserName1); //winner and first to report
        GameUser user2 = uam.getUserByName(testUserName2); //loser and last to report
        user2.setRating(user1.getRating() / 2);
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.DRAW);
        int rating2 = rm.submitRating(user2, user1, RatingManager.GameResultType.DRAW);
        assertTrue(rating1 > rating2);

        //Supposed to be processed, pending ratings confirmed.

        assertEquals(rating1, user1.getRating());
        assertEquals(rating2, user2.getRating());
    }

    @Test
    public void testSubmitRatingDisagreement() throws Exception {
        //Both users claim to have won the game
        GameUser user1 = uam.getUserByName(testUserName1);
        GameUser user2 = uam.getUserByName(testUserName2);
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.WIN);
        int rating2 = rm.submitRating(user2, user1, RatingManager.GameResultType.WIN);

        //Not supposed to be processed.

        assertNotEquals(rating1, user1.getRating());
        assertNotEquals(rating2, user2.getRating());
    }

    @Test
    public void testSubmitRatingOnePlayer() throws Exception {
        //Both users claim to have won the game
        GameUser user1 = uam.getUserByName(testUserName1);
        GameUser user2 = uam.getUserByName(testUserName2);
        int rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.WIN);

        //Not supposed to be processed.
        assertNotEquals(rating1, user1.getRating());
        rating1 = rm.submitRating(user1, user2, RatingManager.GameResultType.WIN);

        //Still not supposed to be processed.
        assertNotEquals(rating1, user1.getRating());
    }

    @Test
    public void testMultiGameInterleaved() throws Exception {
        GameUser userA1 = getNewAuthenticatedUser();
        GameUser userA2 = getNewAuthenticatedUser();
        GameUser userB1 = getNewAuthenticatedUser(); //this user will not submit
        GameUser userB2 = getNewAuthenticatedUser();
        GameUser userC1 = getNewAuthenticatedUser();
        GameUser userC2 = getNewAuthenticatedUser();
        int ratingA1 = rm.submitRating(userA1, userA2, RatingManager.GameResultType.WIN);
        int ratingB2 = rm.submitRating(userB2, userB1, RatingManager.GameResultType.WIN);
        int ratingC1 = rm.submitRating(userC1, userC2, RatingManager.GameResultType.LOSS);
        int ratingA2 = rm.submitRating(userA2, userA1, RatingManager.GameResultType.LOSS);
        int ratingC2 = rm.submitRating(userC2, userC1, RatingManager.GameResultType.WIN);

        //Ratings should have changed accordingly.
        //Easily comparable, as all users start as 1000.
        assertTrue(ratingA1 > ratingA2);
        assertTrue(ratingC1 < ratingC2);

        //Supposed to be processed, pending ratings confirmed.
        assertEquals(ratingA1, userA1.getRating());
        assertEquals(ratingA2, userA2.getRating());
        assertNotEquals(ratingB2, userB2.getRating());
        assertEquals(ratingC1, userC1.getRating());
        assertEquals(ratingC2, userC2.getRating());
    }
}