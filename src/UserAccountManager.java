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
    private static final int CLIENT_AUTHENTICATION_TIMEOUT_MS = 1000*60*60; //1 hour

    private Connection db;

    //Maps used to hold authenticated users/ips.
    private Map<InetAddress, GameUser> ipToUserMap;
    private Map<GameUser, InetAddress> userToIpMap;

    public UserAccountManager() {
        //Exit if the driver is missing
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //Connect to database - application might hang.
        connectToDatabase();

        //Initialise authenticated users/ips map.
        ipToUserMap = new HashMap<InetAddress, GameUser>();
        userToIpMap = new HashMap<GameUser, InetAddress>();
    }

    //Keeps reattempting to connect to the database in an infinite loop
    private void connectToDatabase() {
        while (db == null) {
            try {
                db = DriverManager.getConnection(DB_PATH, DB_USER, DB_PASSWORD);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + " - retrying...");
            }
        }
    }

    //Retrieves user from database and stores in the users map,
    //  *** user is now authenticated for 1 hour ***
    //or null if the credentials are incorrect.
    //TODO: should clear the authentication if someone of the same ip
    // or username was previously connected
    public GameUser authenticateUser(String username, int hashedPassword, InetAddress ip) {
        GameUser gameUser = null;

        try {
            //Reconnect to database if the connection has been lost.
            if (db.isClosed()) {
                connectToDatabase();
            }

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

            ipToUserMap.put(ip,gameUser);
            userToIpMap.put(gameUser,ip);

        } catch (SQLException e) {
        }

        return gameUser;
    }


    //Given an IP address of the client, gets that current user.
    //Returns null if they need to be authenticated again or were never logged in.
    //Side effect: removes user from ipToUserMap if their login time is old.
    public GameUser getUserByAddress(InetAddress ip) {
        return ipToUserMap.get(ip);
    }

    //Checks user was authenticated and their timeout hasn't elapsed.
    public boolean checkUserIsAuthenticated(GameUser user) {
        return userToIpMap.containsKey(user) &&
                user.getTimeSinceAuthenticated() > CLIENT_AUTHENTICATION_TIMEOUT_MS;

    }

    //Removes user/ip from hashmaps. They will need to authenticate themselves again.
    public void unauthenticateUser(GameUser user) {
        ipToUserMap.remove(userToIpMap.remove(user));
    }
}
