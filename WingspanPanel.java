
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
        backgroundFrame.backgroundTransparency = 0f; // no background color
        backgroundFrame.setImageFillType(UIImage.CROP_IMAGE); // setting it so even if screen is an awkward size the picture will crop itself to fit the whole screen

        UIFrame startScreen = new UIFrame("StartScreen", this); // invisible frame holding startMenu
        startScreen.anchorPoint.center(); // centered anchor point
        startScreen.position.center(); // center in middle
        startScreen.size.full(); // entire screen
        startScreen.keepAspectRatio = true; // keep aspect ratio so size remains consistent
        startScreen.backgroundTransparency = 0; // invisible

        startMenu = new UIFrame("StartMenu", this); // ok this is actually the invisible frame holding all the buttons for the start screen (child of startScreen)
        startMenu.anchorPoint.center(); // centered anchor point
        startMenu.position = new Dim2(0.5, 0, 0.55, 0); // positioned almost in the center but slightly more down to make space for title
        startMenu.size = new Dim2(0.5, 0, 0.6, 0); // 50% width of screen, 60% height of screen
        startMenu.backgroundTransparency = 0; // no background
        startMenu.setZIndex(1); // increase layer its on
        startMenu.setParent(startScreen); // parent it to the invisible frame startScreen

        UIFrame birdContainer = new UIFrame("BirdContainer", this); // invisible frame holding birdImage and birdHitbox (for petting)
        birdContainer.size = new Dim2(0.87, 0, 1, 0); // 87% width of the invisible start menu, 100% height
        birdContainer.anchorPoint = new Vector2(0, 0.5); // left middle
        birdContainer.position = new Dim2(-0.1, 0, 0.5, 0); // slightly to the left of the start screen by 10% and centered in the middle vertically
        birdContainer.backgroundTransparency = 0f; // no background
        birdContainer.setParent(startMenu); // setting the startMenu frame as its parent

        UIFrame birdHitbox = new UIFrame("BirdHitbox", this); // bird hitbox, used to detect clicks
        birdHitbox.size = new Dim2(0.35, 0, 0.35, 0); // 35% size of bird container
        birdHitbox.anchorPoint.center(); // centered
        birdHitbox.setZIndex(2); // layer is above birdImage
        birdHitbox.position.center(); // centered
        birdHitbox.backgroundTransparency = 0f; // no background so invisible
        birdHitbox.setParent(birdContainer); // setting the birdContainer frame as its parent

        UIImage birdImage = new UIImage("BirdImage", this); // picture of the wingspan bird
        birdImage.image = images.get("wingspan_bird"); // setting picture to the BufferedImage of the bird
        birdImage.size.full(); // 100% size of its container
        birdImage.anchorPoint.center(); // centered anchor point
        birdImage.position.center(); // centered position
        birdImage.backgroundTransparency = 0f; // no background
        birdImage.setImageFillType(UIImage.FIT_IMAGE); // setting it so even if screen is an awkward size the picture will fit to the biggest size it can without stretching
        birdImage.setParent(birdContainer); // setting the startscreen frame as its parent
        birdImage.setAttribute("ogsize", new Dim2().full()); // original size, 100%
        birdImage.setAttribute("hoversize", new Dim2(1.1, 0, 1.1, 0)); // size when hovered, 110%
        birdImage.setAttribute("presssize", new Dim2(0.8, 0, 0.8, 0)); // size when pressing down, 80%
        birdHitbox.setAttribute("animOnHoverRot", birdImage); // bird hitbox: when hovered over, it'll resize and rotate the birdImage
        birdHitbox.setAttribute("animOnPress", birdImage); // bird hitbox: when pressed, it'll resize birdImage

        // buttons on the start screen
        // peaceful button

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
        peacefulDropshadow.image = images.get("button_cover");
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

        // competitive button

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
        competitiveDropshadow.image = images.get("button_cover");
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

        // title image

        UIImage title = new UIImage("Title", this);
        title.image = images.get("wingspan_title");
        title.backgroundTransparency = 0f;
        title.size = new Dim2(1, 0, 0.4, 0);
        title.position = new Dim2(0, 0, -0.15, 0);
        title.setImageFillType(UIImage.FIT_IMAGE);
        title.setZIndex(-2);
        title.setParent(startMenu);

        // invisible black screen that fades in and out, for transitions
        
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

        if (pressed != null) { // if we actually pressed something
            Object hasAnimOnPress = pressed.getAttribute("animOnPress"); // check if the element wants to be animated when pressed
            if (hasAnimOnPress != null) { // if so
                UIElement container = (UIElement)hasAnimOnPress; // get the thing it wants to animate
                container.tweenSize((Dim2)container.getAttribute("presssize"), 0.05, Tween.QUAD_IN_OUT); // resize to the size it requested
            }

            Object hasPressCover = pressed.getAttribute("pressCover"); // check if element wants to slightly dim when pressed
            if (hasPressCover != null) { // if so 
                UIImage pressCover = (UIImage)hasPressCover; // get the cover
                pressCover.tweenImageTransparency(0.2f, 0.075, Tween.QUAD_IN_OUT); // fade it in 20%
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        RootMouseEvent event = root.handleRelease(e); 
        UIElement released = event.getElement(); // tells us what button was pressed on and then released on (so full click)
        
        if (released != null) { // if we actually pressed and released something
            // we can do whatever with the button that was fully clicked here...
            Object hasAnimOnPress = released.getAttribute("animOnPress"); // check if the element wants to be animated when pressed
            if (hasAnimOnPress != null) { // if so 
                UIElement drop = (UIElement)hasAnimOnPress;
                drop.tweenSize((Dim2)drop.getAttribute("ogsize"), 0.05, Tween.QUAD_IN_OUT); // set it back to its original size
            }

            Object hasPressCover = released.getAttribute("pressCover"); // check if element wants to slightly dim when pressed
            if (hasPressCover != null) { // if so 
                UIImage pressCover = (UIImage)hasPressCover;
                pressCover.tweenImageTransparency(0f, 0.075, Tween.QUAD_IN_OUT); // fade it out so u cant see it anymore
            }

            Object isStartButton = released.getAttribute("startButton"); // if its a start button, we can handle its animation here
            if (isStartButton != null && released.containsPoint(e.getX(), e.getY())) { // check if the mouse is still on the button and is actually a start button
                transition.visible = true; // enable the transition frame
                startMenu.tweenSize(new Dim2(1.5, 0, 1.75, 0), 0.5, Tween.QUAD_IN_OUT); // zoom in the main menu stuff by 3x
                transition.tweenBackgroundTransparency(1f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> { // fade the transition in and then when it finishes
                    startMenu.visible = false; // set start menu visibility to false
                    startMenu.size = new Dim2(0.5, 0, 0.55, 0); // reset its size
                    transition.tweenBackgroundTransparency(0f, 0.5, Tween.QUAD_IN_OUT).onFinish(() -> { // fade the transition back out and then when that finishes
                        transition.visible = false; // set its visibility to false
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
            if (previouslyHovering != null) { // so we gotta reset whatever we were hovering over originally
                Object hasDrop = previouslyHovering.getAttribute("drop"); // if it has a dropshadow
                if (hasDrop != null) {
                    UIImage drop = (UIImage)hasDrop; // get the dropshadow from the attribute value
                    drop.tweenImageTransparency(0f, 0.1, Tween.QUAD_IN_OUT); // fade it out
                    drop.tweenPosition(new Dim2(), 0.1, Tween.QUAD_IN_OUT); // reset its position
                }
                Object hasAnimOnHover = previouslyHovering.getAttribute("animOnHover"); // if it wants to resize on hover
                Object hasAnimOnHoverRot = previouslyHovering.getAttribute("animOnHoverRot"); // if it wants to resize and rotate on hover
                if (hasAnimOnHover != null || hasAnimOnHoverRot != null) {
                    UIElement container = (UIElement)hasAnimOnHover; // get the container
                    if (hasAnimOnHoverRot != null) { // if it wants to rotate as well
                        container = (UIElement)hasAnimOnHoverRot;
                        container.tweenRotation(0, 0.1, Tween.QUAD_IN_OUT); // rotate it back to original
                    }
                    container.tweenSize((Dim2)container.getAttribute("ogsize"), 0.1, Tween.QUAD_IN_OUT); // rotate back to original size
                }
            }
            if (nowHovering != null) { // if we're hovering over something now
                Object hasDrop = nowHovering.getAttribute("drop"); // if it has a drop shadow
                if (hasDrop != null) {
                    UIImage drop = (UIImage)hasDrop;
                    drop.tweenImageTransparency(0.2f, 0.1, Tween.QUAD_IN_OUT); // fade it in 50%
                    drop.tweenPosition(new Dim2(0.01, 0, 0.1, 0), 0.1, Tween.QUAD_IN_OUT); // move it down and right a little
                }
                Object hasAnimOnHover = nowHovering.getAttribute("animOnHover");
                Object hasAnimOnHoverRot = nowHovering.getAttribute("animOnHoverRot");
                if (hasAnimOnHover != null || hasAnimOnHoverRot != null) {
                    UIElement container = (UIElement)hasAnimOnHover;
                    if (hasAnimOnHoverRot != null) {
                        container = (UIElement)hasAnimOnHoverRot;
                        container.tweenRotation((Math.random() * 20) - 10, 0.1, Tween.QUAD_IN_OUT); // rotate it when hovered by a random number between -10 to 10
                    }
                    container.tweenSize((Dim2)container.getAttribute("hoversize"), 0.1, Tween.QUAD_IN_OUT);
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) { }
}
