/**
 * Created by jc4512 on 17/10/14.
 */

import java.sql.*;

public class UserAccountManager {
    private static final String DB_USER = "jc4512";
    private static final String DB_PATH = "jdbc:postgresql://localhost:5432/jc4512";
    private static final String DB_PASSWORD = "P5iKp1JYXf";

    private Connection db;

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
            }
        }
    }


    //Retrieves user from database, or null if the credentials are incorrect
    public GameUser getUser(String username, int hashedPassword) {
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
}
