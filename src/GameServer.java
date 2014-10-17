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
    public enum ClientCommandCode {
        GET_GAME_LIST,
        AUTHENTICATE_USER,
        CREATE_GAME,
        REMOVE_GAME,
        REPORT_PLAYER
    }

    private final char MESSAGE_DELIMINATOR = ',';
    private final int LISTENER_PORT = 4444;

    ServerSocket sktListener;
    GameLobby gameList;

    @Override
    public void run() {
        //Create empty game lobby
        gameList = new GameLobby();

        //Initialise listener - keep attempting until successful.
        while(sktListener == null) {
            try {
                sktListener = new ServerSocket(LISTENER_PORT);
            } catch (IOException e) {
            }
        }

        //Run listening loop
        while (true) {
            try {
                //Receive message from client
                Socket sktClient = sktListener.accept();
                BufferedReader clientReader =
                        new BufferedReader(new InputStreamReader(sktClient.getInputStream()));
                String clientMessage = clientReader.readLine();
                System.out.println("[" + sktClient.getInetAddress() + "] sent: " + clientMessage);

                //Process message and generate an appropriate response, or none if it is junk.
                String serverResponseMessage = processMessageAndGetResponse(sktClient,clientMessage);
                if (serverResponseMessage != null) {
                    DataOutputStream clientWriter = new DataOutputStream(sktClient.getOutputStream());
                    clientWriter.writeBytes(serverResponseMessage);
                    System.out.println("[" + sktClient.getInetAddress() + "] response: " + serverResponseMessage);
                } else {
                    System.out.println("[" + sktClient.getInetAddress() + "] no response required");
                }

                //TODO: is this needed?
                clientReader.close();
                sktClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processMessageAndGetResponse(Socket socket, String message) {
        String response = null;
        try {
            //Split message (e.g. "1,myusername,mypassword") into fields. Switch on command type.
            String fields[] = message.split(Character.toString(MESSAGE_DELIMINATOR));
            ClientCommandCode clientCommandCode = ClientCommandCode.values()[Integer.getInteger(fields[0])];

            switch (clientCommandCode) {
                case GET_GAME_LIST :
                    response = gameList.toString();
                    break;
                case AUTHENTICATE_USER :

                    break;
                case CREATE_GAME :

                    break;
                case REMOVE_GAME :

                    break;
                case REPORT_PLAYER :

                    break;
            }

        } catch (Exception e) {
            //Malformed message. Safe to ignore.
        }
        return response;
    }
}
