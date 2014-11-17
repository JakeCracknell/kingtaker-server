import org.junit.Test;
import users.GameUser;

import java.net.Socket;

import static org.junit.Assert.*;

public class GameLobbyTest {

    private GameUser testUser = new GameUser("bob",1900);

    @Test
    public void testToStringEmpty() throws Exception {
        GameLobby lobby = new GameLobby();
        assertEquals("",lobby.toString());
    }

    @Test
    public void testToStringOne() throws Exception {
        GameLobby lobby = new GameLobby();
        lobby.addGame(getTestGame());
        String regex = "\\{\\d+\\.\\d+\\.\\d+\\.\\d+,\\w+,\\d+,\\d+\\}";
        assertTrue(lobby.toString().matches(regex));
    }

    @Test
    public void testToStringMany() throws Exception {
        GameLobby lobby = new GameLobby();

        for (int i = 0; i < 100; i++) {
            lobby.addGame(getTestGame());
        }

        String regex = "\\{\\d+\\.\\d+\\.\\d+\\.\\d+,\\w+,\\d+,\\d+\\}(,\\{\\d+\\.\\d+\\.\\d+\\.\\d+,\\w+,\\d+,\\d+\\})*";
        assertTrue(lobby.toString().matches(regex));
    }

    @Test
    public void testRemoveByUser() throws Exception {
        GameLobby lobby = new GameLobby();
        lobby.addGame(getTestGame());
        lobby.removeByUser(testUser);

        assertEquals("", lobby.toString());
    }

    private Game getTestGame() throws Exception {
        Socket testSocket = new Socket("google.com", 80);

        return new Game(testSocket,testUser,1);
    }

//    //Test will fail unless the refresh and timeout constants are set below 300
//    @Test
//    public void testGameTimeout() throws Exception {
//        GameLobby lobby = new GameLobby();
//        lobby.addGame(getTestGame());
//        String regex = "\\{\\d+\\.\\d+\\.\\d+\\.\\d+,\\w+,\\d+,\\d+\\}";
//        assertTrue(lobby.toString().matches(regex));
//
//        Thread.sleep(1000);
//
//        assertTrue(lobby.toString().equals(""));
//    }

}