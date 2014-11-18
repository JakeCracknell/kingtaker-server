import NetworkingCodes.ResponseCode;
import ratings.RatingManager;
import users.GameUser;
import users.UserAccountManager;

import java.net.Socket;

//This class encapsulates the gamer server logic.
//It takes parameterised commands and returns appropriate responses.
public class GameServerResponder {
    private GameLobby gameList;
    private UserAccountManager userAccountManager;
    private RatingManager ratingManager;

    public GameServerResponder(GameLobby gameList, UserAccountManager userAccountManager) {
        this.gameList = gameList;
        this.userAccountManager = userAccountManager;
    }

    public String reportGameResult(Socket socket, String winOrLoss, String opponentUsername) {
        GameUser userReporter = userAccountManager.getUserByAddress(socket.getInetAddress());
        GameUser userOpponent = userAccountManager.getUserByName(opponentUsername);

        if (winOrLoss.equals("0")) {
            ratingManager.submitRating(userReporter, userOpponent,
                    RatingManager.GameResultType.WIN);
        } else if (winOrLoss.equals("1")) {
            ratingManager.submitRating(userReporter, userOpponent,
                    RatingManager.GameResultType.DRAW);
        } else if (winOrLoss.equals("2")) {
            ratingManager.submitRating(userReporter, userOpponent,
                    RatingManager.GameResultType.LOSS);
        } else {
            return ResponseCode.INVALID + "";
        }

        return ResponseCode.OK + ResponseCode.DEL + userReporter.getPendingRating();
    }

    public String registerAccount(Socket socket, String username, String passwordHash) {
        String response;
        if (userAccountManager.checkUsernameIsAcceptable(username)) {
            GameUser userToRegister = userAccountManager.registerUser(username,
                    Integer.parseInt(passwordHash), socket.getInetAddress());
            if (userToRegister != null) {
                response = ResponseCode.OK + ResponseCode.DEL + userToRegister.getRating();
            } else {
                response = ResponseCode.BAD_LOGIN + "";
            }
        } else {
            response = ResponseCode.REFUSED + "";
        }
        return response;
    }

    public void reportPlayer(String offenderName) {
        //TODO reporting players
    }

    public void removeGame(Socket socket) {
        GameUser userExGameHost = userAccountManager.getUserByAddress(socket.getInetAddress());
        if (userExGameHost != null) {
            gameList.removeByUser(userExGameHost);
        }
    }

    public String createGame(Socket socket, String variantID) {
        String response;
        GameUser userGameHost = userAccountManager.getUserByAddress(socket.getInetAddress());
        if (userAccountManager.checkUserIsAuthenticated(userGameHost)) {
            gameList.addGame(new Game(socket, userGameHost, Integer.valueOf(variantID)));
            response = ResponseCode.OK + "";
        } else {
            userAccountManager.unauthenticateUser(userGameHost);
            response = ResponseCode.BAD_LOGIN + "";
        }
        return response;
    }

    public String authenticateUser(Socket socket, String username, String passwordHash) {
        String response;
        GameUser userToAuth = userAccountManager.authenticateUser(username,
                Integer.parseInt(passwordHash), socket.getInetAddress());
        if (userToAuth != null) {
            response = ResponseCode.OK + ResponseCode.DEL + userToAuth.getRating() + "";
        } else {
            response = ResponseCode.BAD_LOGIN + "";
        }
        return response;
    }

    public String getGameList() {
        String response;
        String gameListStr = gameList.toString();
        if (gameListStr.equals("")) {
            response = ResponseCode.EMPTY + "";
        } else {
            response = ResponseCode.OK + ResponseCode.DEL + gameListStr;
        }
        return response;
    }
}