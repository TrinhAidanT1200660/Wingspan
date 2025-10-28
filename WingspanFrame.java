import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class WingspanFrame extends JFrame {
    public WingspanFrame(String framename) {
        super(framename);
        try {
            Image icon = new ImageIcon(getClass().getResource("/resources/images/wingspan_favicon.png")).getImage();
            setIconImage(icon);
        } catch (NullPointerException e) {
            System.err.println("Icon image not found");
        }
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new WingspanPanel());
        setVisible(true);
    }
}