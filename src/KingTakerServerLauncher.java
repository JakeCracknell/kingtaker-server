/**
 * Created by jc4512 on 23/10/14.
 */
public class KingTakerServerLauncher {

    //Run game server in separate thread.
    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        Thread gameServerThread = new Thread(gameServer);
        gameServerThread.start();
    }

}
