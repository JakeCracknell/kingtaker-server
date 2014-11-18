package NetworkingCodes;

/**
 * Created by jc4512 on 23/10/14.
 */
public class ClientCommandCode {
    public final static int GET_GAME_LIST = 0;
    public final static int AUTHENTICATE_USER = 1;
    public final static int CREATE_GAME = 2;
    public final static int REMOVE_GAME = 3;
    public final static int REPORT_PLAYER = 4;
    public final static int REGISTER_ACCOUNT = 5;
    public final static int REPORT_GAME_RESULT = 6;

    public final static int PARAM_GAME_WIN = 0;
    public final static int PARAM_GAME_DRAW = 1;
    public final static int PARAM_GAME_LOSS = 2;

    public final static String DEL = ",";
}