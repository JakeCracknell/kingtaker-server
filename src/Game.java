import java.net.Socket;
import java.util.Date;

/**
 * Created by jc4512 on 17/10/14.
 */
public class Game {
    private Socket socket;
    private String username;
    private int rating;
    private int variantID;
    private Date creationDate;

    public Game(Socket socket, String username, int rating, int variantID) {
        this.socket = socket;
        this.username = username;
        this.rating = rating;
        this.variantID = variantID;
        creationDate = new Date();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public int getVariantID() {
        return variantID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "{" + socket +
                "," + username +
                "," + rating +
                "," + variantID +
                '}';
    }
}
