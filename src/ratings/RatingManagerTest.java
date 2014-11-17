package ratings;

import org.junit.Before;
import org.junit.Test;
import users.GameUser;
import users.UserAccountManager;

import java.net.InetAddress;

import static org.junit.Assert.*;

public class RatingManagerTest {
    private UserAccountManager uam = new UserAccountManager();
    private String testUserName1;
    private String testUserName2;

    @Before
    public void setUp() throws Exception {
        testUserName1 = getTestUsername();
        testUserName2 = getTestUsername() + 1;
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

    @Test
    public void testSubmitRatingWinLossPositive() throws Exception {

    }
}