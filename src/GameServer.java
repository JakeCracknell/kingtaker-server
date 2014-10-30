import NetworkingCodes.ClientCommandCode;
import NetworkingCodes.ResponseCode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jc4512 on 16/10/14.
 */
public class GameServer implements Runnable {
    private static final String MESSAGE_DELIMINATOR = ",";
    private static final int LISTENER_PORT = 4444;

    private ServerSocket sktListener;
    private GameLobby gameList;
    private UserAccountManager userAccountManager;

    @Override
    public void run() {
        //Create empty game lobby
        System.out.println("Initialising lobby...");
        gameList = new GameLobby();

        //Initialise UAM. Requires database access and might hang if the
        //connection is unavailable. Exits if driver/library is not found.
        System.out.println("Initialising user account manager...");
        userAccountManager = new UserAccountManager();

        //Initialise listener - keep attempting until successful.
        System.out.println("Initialising listening socket...");
        while (sktListener == null) {
            try {
                sktListener = new ServerSocket(LISTENER_PORT);
            } catch (IOException e) {
                System.out.println(e.getMessage() + " - retrying...");
            }
        }
        System.out.println("Game server has started");

        //Run listening loop
        while (true) {
            try {
                //Receive message from client
                Socket sktClient = sktListener.accept();
                BufferedReader clientReader =
                        new BufferedReader(new InputStreamReader(sktClient.getInputStream()));
                String clientMessage = clientReader.readLine();
                System.out.println("[" + sktClient.getInetAddress().getHostAddress() + "] sent: " + clientMessage);

                //Process message and generate an appropriate response, or none if it is junk.
                String serverResponseMessage = processMessageAndGetResponse(sktClient,clientMessage);
                if (serverResponseMessage != null) {
                    DataOutputStream clientWriter = new DataOutputStream(sktClient.getOutputStream());
                    clientWriter.writeBytes(serverResponseMessage);
                    System.out.println("[" + sktClient.getInetAddress().getHostAddress() + "] response: " + serverResponseMessage);
                } else {
                    System.out.println("[" + sktClient.getInetAddress().getHostAddress() + "] no response required");
                }

                //TODO: is this needed?
                clientReader.close();
                sktClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Splits message into fields, then switch on the first, which specifies the opcode.
    //If it parses correctly, the server will perform any requested actions and/or return
    // information. Otherwise, it will return the INVALID error code.
    private String processMessageAndGetResponse(Socket socket, String message) {
        String response = null;
        try {
            String fields[] = message.split(MESSAGE_DELIMINATOR);
            int clientCommandCode = Integer.valueOf(fields[0]);

            switch (clientCommandCode) {
                case ClientCommandCode.GET_GAME_LIST :
                    response = ResponseCode.OK + MESSAGE_DELIMINATOR + gameList.toString();
                    if (response == "") {
                        response = ResponseCode.EMPTY + "";
                    }
                    break;
                case ClientCommandCode.AUTHENTICATE_USER :
                    GameUser userToAuth = userAccountManager.authenticateUser(fields[1],
                            Integer.valueOf(fields[2]), socket.getInetAddress());
                    if (userToAuth != null) {
                        response = ResponseCode.OK + MESSAGE_DELIMINATOR + userToAuth.getRating() + "";
                    } else {
                        response = ResponseCode.BAD_LOGIN + "";
                    }
                    break;
                case ClientCommandCode.CREATE_GAME :
                    int variantID = Integer.valueOf(fields[1]);
                    GameUser userGameHost = userAccountManager.getUserByAddress(socket.getInetAddress());
                    if (userAccountManager.checkUserIsAuthenticated(userGameHost)) {
                        gameList.addGame(new Game(socket, userGameHost, variantID));
                        response = ResponseCode.OK + "";
                    } else {
                        userAccountManager.unauthenticateUser(userGameHost);
                        response = ResponseCode.BAD_LOGIN + "";
                    }
                    break;
                case ClientCommandCode.REMOVE_GAME :
                    GameUser userExGameHost = userAccountManager.getUserByAddress(socket.getInetAddress());
                    if (userExGameHost != null) {
                        gameList.removeByUser(userExGameHost);
                    }
                    break;
                case ClientCommandCode.REPORT_PLAYER :
                    //TODO: reporting system.
                    break;
            }

        } catch (Exception e) {
            response = ResponseCode.INVALID + "";
        }
        return response;
    }
}
