package users;

import org.junit.Before;
import org.junit.Test;
import users.GameUser;

import java.util.Date;

import static org.junit.Assert.*;

public class GameUserTest {

    private final int testWaitDurationMs = 100;

    private final String testName = "bob";
    private final String testPassword = "password1";
    private final int testRating = 1900;
    private final int testPendingRating = 500;
    private final Date testJoiningDate = new Date();
    private GameUser testUser;

    @Before
    public void setUp() throws Exception {
        testUser = new GameUser(testName, testRating,
                getPasswordHash(testName,testPassword), testJoiningDate);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(testName, testUser.getName());
    }

    @Test
    public void testGetRating() throws Exception {
        assertEquals(testRating, testUser.getRating());
    }

    @Test
    public void testGetHashedPassword() throws Exception {
        assertEquals(getPasswordHash(testName,testPassword), testUser.getHashedPassword());
    }

    @Test
    public void testGetPendingRating() throws Exception {
        testUser.setPendingRating(testPendingRating);
        assertEquals(testRating, testUser.getRating());
        assertEquals(testPendingRating, testUser.getPendingRating());
        testUser.setRating();
        assertEquals(testPendingRating, testUser.getRating());
        testUser.setRating(testRating);
        assertEquals(testRating, testUser.getRating());
    }

    @Test
    public void testGetDateJoined() throws Exception {
        Thread.sleep(testWaitDurationMs);
        long timeMS = (new Date()).getTime() - testUser.getDateJoined().getTime();
        assertTrue(timeMS >= testWaitDurationMs && timeMS < 10000);
    }

    @Test
    public void testGetTimeSinceAuthenticated() throws Exception {
        Thread.sleep(testWaitDurationMs);
        long timeMS = testUser.getTimeSinceAuthenticated();
        assertTrue(timeMS >= testWaitDurationMs && timeMS < 10000);
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(
                new GameUser(testName, testRating,
                        getPasswordHash(testName,testPassword), testJoiningDate),
                new GameUser(testName, testRating,
                        getPasswordHash(testName,testPassword), testJoiningDate));

    }



    private int getPasswordHash(String username, String plaintextPassword) {
        return (username + plaintextPassword).hashCode();
    }

}