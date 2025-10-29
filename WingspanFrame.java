
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class WingspanFrame extends JFrame {
    public WingspanFrame(String framename) {
        super(framename);
        try {
            Image icon = new ImageIcon(getClass().getResource("/resources/images/wingspan_favicon.png")).getImage();
            setIconImage(icon);
            Image cursorImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/mouse_pointer_orange.png"));
            Point hotspot = new Point(0, 0);
            Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, hotspot, "cursor");
            setCursor(customCursor);
        } catch (NullPointerException e) {
            System.err.println("Icon image not found");
        }
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WingspanPanel panel = new WingspanPanel();
        add(panel);
        setVisible(true);
    }
}
