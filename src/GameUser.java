/**
 * Created by jc4512 on 17/10/14.
 */
public class GameUser {
    private String name;
    private int rating;
    private int hashedPassword;

    public GameUser(String name, int rating, int hashedPassword) {
        this.name = name;
        this.rating = rating;
        this.hashedPassword = hashedPassword;
    }

    public GameUser(String name, int rating) {
        this.name = name;
        this.rating = rating;
        this.hashedPassword = 0;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }
}
