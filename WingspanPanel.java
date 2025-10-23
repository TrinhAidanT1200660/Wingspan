
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class WingspanPanel extends JPanel implements MouseListener, MouseMotionListener {
    private UIElement root;
    public WingspanPanel() {
        // loading images (none yet)
        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        

        // necessary for all our GUI, you can just ignore this
        root = UIElement.getRootForPanel(this);
    }
    public void paint(Graphics g) {
        super.paint(g);
        root.draw(g);

        //
    }
    public void mousePressed(MouseEvent e) {
        root.handlePress(e); 

        //
    }
    public void mouseMoved(MouseEvent e) {
        root.handleMouseMovement(e);

        //
    }
    public void mouseDragged(MouseEvent e) {

    }
    public void mouseReleased(MouseEvent e) {
        root.handleRelease(e); 

        //
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
}
