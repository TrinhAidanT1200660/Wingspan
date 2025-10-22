
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class WingspanPanel extends JPanel implements MouseListener {
    public UIFrame frame, secondFrame, thirdFrame, child, descendant, test;
    public UIImage image, testSprite, background;
    public BufferedImage dumpling, spriteAnim, spriteAnim2, bgImg;
    public Tween tween, tween2;
    public Color frameColor = Color.black;
    public WingspanPanel() {
        try {
            dumpling = ImageIO.read(getClass().getResource("/dumpling.png"));
            spriteAnim = ImageIO.read(getClass().getResource("/sprite_sheet_example.png"));
            spriteAnim2 = ImageIO.read(getClass().getResource("/sonicSprite.png"));
            bgImg = ImageIO.read(getClass().getResource("/wingspanBg.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JPanel panel = this;
        UIElement root = UIElement.getRootForPanel(panel);
        background = new UIImage(this);
        root.cropOverflow = true;
        root.anchorPoint = new Vector2(0.5, 0.5);
        root.position = new Dim2(0.5, 0, 0.5, 0);
        root.rotation = 0;
        root.size = new Dim2(0.9, 0, 0.9, 0);
        test = new UIFrame(panel);
        test.backgroundColor = Color.BLACK;
        test.size = new Dim2(0, 50, 0, 50);
        test.position = new Dim2(0, 50, 0, 50);
        test.setZIndex(5000);
        background.size = new Dim2(1, 0, 1, 0);
        background.backgroundTransparency = 0f;
        background.image = bgImg;
        background.setImageFillType(UIImage.CROP_IMAGE);
        background.setZIndex(-5);
        frame = new UIFrame(this);
        frame.size = new Dim2(0.5, 0, 0.5, 0);
        frame.anchorPoint = new Vector2(0.5, 0.5);
        frame.position = new Dim2(0.5, 0, 0.5, 0);
        frame.backgroundColor = Color.black;
        frame.strokeThickness = 4;
        frame.borderRadius = 20;
        frame.strokeColor = Color.black;
        //frame.strokeTransparency = 1f;
        frame.setZIndex(0);
        frame.keepAspectRatio = true;
        thirdFrame = new UIFrame(this);
        thirdFrame.size = new Dim2(0.2, 0, 0.2, 0);
        thirdFrame.anchorPoint = new Vector2(0.5, 0.5);
        thirdFrame.position = new Dim2(0.5, 0, 0.5, 0);
        thirdFrame.rotation = 53;
        thirdFrame.backgroundColor = Color.ORANGE;
        thirdFrame.keepAspectRatio = true;
        thirdFrame.setZIndex(-1231231);
        child = new UIFrame(this);
        child.size = new Dim2(0.5, 0, 0.5, 0);
        child.anchorPoint = new Vector2(0.5, 0.5);
        child.position = new Dim2(0.5, 0, 0.5, 0);
        child.setZIndex(2);
        child.rotation = 23;
        child.backgroundColor = Color.yellow;
        frame.addChild(child);
        /* descendant = new UIFrame(this);
        descendant.size = new Dim2(0.5, 0, 0.5, 0);
        descendant.anchorPoint = new Vector2(0.5, 0.5);
        descendant.position = new Dim2(0.5, 0, 0.5, 0);
        descendant.rotation = 53;
        descendant.backgroundColor = Color.BLUE;  */
        secondFrame = new UIFrame(this);
        secondFrame.size = new Dim2(0, 100, 0, 100);
        secondFrame.backgroundColor = Color.GREEN;
        image = new UIImage(this);
        image.size = new Dim2(0.25, 0, 0.25, 0);
        image.anchorPoint = new Vector2(1,1);
        image.position = new Dim2(1,0,1,0);
        image.backgroundColor = Color.RED;
        image.setImageFillType(UIImage.CROP_IMAGE);
        image.backgroundTransparency = 1f;
        image.image = dumpling; 
        testSprite = new UIImage(this);
        testSprite.size = new Dim2(0.25, 0, 0.25, 0);
        testSprite.anchorPoint = new Vector2(0.5, 0.5);
        testSprite.position = new Dim2(0.5, 0, 0.5, 0);
        testSprite.backgroundColor = Color.black;
        testSprite.backgroundTransparency = 1f;
        testSprite.image = spriteAnim;
        testSprite.setImageFillType(UIImage.SPRITE_ANIMATION);
        testSprite.setSpriteSheet(50, 37);
        testSprite.addReleaseListener(e -> {
            testSprite.playSpriteAnimation(0.08, true);
        });
        UIImage secondSprite = new UIImage(this);
        secondSprite.size = new Dim2(0.5, 0, 0.5, 0);
        secondSprite.anchorPoint = new Vector2(1, 1);
        secondSprite.position = new Dim2(1,0,1,0);
        secondSprite.backgroundColor = Color.black;
        secondSprite.backgroundTransparency = 1f;
        secondSprite.image = spriteAnim2;
        secondSprite.setImageFillType(UIImage.SPRITE_ANIMATION);
        secondSprite.setSpriteSheet(71, 90);
        secondSprite.addReleaseListener(e -> {
            secondSprite.playSpriteAnimation(0.08, true);
        });
        secondSprite.setParent(child);
        testSprite.size = new Dim2(0.25, 0, 0.25, 0);
        testSprite.anchorPoint = new Vector2(0.5, 0.5);
        testSprite.position = new Dim2(0.5, 0, 0.5, 0);
        testSprite.backgroundColor = Color.black;
        testSprite.backgroundTransparency = 1f;
        testSprite.image = spriteAnim;
        testSprite.setImageFillType(UIImage.SPRITE_ANIMATION);
        testSprite.setSpriteSheet(50, 37);
        testSprite.addReleaseListener(e -> {
            testSprite.playSpriteAnimation(0.08, true);
        });
        addMouseListener(this);
        //frame.addClickListener(event -> frame.tweenSize(new Dim2(3, 0, 3, 0), 1, Tween.CUBIC_IN_OUT));
        secondFrame.addReleaseListener(event -> System.out.println("Clicked on second frame!"));
        //descendant.addReleaseListener(event -> System.out.println("Clicked on frame's child's child!"));
        child.addReleaseListener(event -> System.out.println("Clicked on frame's child!"));
        image.addReleaseListener(event -> System.out.println("Clicked on image!"));
        background.addReleaseListener(e -> {
            tween = frame.tweenPosition(new Dim2(0, e.getX(), 0, e.getY()), 0.5, Tween.OVERSHOOT);
            tween.onFinish(() -> System.out.println("finish pos"));
            frame.tweenRotation(frame.rotation + 53, 1, Tween.OVERSHOOT);
            frameColor = frameColor == Color.black ? Color.pink : Color.black;
            tween2 = frame.tweenBackgroundColor(frameColor, 1, Tween.QUAD_IN_OUT);
            tween2.onFinish(() -> System.out.println("finish color"));
            //image.tweenImageTransparency(0.0f, 1);
            System.out.println("Clicked on screen!");
        });
        frame.addHoverListener(e -> frame.tweenSize(new Dim2(0.55, 0, 0.55, 0), 0.07, Tween.QUAD_IN_OUT));
        frame.addExitListener(e -> frame.tweenSize(new Dim2(0.5, 0, 0.5, 0), 0.07, Tween.QUAD_IN_OUT));
        frame.addPressListener(e -> frame.tweenSize(new Dim2(0.4, 0, 0.4, 0), 0.07, Tween.QUAD_IN_OUT).onFinish(() -> {
            System.out.println("frame pressed down animation complete");
        }));
        frame.addReleaseListener(e -> {
            boolean is = frame.containsPoint(e.getX(), e.getY());
            frame.tweenSize(new Dim2(is ? 0.55 : 0.5, 0, is ? 0.55 : 0.5, 0), 0.07, Tween.QUAD_IN_OUT).onFinish(() -> {
                System.out.println("done");
            });
        }); 
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
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
        UIElement root = UIElement.getRootForPanel(this);
        if (root.containsPoint(e.getX(), e.getY())) return;
        root.tweenRotation(root.rotation + 45, 0.5, Tween.OVERSHOOT);
    }
    public void mouseReleased(MouseEvent e) {
        UIElement.getRootForPanel(this).handleRelease(e); 
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
}
