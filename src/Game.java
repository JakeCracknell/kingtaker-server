import java.net.Socket;
import java.util.Date;

/**
 * Created by jc4512 on 17/10/14.
 */
public class Game {
    private Socket socket;
    private GameUser user;
    private int variantID;
    private Date creationDate;

    public Game(Socket socket, GameUser user, int variantID) {
        this.socket = socket;
        this.user = user;
        this.variantID = variantID;
        creationDate = new Date();
    }

    public Socket getSocket() {
        return socket;
    }

    public GameUser getUser() {
        return user;
    }

    public int getVariantID() {
        return variantID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "{" + socket.getInetAddress().getHostAddress() +
                "," + user.getName() +
                "," + user.getRating() +
                "," + variantID +
                '}';
    }
}
