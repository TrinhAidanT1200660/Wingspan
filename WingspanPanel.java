
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class WingspanPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final UIElement root, transition, startMenu;
    private final HashMap<String, BufferedImage> images = new HashMap<>();
    private final String[] requiredImages = {
        "wingspan_background.png",
        "wingspan_bird.png",
        "button_dropshadow.png",
        "competitive_button.png",
        "peaceful_button.png",
        "button_cover.png",
        "wingspan_title.png",
    };

    public WingspanPanel() {
        // loading images (none yet)
        loadAllImages();

        UIImage backgroundFrame = new UIImage("Background", this); // background image of the sky
        backgroundFrame.image = images.get("wingspan_background"); // setting the picture to the BufferedImage of the sky
        backgroundFrame.size.full(); // take 100% of the screen
        backgroundFrame.backgroundTransparency = 0f; // no background
        backgroundFrame.setImageFillType(UIImage.CROP_IMAGE); // setting it so even if screen is an awkward size the picture will crop itself to fit the whole screen

        UIFrame startScreen = new UIFrame("StartScreen", this); // invisible frame holding all the buttons for start screen
        startScreen.anchorPoint.center();
        startScreen.position.center(); // center in middle
        startScreen.size.full(); // entire screen
        startScreen.backgroundTransparency = 0; // invisible

        startMenu = new UIFrame("StartMenu", this);
        startMenu.anchorPoint.center();
        startMenu.position = new Dim2(0.5, 0, 0.55, 0);
        startMenu.size = new Dim2(0.5, 0, 0.6, 0);
        startMenu.keepAspectRatio = true;
        startMenu.backgroundTransparency = 0;
        startMenu.setZIndex(1);
        startMenu.setParent(startScreen);

        UIFrame birdContainer = new UIFrame("BirdContainer", this);
        birdContainer.size = new Dim2(0.87, 0, 1, 0); // 87% width of the invisible start menu, 100% height
        birdContainer.anchorPoint = new Vector2(0, 0.5);
        birdContainer.position = new Dim2(-0.1, 0, 0.5, 0);
        birdContainer.backgroundTransparency = 0f; // no background
        birdContainer.setParent(startMenu); // setting the startscreen frame as its parent

        UIFrame birdHitbox = new UIFrame("BirdHitbox", this);
        birdHitbox.size = new Dim2(0.35, 0, 0.35, 0); // 87% width of the invisible start menu, 100% height
        birdHitbox.anchorPoint.center();
        birdHitbox.setZIndex(2);
        birdHitbox.position.center();
        birdHitbox.backgroundTransparency = 0f; // no background
        birdHitbox.setParent(birdContainer); // setting the startscreen frame as its parent

        UIImage birdImage = new UIImage("BirdImage", this); // picture of the wingspan bird
        birdImage.image = images.get("wingspan_bird"); // setting picture to the BufferedImage of the bird
        birdImage.size.full(); // 100% size of its container
        birdImage.anchorPoint.center(); // centered anchor point
        birdImage.position.center(); // centered position
        birdImage.backgroundTransparency = 0f; // no background
        birdImage.setImageFillType(UIImage.FIT_IMAGE); // setting it so even if screen is an awkward size the picture will fit to the biggest size it can without stretching
        birdImage.setParent(birdContainer); // setting the startscreen frame as its parent
        birdImage.setAttribute("ogsize", new Dim2().full());
        birdImage.setAttribute("hoversize", new Dim2(1.1, 0, 1.1, 0));
        birdImage.setAttribute("presssize", new Dim2(0.8, 0, 0.8, 0));
        birdHitbox.setAttribute("animOnHoverRot", birdImage);
        birdHitbox.setAttribute("animOnPress", birdImage);

        UIFrame peacefulContainer = new UIFrame("PeacefulContainer", this);
        peacefulContainer.backgroundTransparency = 0f;
        peacefulContainer.size = new Dim2(0.35, 0, 0.125, 0);
        peacefulContainer.anchorPoint = new Vector2(0.5, 0.5);
        peacefulContainer.position = new Dim2(0.825, 0, 0.5, 0);
        peacefulContainer.setParent(startMenu);
        peacefulContainer.setAttribute("ogsize", new Dim2(0.35, 0, 0.125, 0));
        peacefulContainer.setAttribute("hoversize", new Dim2(0.4, 0, 0.15, 0));
        peacefulContainer.setAttribute("presssize", new Dim2(0.275, 0, 0.0985, 0));

        UIImage peacefulButton = new UIImage("PeacefulButton", this);
        peacefulButton.image = images.get("peaceful_button");
        peacefulButton.backgroundTransparency = 0f;
        peacefulButton.size.full();
        peacefulButton.setImageFillType(UIImage.FIT_IMAGE);
        peacefulButton.setParent(peacefulContainer);

        UIImage peacefulButtonCover = new UIImage("PeacefulButtonCover", this);
        peacefulButtonCover.backgroundTransparency = 0f;
        peacefulButtonCover.ignore = true;
        peacefulButtonCover.size.full();
        peacefulButtonCover.image = images.get("button_cover");
        peacefulButtonCover.setImageFillType(UIImage.FIT_IMAGE);
        peacefulButtonCover.imageTransparency = 0f;
        peacefulButtonCover.setParent(peacefulButton);

        UIImage peacefulDropshadow = new UIImage("PeacefulDropshadow", this);
        peacefulDropshadow.image = images.get("button_dropshadow");
        peacefulDropshadow.backgroundTransparency = 0f;
        peacefulDropshadow.size.full();
        peacefulDropshadow.imageTransparency = 0f;
        peacefulDropshadow.setZIndex(-1);
        peacefulDropshadow.position = new Dim2(0, 0, 0, 0);
        peacefulDropshadow.setImageFillType(UIImage.FIT_IMAGE);
        peacefulDropshadow.setParent(peacefulContainer);
        peacefulButton.setAttribute("drop", peacefulDropshadow);
        peacefulButton.setAttribute("animOnHoverRot", peacefulContainer);
        peacefulButton.setAttribute("animOnPress", peacefulContainer);
        peacefulButton.setAttribute("pressCover", peacefulButtonCover);
        peacefulButton.setAttribute("startButton", true);

        UIFrame competitiveContainer = new UIFrame("CompetitiveContainer", this);
        competitiveContainer.backgroundTransparency = 0f;
        competitiveContainer.size = new Dim2(0.35, 0, 0.125, 0);
        competitiveContainer.anchorPoint = new Vector2(0.5, 0.5);
        competitiveContainer.position = new Dim2(0.825, 0, 0.67, 0);
        competitiveContainer.setParent(startMenu);
        competitiveContainer.setAttribute("ogsize", new Dim2(0.35, 0, 0.125, 0));
        competitiveContainer.setAttribute("hoversize", new Dim2(0.4, 0, 0.15, 0));
        competitiveContainer.setAttribute("presssize", new Dim2(0.3, 0, 0.1, 0));

        UIImage competitiveButton = new UIImage("CompetitiveButton", this);
        competitiveButton.image = images.get("competitive_button");
        competitiveButton.backgroundTransparency = 0f;
        competitiveButton.size.full();
        competitiveButton.setImageFillType(UIImage.FIT_IMAGE);
        competitiveButton.setParent(competitiveContainer);

        UIImage competitiveButtonCover = new UIImage("CompetitiveButtonCover", this);
        competitiveButtonCover.backgroundTransparency = 0f;
        competitiveButtonCover.ignore = true;
        competitiveButtonCover.size.full();
        competitiveButtonCover.image = images.get("button_cover");
        competitiveButtonCover.setImageFillType(UIImage.FIT_IMAGE);
        competitiveButtonCover.imageTransparency = 0f;
        competitiveButtonCover.setParent(competitiveButton);

        UIImage competitiveDropshadow = new UIImage("CompetitiveDropshadow", this);
        competitiveDropshadow.image = images.get("button_dropshadow");
        competitiveDropshadow.backgroundTransparency = 0f;
        competitiveDropshadow.size.full();
        competitiveDropshadow.imageTransparency = 0f;
        competitiveDropshadow.setZIndex(-1);
        competitiveDropshadow.position = new Dim2(0, 0, 0, 0);
        competitiveDropshadow.setImageFillType(UIImage.FIT_IMAGE);
        competitiveDropshadow.setParent(competitiveContainer);
        competitiveButton.setAttribute("drop", competitiveDropshadow);
        competitiveButton.setAttribute("animOnHoverRot", competitiveContainer);
        competitiveButton.setAttribute("animOnPress", competitiveContainer);
        competitiveButton.setAttribute("startButton", true);
        competitiveButton.setAttribute("pressCover", competitiveButtonCover);

        UIImage title = new UIImage("Title", this);
        title.image = images.get("wingspan_title");
        title.backgroundTransparency = 0f;
        title.size = new Dim2(1, 0, 0.4, 0);
        title.position = new Dim2(0, 0, -0.15, 0);
        title.setImageFillType(UIImage.FIT_IMAGE);
        title.setZIndex(-2);
        title.setParent(startMenu);

        transition = new UIFrame("Transition", this);
        transition.backgroundTransparency = 0f;
        transition.visible = false;
        transition.setZIndex(100000);
        transition.size.full();
        transition.position.center();
        transition.anchorPoint.center();
        transition.backgroundColor = Color.BLACK;

        // necessary for all our GUI, you can just ignore this
        root = UIElement.getRootForPanel(this);

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    // loads all images in the requiredImages array into images hashmap (maybe theres a better way to do this?) 
    private void loadAllImages() {
        for (String file : requiredImages) { // loop
            try {
                images.put(file.split("\\.")[0], ImageIO.read(getClass().getResource("/images/" + file))); // puts in the images hashMap: key: file name, value: loaded image
            } catch (IOException e) {
                System.out.println("Error loading " + file + ": " + e.getLocalizedMessage());
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        root.draw(g); // drawing everything on screen
    }

    public void mousePressed(MouseEvent e) {
        RootMouseEvent event = root.handlePress(e); 
        UIElement pressed = event.getElement(); // tells us what element was pressed (mouse button went down but not up)

        if (pressed != null) {
            Object hasAnimOnPress = pressed.getAttribute("animOnPress");
            if (hasAnimOnPress != null) {
                UIElement container = (UIElement)hasAnimOnPress;
                container.tweenSize((Dim2)container.getAttribute("presssize"), 0.05, Tween.QUAD_IN_OUT);
            }

            Object hasPressCover = pressed.getAttribute("pressCover");
            if (hasPressCover != null) {
                UIImage pressCover = (UIImage)hasPressCover;
                pressCover.tweenImageTransparency(0.2f, 0.075, Tween.QUAD_IN_OUT);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        RootMouseEvent event = root.handleRelease(e); 
        UIElement released = event.getElement(); // tells us what button was pressed on and then released on (so full click)
        
        if (released != null) {
            // we can do whatever with the button that was fully clicked here...
            Object hasAnimOnPress = released.getAttribute("animOnPress");
            if (hasAnimOnPress != null) {
                UIElement drop = (UIElement)hasAnimOnPress;
                drop.tweenSize((Dim2)drop.getAttribute("ogsize"), 0.05, Tween.QUAD_IN_OUT);
            }

            Object hasPressCover = released.getAttribute("pressCover");
            if (hasPressCover != null) {
                UIImage pressCover = (UIImage)hasPressCover;
                pressCover.tweenImageTransparency(0f, 0.075, Tween.QUAD_IN_OUT);
            }

            Object isStartButton = released.getAttribute("startButton");
            if (isStartButton != null && released.containsPoint(e.getX(), e.getY())) {
                transition.visible = true;
                startMenu.tweenSize(new Dim2(1.5, 0, 1.75, 0), 0.5, Tween.QUAD_IN_OUT);
                transition.tweenBackgroundTransparency(1f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> {
                    startMenu.visible = false;
                    startMenu.size = new Dim2(0.5, 0, 0.55, 0);
                    transition.tweenBackgroundTransparency(0f, 0.5, Tween.QUAD_IN_OUT).onFinish(() -> {
                        transition.visible = false;
                    });
                });
            }
        }
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
            if (previouslyHovering != null) {
                Object hasDrop = previouslyHovering.getAttribute("drop");
                if (hasDrop != null) {
                    UIImage drop = (UIImage)hasDrop;
                    drop.tweenImageTransparency(0f, 0.1, Tween.QUAD_IN_OUT);
                    drop.tweenPosition(new Dim2(), 0.1, Tween.QUAD_IN_OUT);
                }
                Object hasAnimOnHover = previouslyHovering.getAttribute("animOnHover");
                Object hasAnimOnHoverRot = previouslyHovering.getAttribute("animOnHoverRot");
                if (hasAnimOnHover != null || hasAnimOnHoverRot != null) {
                    UIElement container = (UIElement)hasAnimOnHover;
                    if (hasAnimOnHoverRot != null) {
                        container = (UIElement)hasAnimOnHoverRot;
                        container.tweenRotation(0, 0.1, Tween.QUAD_IN_OUT);
                    }
                    container.tweenSize((Dim2)container.getAttribute("ogsize"), 0.1, Tween.QUAD_IN_OUT);
                }
            }
            if (nowHovering != null) {
                Object hasDrop = nowHovering.getAttribute("drop");
                if (hasDrop != null) {
                    UIImage drop = (UIImage)hasDrop;
                    drop.tweenImageTransparency(0.5f, 0.1, Tween.QUAD_IN_OUT);
                    drop.tweenPosition(new Dim2(0.01, 0, 0.1, 0), 0.1, Tween.QUAD_IN_OUT);
                }
                Object hasAnimOnHover = nowHovering.getAttribute("animOnHover");
                Object hasAnimOnHoverRot = nowHovering.getAttribute("animOnHoverRot");
                if (hasAnimOnHover != null || hasAnimOnHoverRot != null) {
                    UIElement container = (UIElement)hasAnimOnHover;
                    if (hasAnimOnHoverRot != null) {
                        container = (UIElement)hasAnimOnHoverRot;
                        container.tweenRotation((Math.random() * 20) - 10, 0.1, Tween.QUAD_IN_OUT);
                    }
                    container.tweenSize((Dim2)container.getAttribute("hoversize"), 0.1, Tween.QUAD_IN_OUT);
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) { }
}
