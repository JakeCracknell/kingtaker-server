import javax.swing.*;

/**
 * Created by jc4512 on 16/10/14.
 */
public class frmMain {

    private JTable table1;
    private JPanel panel1;

    public frmMain() {

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("frmMain");
        frame.setContentPane(new frmMain().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
