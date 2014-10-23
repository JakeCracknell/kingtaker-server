import java.util.Date;

/**
 * Created by jc4512 on 17/10/14.
 */
public class GameUser {
    private String name;
    private int rating;
    private int hashedPassword;
    private Date dateJoined;
    private long timeLastAuthenticated;

    public GameUser(String name, int rating, int hashedPassword, Date dateJoined) {
        this.name = name;
        this.rating = rating;
        this.hashedPassword = hashedPassword;
        this.dateJoined = dateJoined;
        this.timeLastAuthenticated = System.currentTimeMillis();
    }

    public GameUser(String name, int rating) {
        this.name = name;
        this.rating = rating;
        this.hashedPassword = 0;
        this.dateJoined = null;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public long getTimeSinceAuthenticated() {
        return System.currentTimeMillis() - timeLastAuthenticated;
    }
}
