import java.util.Date;

/**
 * Created by jc4512 on 17/10/14.
 */
public class GameUser {
    public static final int DEFAULT_USER_RATING = 1000;

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

    public GameUser(String name, int hashedPassword) {
        this.name = name;
        this.rating = DEFAULT_USER_RATING;
        this.hashedPassword = hashedPassword;
        this.dateJoined = new Date();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameUser gameUser = (GameUser) o;

        if (!name.equals(gameUser.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
