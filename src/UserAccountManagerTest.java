import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Date;

import static org.junit.Assert.*;

public class UserAccountManagerTest {

    //all testUser credentials are already input into database (many tests fail otherwise)
    private final String testUserNameInDB = "***TESTUSER***";
    private final String testUserNameNotInDB = "***NOTATESTUSER***";
    private final int testPasswordHash = 0;
    private final GameUser testUserRogue = new GameUser("bob",1900);
    private InetAddress testIP1;
    private InetAddress testIP2;
    private UserAccountManager uam;

    //all tests will fail if no database connection
    @Before
    public void setUp() throws Exception {
        uam = new UserAccountManager();
        testIP1 = InetAddress.getByAddress(new byte[]{8,8,8,8});
        testIP2 = InetAddress.getByAddress(new byte[]{8,8,4,4});
    }

    @Test
    public void testAuthenticateUserOK() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP1);
        assertNotNull(gameUser);

        assertTrue(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testAuthenticateUserTwice() throws Exception {
        GameUser gameUser1 = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP2);
        GameUser gameUser2 = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP1);

        //gameUser1 == gameUser2
        assertTrue(uam.checkUserIsAuthenticated(gameUser2));

        GameUser gameUser3 = uam.getUserByAddress(testIP1);
        assertNotNull(gameUser3);
        GameUser gameUser4 = uam.getUserByAddress(testIP2);
        assertNull(gameUser4);
    }

    @Test
    public void testAuthenticateUserBadName() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameNotInDB, testPasswordHash, testIP1);
        assertNull(gameUser);

        assertFalse(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testAuthenticateUserBadPassword() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameNotInDB, 1, testIP1);
        assertNull(gameUser);

        assertFalse(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testGetUserByAddressPositive() throws Exception {
        GameUser gameUser1 = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP1);
        GameUser gameUser2 = uam.getUserByAddress(testIP1);
        assertEquals(gameUser1, gameUser2);
    }

    @Test
    public void testGetUserByAddressNegative() throws Exception {
        uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP1);
        GameUser gameUser2 = uam.getUserByAddress(testIP2);
        assertNull(gameUser2);
    }

    @Test
    public void testCheckUserIsAuthenticatedNegative() throws Exception {
        UserAccountManager uam = new UserAccountManager();
        assertFalse(uam.checkUserIsAuthenticated(testUserRogue));
    }

    @Test
    public void testUnauthenticateUserPositive() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP1);

        uam.unauthenticateUser(gameUser);

        assertNull(uam.getUserByAddress(testIP1));
        assertFalse(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testUnauthenticateUserNegative() throws Exception {
        GameUser gameUser = uam.authenticateUser(testUserNameInDB, testPasswordHash, testIP1);

        uam.unauthenticateUser(testUserRogue);

        assertTrue(uam.checkUserIsAuthenticated(gameUser));
    }

    @Test
    public void testRegisterUserPositive() throws Exception {
        String uniqueTestUsername = "test" + System.currentTimeMillis();
        GameUser gameUser = uam.registerUser(uniqueTestUsername, testPasswordHash, testIP1);
        assertNotNull(gameUser);
        assertTrue(uam.checkUserIsAuthenticated(gameUser));
        assertEquals(uam.getUserByAddress(testIP1), gameUser);
    }

    @Test
    public void testRegisterUserNegative() throws Exception {
        String uniqueTestUsername = "test" + System.currentTimeMillis();
        GameUser gameUser1 = uam.registerUser(uniqueTestUsername, testPasswordHash, testIP1);
        assertNotNull(gameUser1);
        assertTrue(uam.checkUserIsAuthenticated(gameUser1));
        assertEquals(uam.getUserByAddress(testIP1), gameUser1);
        GameUser gameUser2 = uam.registerUser(uniqueTestUsername, testPasswordHash, testIP2);
        assertNull(gameUser2);
        assertFalse(uam.checkUserIsAuthenticated(gameUser2));
        assertNotEquals(uam.getUserByAddress(testIP1), gameUser2);

        //Also test this does not impact the first account-maker
        assertNotNull(gameUser1);
        assertTrue(uam.checkUserIsAuthenticated(gameUser1));
        assertEquals(uam.getUserByAddress(testIP1), gameUser1);
    }

    @Test
    public void testCheckUsernameTooShort() throws Exception {
        assertFalse(uam.checkUsernameIsAcceptable(null));
        assertFalse(uam.checkUsernameIsAcceptable(""));
        assertFalse(uam.checkUsernameIsAcceptable("0"));
        assertFalse(uam.checkUsernameIsAcceptable("00"));
    }

    @Test
    public void testCheckUsernameTooLong() throws Exception {
        assertFalse(uam.checkUsernameIsAcceptable("012345678901234567890"));
    }

    @Test
    public void testCheckUsernameBadCharacters() throws Exception {
        char[] badChars = new char[]{'%','!','£','@','-','#', ' ', '\'', ',' ,'\n','\t'};

        for (int i = 0; i < badChars.length; i++) {
            assertFalse(uam.checkUsernameIsAcceptable("aaa" + badChars[i]));
        }
    }

    @Test
    public void testCheckUsernamePositive() throws Exception {
        assertTrue(uam.checkUsernameIsAcceptable("012"));
        assertTrue(uam.checkUsernameIsAcceptable("01234567890123456789"));
        assertTrue(uam.checkUsernameIsAcceptable("ABCabc_012"));
    }
}