
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class WingspanPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final UIElement root;
    public WingspanPanel() {
        // loading images (none yet)
        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }



        // necessary for all our GUI, you can just ignore this
        root = UIElement.getRootForPanel(this);

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public void paint(Graphics g) {
        super.paint(g);
        root.draw(g); // drawing everything on screen
    }

    public void mousePressed(MouseEvent e) {
        RootMouseEvent event = root.handlePress(e); 
        UIElement pressed = event.getElement(); // tells us what element was pressed (mouse button went down but not up)
    }

    public void mouseReleased(MouseEvent e) {
        RootMouseEvent event = root.handleRelease(e); 
        UIElement released = event.getElement(); // tells us what button was pressed on and then released on (so full click)
        
        // we can do whatever with the button that was fully clicked here...
    }

    public void mouseEntered(MouseEvent e) { }

    public void mouseClicked(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mouseMoved(MouseEvent e) {
        RootMouseEvent events[] = root.handleMouseMovement(e);
        RootMouseEvent nowHoveringEvent = events[0];
        RootMouseEvent previouslyHoveringEvent = events[1];

        UIElement nowHovering = nowHoveringEvent.getElement();
        UIElement previouslyHovering = previouslyHoveringEvent.getElement();

        boolean changed = nowHovering != previouslyHovering;
        if (changed) {
            // hovering over something else now...

        }
    }

    public void mouseDragged(MouseEvent e) { }
}
