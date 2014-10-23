import java.util.ArrayList;

/**
 * Created by jc4512 on 17/10/14.
 */
public class GameLobby {
    private static final char DELIMINATOR = ',';

    private ArrayList<Game> games = new ArrayList<Game>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Game game : games) {
            sb.append(game.toString() + DELIMINATOR);
        }
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
}
