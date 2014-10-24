/**
 * Created by jc4512 on 17/10/14.
 */

import java.net.InetAddress;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserAccountManager {
    private static final String DB_USER = "jc4512";
    private static final String DB_PATH = "jdbc:postgresql://db:5432/jc4512";
    private static final String DB_PASSWORD = "P5iKp1JYXf";
    private static final int CLIENT_AUTHENTICATION_TIMEOUT_MS = 1000*60*60;

    private Connection db;
    private Map<InetAddress, GameUser> userMap;

    //Open database - block until it is opened, but exit if the driver is missing.
    public UserAccountManager() {
        while (db == null) {
            try {
                Class.forName("org.postgresql.Driver");
                db = DriverManager.getConnection(DB_PATH, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + " - retrying...");
            }
        }

        userMap = new HashMap<InetAddress, GameUser>();
    }


    //Retrieves user from database and stores in the users map,
    //or null if the credentials are incorrect
    public GameUser authenticateUser(String username, int hashedPassword) {
        GameUser gameUser = null;

        try {
            //Prepared statement is used to minimise the risk of SQL injection
            PreparedStatement stmt = db.prepareStatement
                    ("SELECT * FROM tblUsers WHERE Username = ? AND PasswordHash = ?;");
            stmt.setString(1, username);
            stmt.setInt(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();

            //If login details are correct and user/pw exists in database,
            //create new GameUser instance.
            if (rs.next()) {
                gameUser = new GameUser(
                        username,
                        rs.getInt("Rating"),
                        hashedPassword,
                        rs.getDate("DateJoined")
                );
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
        }

        return gameUser;
    }


    //Given an IP address of the client, gets that current user.
    //Returns null if they need to be authenticated again or were never logged in.
    //Side effect: removes user from userMap if their login time is old.
    public GameUser getUserByAddress(InetAddress ip) {
        return userMap.get(ip);
    }

    public boolean checkUserIsAuthenticated(GameUser user) {
        return userMap.containsKey(user) &&
                user.getTimeSinceAuthenticated() > CLIENT_AUTHENTICATION_TIMEOUT_MS;

    }

    public void unauthenticateUser(GameUser user) {
        userMap.remove(user);
    }
}
