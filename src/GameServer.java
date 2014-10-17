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
    public enum ClientMessage {
        GET_GAME_LIST,
        AUTHENTICATE_USER,
        CREATE_GAME,
        REMOVE_GAME,
        REPORT_PLAYER
    }

    private final int LISTENER_PORT = 4444;

    ServerSocket sktListener;


    @Override
    public void run() {
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

        } catch (Exception e) {
            //Malformed message. Safe to ignore.
        }
        return response;
    }
}
