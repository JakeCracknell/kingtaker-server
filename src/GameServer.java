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
    private GameServerResponder gameServerResponder;

    private ServerSocket sktListener;
    private GameLobby gameList;
    private UserAccountManager userAccountManager;

    @Override
    public void run() {
        //Create empty game lobby and start a new thread to remove old games.
        System.out.println("Initialising lobby...");
        gameList = new GameLobby();

        //Initialise UAM. Requires database access and might hang if the
        //connection is unavailable. Exits if driver/library is not found.
        System.out.println("Initialising user account manager...");
        userAccountManager = new UserAccountManager();

        //Initialise responder.
        System.out.println("Initialising game server responder...");
        gameServerResponder = new GameServerResponder(gameList, userAccountManager);

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
    //Delegates to the appropriate method in GameServerResponder.
    //If it parses correctly, an appropriate response will be returned and any
    // requested actions will be completed. Otherwise, it will return the INVALID code.
    private String processMessageAndGetResponse(Socket socket, String message) {
        String response = null;
        try {
            String fields[] = message.split(ClientCommandCode.DEL);
            int clientCommandCode = Integer.valueOf(fields[0]);

            switch (clientCommandCode) {
                case ClientCommandCode.GET_GAME_LIST :
                    response = gameServerResponder.getGameList();
                    break;

                case ClientCommandCode.AUTHENTICATE_USER :
                    response = gameServerResponder.authenticateUser(socket, fields[1], fields[2]);
                    break;

                case ClientCommandCode.CREATE_GAME :
                    response = gameServerResponder.createGame(socket, fields[1]);
                    break;

                case ClientCommandCode.REMOVE_GAME :
                    gameServerResponder.removeGame(socket);
                    break;

                case ClientCommandCode.REPORT_PLAYER :
                    gameServerResponder.reportPlayer(fields[1]);
                    break;

                case ClientCommandCode.REGISTER_ACCOUNT :
                    response = gameServerResponder.registerAccount(socket, fields[1], fields[2]);
                    break;

                case ClientCommandCode.REPORT_GAME_RESULT :
                    response = gameServerResponder.reportGameResult(socket, fields[1], fields[2]);
                    break;
            }

        } catch (Exception e) {
            //Is only supposed to occur if the message is malformed and fields[n] is out of bounds,
            //throwing an ArrayIndexOutOfBoundsException.
            response = ResponseCode.INVALID + "";
        }
        return response;
    }

}
