import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

public class UserAccountManagerTest {

    //input into database
    private final String testUserNameInDB = "***TESTUSER***";
    private final String testUserNameNotInDB = "***NOTATESTUSER***";
    private final int testPasswordHash = 0;
    private InetAddress testIP;
    private UserAccountManager uam;

    //all tests will fail if no database connection
    @Before
    public void setUp() throws Exception {
        uam = new UserAccountManager();
        testIP = InetAddress.getByAddress(new byte[]{8,8,8,8});
    }

    @Test
    public void testAuthenticateUserOK() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP);
        assertNotNull(gameUser);

        assertTrue(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testAuthenticateUserTwice() throws Exception {
        InetAddress testIP2 = InetAddress.getByAddress(new byte[]{8,8,4,4});

        GameUser gameUser1 = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP2);
        GameUser gameUser2 = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP);

        //gameUser1 == gameUser2
        assertTrue(uam.checkUserIsAuthenticated(gameUser2));

        GameUser gameUser3 = uam.getUserByAddress(testIP);
        assertNotNull(gameUser3);
        GameUser gameUser4 = uam.getUserByAddress(testIP2);
        assertNull(gameUser4);
    }

    @Test
    public void testAuthenticateUserBadName() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameNotInDB, testPasswordHash, testIP);
        assertNull(gameUser);

        assertFalse(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testAuthenticateUserBadPassword() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameNotInDB, 1, testIP);
        assertNull(gameUser);

        assertFalse(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testGetUserByAddress() throws Exception {

    }

    @Test
    public void testCheckUserIsAuthenticatedNegative() throws Exception {
        UserAccountManager uam = new UserAccountManager();
        assertFalse(uam.checkUserIsAuthenticated(new GameUser("bob",1900)));
    }

    @Test
    public void testUnauthenticateUser() throws Exception {

    }
}