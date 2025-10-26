import javax.swing.JFrame;

public class WingspanFrame extends JFrame {
    public WingspanFrame(String framename) {
        super(framename);
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new WingspanPanel());
        setVisible(true);
    }
}