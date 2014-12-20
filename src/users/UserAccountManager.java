package users; /**
 * Created by jc4512 on 17/10/14.
 */

import users.GameUser;

import java.net.InetAddress;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserAccountManager {
    private static final String DB_USER = "jc4512";
    private static final String DB_PATH = "jdbc:postgresql://db.doc.ic.ac.uk:5432/jc4512?ssl=true";
    private static final String DB_PASSWORD = "P5iKp1JYXf";

    private final String USERNAME_REGEX = "(\\w){3,20}";
    private static final int CLIENT_AUTHENTICATION_TIMEOUT_MS = 1000*60*60; //1 hour

    private Connection db;

    //Maps used to hold authenticated users/ips.
    private Map<InetAddress, GameUser> ipToUserMap;
    private Map<GameUser, InetAddress> userToIpMap;
    private Map<String, GameUser> usernameToUserMap;

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
        usernameToUserMap = new HashMap<String, GameUser>();
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
    //If user connects twice, maps do not get two entries -
    // the IP is updated if necessary.
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

            unauthenticateUser(gameUser);
            ipToUserMap.put(ip, gameUser);
            userToIpMap.put(gameUser, ip);
            usernameToUserMap.put(username, gameUser);

        } catch (SQLException e) {
        }

        return gameUser;
    }

    //A client has attempted to register a new account.
    //This method returns a new GameUser object or null if there was a problem.
    //Pre: the name conforms to the acceptable username regex pattern.
    //1. Check the name is not already taken
    //2. Insert username/password combo into database.
    //      null is returned if for any reason the user was not inserted.
    //3. Add user to ip maps.
    public GameUser registerUser(String username, int hashedPassword, InetAddress ip) {
        GameUser gameUser = null;

        try {
            //Reconnect to database if the connection has been lost.
            if (db.isClosed()) {
                connectToDatabase();
            }

            //Prepared statement is used to minimise the risk of SQL injection
            PreparedStatement stmt = db.prepareStatement
                    ("SELECT * FROM tblUsers WHERE Username = ?;");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            //If username already exists, return null.
            if (rs.next()) {
                return null;
            }
            rs.close();
            stmt.close();

            //Insert user into database and return null if the operation failed.
            PreparedStatement stmt2 = db.prepareStatement
                    ("INSERT INTO tblUsers VALUES( ?, now(), ?, ? );");
            stmt2.setString(1, username);
            stmt2.setInt(2, GameUser.DEFAULT_USER_RATING);
            stmt2.setInt(3, hashedPassword);
            if (stmt2.executeUpdate() == 0) {
                return null;
            }

            gameUser = new GameUser(username, hashedPassword);

            ipToUserMap.put(ip,gameUser);
            userToIpMap.put(gameUser,ip);
            usernameToUserMap.put(username, gameUser);

        } catch (SQLException e) {
        }

        return gameUser;
    }

    // Used to update a GameUsers details in the database.
    // Specifically, the rating and password.
    public void updateUser(GameUser gameUser) {
        try {
            //Reconnect to database if the connection has been lost.
            if (db.isClosed()) {
                connectToDatabase();
            }

            //Prepared statement is used to minimise the risk of SQL injection
            PreparedStatement stmt = db.prepareStatement
                    ("UPDATE tblUsers SET PasswordHash = ?, Rating = ? WHERE Username = ?;");
            stmt.setInt(1, gameUser.getHashedPassword());
            stmt.setInt(2, gameUser.getRating());
            stmt.setString(3, gameUser.getName());
            int success = stmt.executeUpdate();

            stmt.close();

        } catch (SQLException e) {
        }
    }


    //Given an IP address of the client, gets that current user.
    //Returns null if they were never logged in.
    public GameUser getUserByAddress(InetAddress ip) {
        return ipToUserMap.get(ip);
    }

    //Given an IP address of the client, gets that current user.
    //Returns null if they were never logged in.
    public GameUser getUserByName(String username) {
        return usernameToUserMap.get(username);
    }

    //Checks user was authenticated and their timeout hasn't elapsed.
    public boolean checkUserIsAuthenticated(GameUser user) {
        return user != null &&
                userToIpMap.containsKey(user) &&
                user.getTimeSinceAuthenticated() < CLIENT_AUTHENTICATION_TIMEOUT_MS;

    }

    //Removes user/ip from hashmaps. They will need to authenticate themselves again.
    public void unauthenticateUser(GameUser user) {
        if (user != null) {
            ipToUserMap.remove(userToIpMap.remove(user));
            usernameToUserMap.remove(user.getName());
        }
    }

    public boolean checkUsernameIsAcceptable(String username) {
        return username != null && username.matches(USERNAME_REGEX);
    }
}
