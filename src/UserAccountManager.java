/**
 * Created by jc4512 on 17/10/14.
 */
import java.sql.Connection;
import java.sql.DriverManager;

public class UserAccountManager {
    private static final String DB_USER = "jc4512";
    private static final String DB_PATH = "jdbc:postgresql://localhost:5432/jc4512";
    private static final String DB_PASSWORD = "P5iKp1JYXf";

    private Connection db;

    //Open database
    public UserAccountManager() {
        try {
            Class.forName("org.postgresql.Driver");
            db = DriverManager.getConnection(DB_PATH, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    //Retrieves user from database, or null if the credentials are incorrect
    public GameUser authenticateUser(String username, int hashedPassword) {
        return null;
    }
}
