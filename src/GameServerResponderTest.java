import NetworkingCodes.ResponseCode;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class GameServerResponderTest {

    private final String getGameListPositiveResponseRegex = ResponseCode.OK + ResponseCode.DEL +
            "\\{\\d+\\.\\d+\\.\\d+\\.\\d+,\\w+,\\d+,\\d+\\}(,\\{\\d+\\.\\d+\\.\\d+\\.\\d+,\\w+,\\d+,\\d+\\})*";
    private GameServerResponder gsr;
    private Socket testSocketBBC; //uses bbc web server as arbitrary value
    private Socket testSocketGoogle; //uses google web server
    private String testPW = "0";
    private String testVID = "1";

    private String getTestUsername() {
        return "test" + System.currentTimeMillis();
    }
    
    @Before
    public void setUp() throws Exception {
        gsr = new GameServerResponder(new GameLobby(), new UserAccountManager());
        testSocketBBC = new Socket(InetAddress.getByName("bbc.co.uk"), 80);
        testSocketGoogle = new Socket(InetAddress.getByName("google.co.uk"), 80);
    }

    @Test
    public void testReportGameResult() throws Exception {
        //TODO
    }

    @Test
    public void testRegisterAccountPositive() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response = gsr.registerAccount(testSocketBBC, getTestUsername(), testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response);
    }

    @Test
    public void testRegisterAccountExists() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response);
        String response2 = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.BAD_LOGIN + "", response2);
    }

    @Test
    public void testRegisterAccountBadName() throws Exception {
        String[] badNames = new String[]{"","ab","a--a","012345678901234567890"};
        for (String name : badNames) {
            String response = gsr.registerAccount(testSocketBBC, name, testPW);
            assertEquals(ResponseCode.REFUSED + "", response);
        }
    }

    @Test
    public void testAuthenticateUserNegative() throws Exception {
        String response = gsr.authenticateUser(testSocketBBC, "not_a_user", testPW);
        assertEquals(ResponseCode.BAD_LOGIN + "", response);
    }
    @Test
    public void testAuthenticateUserPositive() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response);
    }

    @Test
    public void testReportPlayer() throws Exception {
        //TODO
    }


    @Test
    public void testCreateGamePositive() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response1 = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response1);

        String response2 = gsr.createGame(testSocketBBC, testVID);
        assertEquals(ResponseCode.OK + "", response2);
    }

    @Test
    public void testCreateGameNotLoggedIn() throws Exception {
        String response = gsr.createGame(testSocketBBC, testPW);
        assertEquals(ResponseCode.BAD_LOGIN + "", response);
    }

    @Test
    public void testGetGameListEmpty() throws Exception {
        String response = gsr.getGameList();
        assertEquals(ResponseCode.EMPTY + "", response);
    }

    @Test
    public void testGetGameListNonEmpty() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response1 = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response1);

        //Creating multiple games - technically not expected by same user/socket.
        for (int i = 0; i < 10; i++) {
            String response2 = gsr.createGame(testSocketBBC, testVID);
            assertEquals(ResponseCode.OK + "", response2);
        }

        String response3 = gsr.getGameList();
        assertTrue(response3.matches(getGameListPositiveResponseRegex));
    }

    @Test
    //Creates account X
    //Creates game G
    //Gets game list, sees game G
    //Removes game G
    //Gets game list, gets EMPTY code
    //Logs in again successfully from the same IP
    public void gameServerSystemTest1() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response1 = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response1);

        String response2 = gsr.createGame(testSocketBBC, testVID);
        assertEquals(ResponseCode.OK + "", response2);

        String response3 = gsr.getGameList();
        assertTrue(response3.matches(getGameListPositiveResponseRegex));

        gsr.removeGame(testSocketBBC);

        String response4 = gsr.getGameList();
        assertEquals(ResponseCode.EMPTY + "", response4);

        String response5 = gsr.authenticateUser(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response5);
    }

    @Test
    //BBC creates account X and is authenticated
    //Google logs X in.
    //BBC cannot create a new game (as has been logged out)
    //Google can create a new game.
    //BBC sees this game when requesting the lobby
    public void gameServerSystemTest2() throws Exception {
        String uniqueTestUsername = getTestUsername();
        String response1 = gsr.registerAccount(testSocketBBC, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response1);

        String response2 = gsr.authenticateUser(testSocketGoogle, uniqueTestUsername, testPW);
        assertEquals(ResponseCode.OK + ResponseCode.DEL + GameUser.DEFAULT_USER_RATING, response2);

        String response3 = gsr.createGame(testSocketBBC, testVID);
        assertEquals(ResponseCode.BAD_LOGIN + "", response3);

        String response4 = gsr.getGameList();
        assertEquals(ResponseCode.EMPTY + "", response4);

        String response5 = gsr.createGame(testSocketGoogle, testVID);
        assertEquals(ResponseCode.OK + "", response5);

        String response6 = gsr.getGameList();
        assertTrue(response6.matches(getGameListPositiveResponseRegex));
    }
}