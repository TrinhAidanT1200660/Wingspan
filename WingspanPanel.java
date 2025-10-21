
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class WingspanPanel extends JPanel implements MouseListener {
    public UIFrame frame, secondFrame, thirdFrame, child, descendant;
    public UIImage image, testSprite;
    public BufferedImage dumpling, spriteAnim;
    public Tween tween, tween2;
    public Color frameColor = Color.black;
    public WingspanPanel() {
        try {
            dumpling = ImageIO.read(getClass().getResource("/dumpling.png"));
            spriteAnim = ImageIO.read(getClass().getResource("/sprite_sheet_example.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new UIFrame(this);
        frame.size = new Dim2(0.5, 0, 0.5, 0);
        frame.anchorPoint = new Vector2(0.5, 0.5);
        frame.position = new Dim2(0.5, 0, 0.5, 0);
        frame.rotation = 53;
        frame.backgroundColor = Color.black;
        frame.keepAspectRatio = true;
        thirdFrame = new UIFrame(this);
        thirdFrame.size = new Dim2(0.2, 0, 0.2, 0);
        thirdFrame.anchorPoint = new Vector2(0.5, 0.5);
        thirdFrame.position = new Dim2(0.5, 0, 0.5, 0);
        thirdFrame.rotation = 53;
        thirdFrame.backgroundColor = Color.ORANGE;
        thirdFrame.backgroundTransparency = 0.5f;
        thirdFrame.keepAspectRatio = true;
        thirdFrame.setZIndex(5);
        child = new UIFrame(this);
        child.size = new Dim2(0.5, 0, 0.5, 0);
        child.anchorPoint = new Vector2(0.5, 0.5);
        child.position = new Dim2(0.5, 0, 0.5, 0);
        child.rotation = 53;
        child.backgroundColor = Color.yellow;
        child.parent = frame;
        frame.children.add(child);
        descendant = new UIFrame(this);
        descendant.size = new Dim2(0.5, 0, 0.5, 0);
        descendant.anchorPoint = new Vector2(0.5, 0.5);
        descendant.position = new Dim2(0.5, 0, 0.5, 0);
        descendant.rotation = 53;
        descendant.backgroundColor = Color.BLUE;
        descendant.parent = child;
        child.children.add(descendant);
        secondFrame = new UIFrame(this);
        secondFrame.size = new Dim2(0, 100, 0, 100);
        secondFrame.backgroundColor = Color.GREEN;
        image = new UIImage(this);
        image.size = new Dim2(0.07, 0, 0.2, 0);
        image.keepAspectRatio = true;
        image.anchorPoint = new Vector2(1, 1);
        image.position = new Dim2(1, 0, 1, 0);
        image.backgroundColor = Color.RED;
        image.setImageFillType(UIImage.CROP_IMAGE);
        image.backgroundTransparency = 1f;
        image.image = dumpling;
        testSprite = new UIImage(this);
        testSprite.size = new Dim2(0.1, 0, 0.1, 0);
        testSprite.anchorPoint = new Vector2(0, 1);
        testSprite.position = new Dim2(0, 0, 1, 0);
        testSprite.backgroundColor = Color.black;
        testSprite.backgroundTransparency = 1f;
        testSprite.keepAspectRatio = true;
        testSprite.image = spriteAnim;
        testSprite.setImageFillType(UIImage.SPRITE_ANIMATION);
        testSprite.setSpriteSheet(50, 37);
        testSprite.addReleaseListener(e -> {
            testSprite.playSpriteAnimation(0.08, true);
        });
        addMouseListener(this);
        //frame.addClickListener(event -> frame.tweenSize(new Dim2(3, 0, 3, 0), 1, Tween.CUBIC_IN_OUT));
        secondFrame.addReleaseListener(event -> System.out.println("Clicked on second frame!"));
        descendant.addReleaseListener(event -> System.out.println("Clicked on frame's child's child!"));
        child.addReleaseListener(event -> System.out.println("Clicked on frame's child!"));
        image.addReleaseListener(event -> System.out.println("Clicked on image!"));
        UIElement.getRootForPanel(this).addReleaseListener(e -> {
            tween = frame.tweenPosition(new Dim2(0, e.getX(), 0, e.getY()), 0.5, Tween.OVERSHOOT);
            tween.onFinish(() -> System.out.println("finish pos"));
            frame.tweenRotation(frame.rotation + 180, 1, Tween.OVERSHOOT);
            tween2 = frame.tweenBackgroundColor(Color.PINK, 1, Tween.QUAD_IN_OUT);
            tween2.onFinish(() -> System.out.println("finish color"));
            frameColor = frameColor == Color.black ? Color.blue : Color.black;
            image.tweenImageTransparency(0.0f, 1);
            System.out.println("Clicked on screen!");
        });
        frame.addHoverListener(e -> frame.tweenSize(new Dim2(0.55, 0, 0.55, 0), 0.07, Tween.QUAD_IN_OUT));
        frame.addExitListener(e -> frame.tweenSize(new Dim2(0.5, 0, 0.5, 0), 0.07, Tween.QUAD_IN_OUT));
        frame.addPressListener(e -> frame.tweenSize(new Dim2(0.4, 0, 0.4, 0), 0.07, Tween.QUAD_IN_OUT).onFinish(() -> {
            System.out.println("frame pressed down animation complete");
        }));
        frame.addReleaseListener(e -> {
            boolean is = frame.containsPoint(e.getX(), e.getY());
            frame.tweenSize(new Dim2(is ? 0.55 : 0.5, 0, is ? 0.55 : 0.5, 0), 0.07, Tween.QUAD_IN_OUT);
        });
        JPanel panel = this;
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                UIElement root = UIElement.getRootForPanel(panel);
                if (root != null) {
                    root.handleMouseMovement(e);
                }
            }
        });
    }
    public void paint(Graphics g) {
        super.paint(g);
        UIElement.getRootForPanel(this).draw(g);
    }
    public void mousePressed(MouseEvent e) {
        //frame.tweenBackgroundColor(frameColor, 1, UIElement.QUAD_IN_OUT);
        UIElement.getRootForPanel(this).handlePress(e); 
    }
    public void mouseReleased(MouseEvent e) {
        UIElement.getRootForPanel(this).handleRelease(e); 
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
}
