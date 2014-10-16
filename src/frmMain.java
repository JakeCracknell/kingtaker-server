import javax.swing.*;

/**
 * Created by jc4512 on 16/10/14.
 */
public class frmMain {

    private JTable table1;
    private JPanel panel1;
    private JButton button1;

    public frmMain() {

    }

    //Boilerplate code to initialise and display GUI form.
    //Starting listening server
    public static void main(String[] args) {
        JFrame frame = new JFrame("frmMain");
        frame.setContentPane(new frmMain().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        GameServer gameServer = new GameServer();
        Thread gameServerThread = new Thread(gameServer);
        gameServerThread.start();
    }
}
