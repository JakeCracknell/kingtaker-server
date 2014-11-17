import users.GameUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jc4512 on 17/10/14.
 */
public class GameLobby implements Runnable {
    private final int OPEN_GAME_TIMEOUT_MS = 1000 * 60 * 10; //10 minutes
    private final int REMOVE_OLD_GAMES_INTERVAL_MS = 1000 * 60; //1 minutes

    private ArrayList<Game> games = new ArrayList<Game>();

    public GameLobby() {
        Thread gameLobbyThread = new Thread(this);
        gameLobbyThread.start();
    }

    @Override
    public synchronized String toString() {
        if (games.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Game game : games) {
            sb.append(game.toString() + ",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public void removeByUser(GameUser gameUser) {
        for (Game game : games) {
            if (game.getUser() == gameUser) {
                games.remove(game);
                return;
            }
        }
    }

    public synchronized void addGame(Game game) {
        games.add(game);
    }

    private synchronized void removeOldGames() {
        for (int i = 0; i < games.size(); i++) {
            if (((new Date()).getTime() -
                    games.get(i).getCreationDate().getTime())
                    > OPEN_GAME_TIMEOUT_MS) {
                games.remove(i);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            removeOldGames();
            try {
                Thread.sleep(REMOVE_OLD_GAMES_INTERVAL_MS);
            } catch (InterruptedException e) {}
        }
    }

}
