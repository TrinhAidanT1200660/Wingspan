
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

public class WingspanPanel extends JPanel implements MouseListener, MouseMotionListener {
    public Game currentGame;
    private UIElement root, transition, startMenu, resourceChoosingScreen, birdContainer;
    private UIText loadingTitle;
    private UIImage loadingBird;

    public WingspanPanel() {
        initializeUI();
        // necessary for all our GUI, you can just ignore this
        root = UIElement.getRootForPanel(this);

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadResources();
        ImageHandler.loadGroup("StartMenu", () -> {
            Timer t = new Timer(1000, (e) -> {
                UIElement.getByName("ResourceChoosingScreen").visible = false;
                loadingTitle.tweenTextTransparency(0f, 0.4, Tween.QUAD_IN_OUT);
                transition.tweenBackgroundTransparency(0f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> {
                    transition.visible = false;
                });
                startMenu.tweenSize(new Dim2(0.5, 0, 0.6, 0), 0.4, Tween.QUAD_IN_OUT);
                animateBird();
            });
            t.setRepeats(false);
            t.start();
        });
    }

    public void playTransition(Runnable between) {
        transition.visible = true;
        transition.backgroundTransparency = 0f;
        loadingTitle.textTransparency = 0f;
        loadingTitle.tweenTextTransparency(1f, 0.4, Tween.QUAD_IN_OUT);
        transition.tweenBackgroundTransparency(1f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> {
            if (between != null) between.run();
            loadingTitle.tweenTextTransparency(0f, 0.4, Tween.QUAD_IN_OUT);
            transition.tweenBackgroundTransparency(0f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> { 
                transition.visible = false;
            });
        });
    }

    // loads all images in the requiredImages array into images hashmap (maybe
    // theres a better way to do this?)
    private void loadResources() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/resources/font.otf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(24f);
            GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
            graphics.registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        root.draw(g); // drawing everything on screen
    }

    @Override
    public void mousePressed(MouseEvent e) {
        RootMouseEvent event = root.handlePress(e);
        UIElement pressed = event.getElement(); // tells us what element was pressed (mouse button went down but not up)

        if (pressed != null) { // if we actually pressed something
            Object hasAnimOnPress = pressed.getAttribute("animOnPress"); // check if the element wants to be animated
            // when pressed
            if (hasAnimOnPress != null) { // if so
                UIElement container = (UIElement) hasAnimOnPress; // get the thing it wants to animate
                container.tweenSize((Dim2) container.getAttribute("presssize"), 0.05, Tween.QUAD_IN_OUT); // resize to
                // the size it
                // requested
            }

            Object hasPressCover = pressed.getAttribute("pressCover"); // check if element wants to slightly dim when
            // pressed
            if (hasPressCover != null) { // if so
                UIFrame pressCover = (UIFrame) hasPressCover; // get the cover
                pressCover.tweenBackgroundTransparency(0.2f, 0.075, Tween.QUAD_IN_OUT); // fade it in 20%
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        RootMouseEvent event = root.handleRelease(e);
        UIElement released = event.getElement(); // tells us what button was pressed on and then released on (so full
        // click)

        if (released != null) { // if we actually pressed and released something
            // we can do whatever with the button that was fully clicked here...
            Object hasAnimOnPress = released.getAttribute("animOnPress"); // check if the element wants to be animated
            // when pressed
            if (hasAnimOnPress != null) { // if so
                UIElement drop = (UIElement) hasAnimOnPress;
                drop.tweenSize((Dim2) drop.getAttribute("ogsize"), 0.05, Tween.QUAD_IN_OUT); // set it back to its
                // original size
            }

            Object hasPressCover = released.getAttribute("pressCover"); // check if element wants to slightly dim when
            // pressed
            if (hasPressCover != null) { // if so
                UIFrame pressCover = (UIFrame) hasPressCover;
                pressCover.tweenBackgroundTransparency(0f, 0.075, Tween.QUAD_IN_OUT); // fade it out so u cant see it
                // anymore
            }

            if (released.containsPoint(e.getX(), e.getY())) {
                Object isStartButton = released.getAttribute("startButton"); // if its a start button, we can handle its animation here
                if (isStartButton != null) { // check if the mouse is still on the button and is actually a start button
                    transition.visible = true; // enable the transition frame
                    startMenu.tweenSize(new Dim2(1.5, 0, 1.8, 0), 0.5, Tween.QUAD_IN_OUT); // zoom in the main menu stuff by 3x
                    currentGame = new Game(released == UIElement.getByName("CompetitiveButtonBg"));
                    playTransition(() -> {
                        ArrayList<Bird> randomBirds = currentGame.pullRandomBirds();
                        for (int i = 0; i < randomBirds.size(); i++) {
                            String imageFileString = randomBirds.get(i).getImage();
                            ImageHandler.setGroup(imageFileString, "BirdChoiceCards");
                            ((UIImage)(UIElement.getByName("Bird" + i))).imagePath = imageFileString;
                        }
                        ImageHandler.loadGroup("BirdChoiceCards");
                        startMenu.visible = false;
                        ImageHandler.clearGroupCache("StartMenu");
                        resourceChoosingScreen.visible = true;
                        resourceChoosingScreen.size = new Dim2().full().dilate(3);
                        startMenu.size = new Dim2(0.5, 0, 0.6, 0);
                        resourceChoosingScreen.tweenSize(new Dim2().full(), 0.4, Tween.QUAD_IN_OUT);
                    });
                }

                // resources screen
                if (released == UIElement.getByName("ResourcesContinueButton")) {

                }
            }
        }

        mouseMoved(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
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
                    UIFrame drop = (UIFrame) hasDrop; // get the dropshadow from the attribute value
                    drop.tweenBackgroundTransparency(0f, 0.1, Tween.QUAD_IN_OUT); // fade it out
                    drop.tweenPosition(new Dim2(), 0.1, Tween.QUAD_IN_OUT); // reset its position
                }
                Object hasAnimOnHover = previouslyHovering.getAttribute("animOnHover"); // if it wants to resize on
                // hover
                Object hasAnimOnHoverRot = previouslyHovering.getAttribute("animOnHoverRot"); // if it wants to resize
                // and rotate on hover
                if (hasAnimOnHover != null || hasAnimOnHoverRot != null) {
                    UIElement container = (UIElement) hasAnimOnHover; // get the container
                    if (hasAnimOnHoverRot != null) { // if it wants to rotate as well
                        container = (UIElement) hasAnimOnHoverRot;
                        container.tweenRotation(0, 0.1, Tween.QUAD_IN_OUT); // rotate it back to original
                    }
                    container.tweenSize((Dim2) container.getAttribute("ogsize"), 0.1, Tween.QUAD_IN_OUT); // rotate back
                    // to original
                    // size
                }
            }
            if (nowHovering != null) { // if we're hovering over something now
                Object hasDrop = nowHovering.getAttribute("drop"); // if it has a drop shadow
                if (hasDrop != null) {
                    UIFrame drop = (UIFrame) hasDrop;
                    drop.tweenBackgroundTransparency(0.2f, 0.1, Tween.QUAD_IN_OUT); // fade it in 50%
                    drop.tweenPosition(new Dim2(0.015, 0, 0.075, 0), 0.1, Tween.QUAD_IN_OUT); // move it down and right
                    // a little
                }
                Object hasAnimOnHover = nowHovering.getAttribute("animOnHover");
                Object hasAnimOnHoverRot = nowHovering.getAttribute("animOnHoverRot");
                if (hasAnimOnHover != null || hasAnimOnHoverRot != null) {
                    UIElement container = (UIElement) hasAnimOnHover;
                    if (hasAnimOnHoverRot != null) {
                        container = (UIElement) hasAnimOnHoverRot;
                        container.tweenRotation((Math.random() * 20) - 10, 0.1, Tween.QUAD_IN_OUT); // rotate it when
                        // hovered by a
                        // random number
                        // between -10 to 10
                    }
                    container.tweenSize((Dim2) container.getAttribute("hoversize"), 0.1, Tween.QUAD_IN_OUT);
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    public void animateBird() {
        if (startMenu.visible) {
            birdContainer.tweenPosition(new Dim2(-0.1, 0, 0.55, 0), 1, Tween.QUAD_IN_OUT).onFinish(() -> {
                birdContainer.tweenPosition(new Dim2(-0.1, 0, 0.45, 0), 1, Tween.QUAD_IN_OUT).onFinish(() -> {
                    animateBird();
                });
            });
        }
    }

    public void initializeUI() {
        UIText.defaultFontName = "Cardenio Modern Bold";

        UIImage backgroundFrame = new UIImage("Background", this); // background image of the sky
        backgroundFrame.imagePath = "images/wingspan_background.png"; // setting the picture to the BufferedImage of the
        // sky
        backgroundFrame.size.full(); // take 100% of the screen
        backgroundFrame.backgroundTransparency = 0f; // no background color
        backgroundFrame.setImageFillType(UIImage.CROP_IMAGE); // setting it so even if screen is an awkward size the
        // picture will crop itself to fit the whole screen

        UIFrame startScreen = new UIFrame("StartScreen", this); // invisible frame holding startMenu
        startScreen.anchorPoint.center(); // centered anchor point
        startScreen.position.center(); // center in middle
        startScreen.size.full(); // entire screen
        startScreen.keepAspectRatio = true; // keep aspect ratio so size remains consistent
        startScreen.backgroundTransparency = 0; // invisible

        startMenu = new UIFrame("StartMenu", this); // ok this is actually the invisible frame holding all the buttons
        // for the start screen (child of startScreen)
        startMenu.anchorPoint.center(); // centered anchor point
        startMenu.position = new Dim2(0.5, 0, 0.55, 0); // positioned almost in the center but slightly more down to
        // make space for title
        startMenu.size = new Dim2(1.5, 0, 1.8, 0); // 50% width of screen, 60% height of screen
        startMenu.backgroundTransparency = 0; // no background
        startMenu.setZIndex(1); // increase layer its on
        startMenu.setParent(startScreen); // parent it to the invisible frame startScreen

        birdContainer = new UIFrame("BirdContainer", this); // invisible frame holding birdImage and birdHitbox (for
        // petting)
        birdContainer.size = new Dim2(0.87, 0, 1, 0); // 87% width of the invisible start menu, 100% height
        birdContainer.anchorPoint = new Vector2(0, 0.5); // left middle
        birdContainer.position = new Dim2(-0.1, 0, 0.45, 0); // slightly to the left of the start screen by 10% and
        // centered in the middle vertically
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
        birdImage.imagePath = "images/wingspan_bird.png"; // setting picture to the BufferedImage of the bird
        ImageHandler.setGroup("images/wingspan_bird.png", "StartMenu");
        birdImage.size.full(); // 100% size of its container
        birdImage.anchorPoint.center(); // centered anchor point
        birdImage.position.center(); // centered position
        birdImage.backgroundTransparency = 0f; // no background
        birdImage.setImageFillType(UIImage.FIT_IMAGE); // setting it so even if screen is an awkward size the picture
        // will fit to the biggest size it can without stretching
        birdImage.setParent(birdContainer); // setting the startscreen frame as its parent
        birdImage.setAttribute("ogsize", new Dim2().full()); // original size, 100%
        birdImage.setAttribute("hoversize", new Dim2().full().dilate(1.1)); // size when hovered, 110%
        birdImage.setAttribute("presssize", new Dim2().full().dilate(0.8)); // size when pressing down, 80%
        birdHitbox.setAttribute("animOnHoverRot", birdImage); // bird hitbox: when hovered over, it'll resize and rotate
        // the birdImage
        birdHitbox.setAttribute("animOnPress", birdImage); // bird hitbox: when pressed, it'll resize birdImage

        // buttons on the start screen
        // peaceful button
        UIFrame peacefulContainer = new UIFrame("PeacefulContainer", this);
        peacefulContainer.backgroundTransparency = 0f;
        peacefulContainer.size = new Dim2(0.35, 0, 0.117, 0);
        peacefulContainer.anchorPoint = new Vector2(0.5, 0.5);
        peacefulContainer.position = new Dim2(0.825, 0, 0.5, 0);
        peacefulContainer.setParent(startMenu);
        peacefulContainer.setAttribute("ogsize", peacefulContainer.size.clone());
        peacefulContainer.setAttribute("hoversize", peacefulContainer.size.clone().dilate(1.15));
        peacefulContainer.setAttribute("presssize", peacefulContainer.size.clone().dilate(0.8));

        UIFrame peacefulButtonBg = new UIFrame("PeacefulButtonBg", this);
        peacefulButtonBg.size.full();
        peacefulButtonBg.borderRadius = new Dim(0.4, 0);
        peacefulButtonBg.setParent(peacefulContainer);

        UIText peacefulButton = new UIText("PeacefulButton", this);
        peacefulButton.ignore = true;
        peacefulButton.textScaled = true;
        peacefulButton.text = "PEACEFUL";
        peacefulButton.textColor = new Color(55, 164, 200);
        peacefulButton.size = peacefulButton.size.full().dilate(0.75);
        peacefulButton.position.center();
        peacefulButton.anchorPoint.center();
        peacefulButton.borderRadius = new Dim(0.4, 0);
        peacefulButton.setParent(peacefulButtonBg);

        UIFrame peacefulButtonCover = new UIFrame("PeacefulButtonCover", this);
        peacefulButtonCover.backgroundTransparency = 0f;
        peacefulButtonCover.backgroundColor = Color.black;
        peacefulButtonCover.ignore = true;
        peacefulButtonCover.borderRadius = new Dim(0.4, 0);
        peacefulButtonCover.size.full();
        peacefulButtonCover.setZIndex(2);
        peacefulButtonCover.setParent(peacefulButtonBg);

        UIFrame peacefulDropshadow = new UIFrame("PeacefulDropshadow", this);
        peacefulDropshadow.backgroundTransparency = 0f;
        peacefulDropshadow.backgroundColor = Color.black;
        peacefulDropshadow.ignore = true;
        peacefulDropshadow.borderRadius = new Dim(0.4, 0);
        peacefulDropshadow.size.full();
        peacefulDropshadow.setZIndex(-1);
        peacefulDropshadow.setParent(peacefulContainer);
        peacefulButtonBg.setAttribute("drop", peacefulDropshadow);
        peacefulButtonBg.setAttribute("animOnHoverRot", peacefulContainer);
        peacefulButtonBg.setAttribute("animOnPress", peacefulContainer);
        peacefulButtonBg.setAttribute("pressCover", peacefulButtonCover);
        peacefulButtonBg.setAttribute("startButton", true);

        // competitive button
        UIFrame competitiveContainer = new UIFrame("CompetitiveContainer", this);
        competitiveContainer.backgroundTransparency = 0f;
        competitiveContainer.size = new Dim2(0.35, 0, 0.117, 0);
        competitiveContainer.anchorPoint = new Vector2(0.5, 0.5);
        competitiveContainer.position = new Dim2(0.825, 0, 0.67, 0);
        competitiveContainer.setParent(startMenu);
        competitiveContainer.setAttribute("ogsize", competitiveContainer.size.clone());
        competitiveContainer.setAttribute("hoversize", competitiveContainer.size.clone().dilate(1.15));
        competitiveContainer.setAttribute("presssize", competitiveContainer.size.clone().dilate(0.8));

        UIFrame competitiveButtonBg = new UIFrame("CompetitiveButtonBg", this);
        competitiveButtonBg.size.full();
        competitiveButtonBg.borderRadius = new Dim(0.4, 0);
        competitiveButtonBg.setParent(competitiveContainer);

        UIText competitiveButton = new UIText("CompetitiveButton", this);
        competitiveButton.ignore = true;
        competitiveButton.textScaled = true;
        competitiveButton.text = "COMPETITIVE";
        competitiveButton.textColor = new Color(113, 184, 75);
        competitiveButton.size = competitiveButton.size.full().dilate(0.75);
        competitiveButton.position.center();
        competitiveButton.anchorPoint.center();
        competitiveButton.borderRadius = new Dim(0.4, 0);
        competitiveButton.setParent(competitiveButtonBg);

        UIFrame competitiveButtonCover = new UIFrame("CompetitiveButtonCover", this);
        competitiveButtonCover.backgroundTransparency = 0f;
        competitiveButtonCover.backgroundColor = Color.black;
        competitiveButtonCover.ignore = true;
        competitiveButtonCover.borderRadius = new Dim(0.4, 0);
        competitiveButtonCover.size.full();
        competitiveButtonCover.setZIndex(2);
        competitiveButtonCover.setParent(competitiveButtonBg);

        UIFrame competitiveDropshadow = new UIFrame("CompetitiveDropshadow", this);
        competitiveDropshadow.backgroundTransparency = 0f;
        competitiveDropshadow.backgroundColor = Color.black;
        competitiveDropshadow.ignore = true;
        competitiveDropshadow.borderRadius = new Dim(0.4, 0);
        competitiveDropshadow.size.full();
        competitiveDropshadow.setZIndex(-1);
        competitiveDropshadow.setParent(competitiveContainer);
        competitiveButtonBg.setAttribute("drop", competitiveDropshadow);
        competitiveButtonBg.setAttribute("animOnHoverRot", competitiveContainer);
        competitiveButtonBg.setAttribute("animOnPress", competitiveContainer);
        competitiveButtonBg.setAttribute("pressCover", competitiveButtonCover);
        competitiveButtonBg.setAttribute("startButton", true);

        // title image
        UIImage title = new UIImage("Title", this);
        title.imagePath = "images/wingspan_title.png";
        ImageHandler.setGroup("images/wingspan_title.png", "StartMenu");
        title.backgroundTransparency = 0f;
        title.size = new Dim2(1, 0, 0.4, 0);
        title.position = new Dim2(0, 0, -0.15, 0);
        title.setImageFillType(UIImage.FIT_IMAGE);
        title.setZIndex(-2);
        title.setParent(startMenu);

        // invisible black screen that fades in and out, for transitions
        transition = new UIFrame("Transition", this);
        transition.backgroundTransparency = 1f;
        transition.visible = true;
        transition.setZIndex(100000);
        transition.size.full();
        transition.position.center();
        transition.anchorPoint.center();
        transition.backgroundColor = Color.BLACK;

        loadingTitle = new UIText("LoadingTitle", this);
        loadingTitle.backgroundTransparency = 0f;
        loadingTitle.textColor = Color.white;
        loadingTitle.textScaled = true;
        loadingTitle.size = new Dim2(0.2, 0, 0.15, 0);
        loadingTitle.position.center();
        loadingTitle.anchorPoint.center();
        loadingTitle.text = "WINGSPAN";
        loadingTitle.setParent(transition);

        // next screen
        resourceChoosingScreen = new UIFrame("ResourceChoosingScreen", this); // invisible frame holding player choosing stuff
        resourceChoosingScreen.anchorPoint.center(); // centered anchor point
        resourceChoosingScreen.position.center(); // center in middle
        resourceChoosingScreen.size.full(); // entire screen
        resourceChoosingScreen.backgroundTransparency = 0; // invisible

        UIText playerChoosingTitle = new UIText("PlayerChoosingTitle", this);
        playerChoosingTitle.backgroundTransparency = 0f;
        playerChoosingTitle.textColor = Color.white;
        playerChoosingTitle.textScaled = true;
        playerChoosingTitle.size = new Dim2(0.25, 0, 0.1, 0);
        playerChoosingTitle.keepAspectRatio = true;
        playerChoosingTitle.position = new Dim2(0.5, 0, 0.07, 0);
        playerChoosingTitle.anchorPoint = new Vector2(0.5, 0);
        playerChoosingTitle.text = "Player 1";
        playerChoosingTitle.setParent(resourceChoosingScreen);
        playerChoosingTitle.textStrokeColor = Color.BLACK;
        playerChoosingTitle.textStrokeTransparency = 1f;
        playerChoosingTitle.textStrokeThickness = new Dim(0.007, 0);

        UIFrame choosableBirdsContainer = new UIFrame("ChoosableBirdsContainer", this);
        choosableBirdsContainer.position = new Dim2(0.5, 0, 0.45, 0);
        choosableBirdsContainer.anchorPoint.center();
        choosableBirdsContainer.size = new Dim2(0.85, 0, 0.5, 0);
        choosableBirdsContainer.keepAspectRatio = true;
        choosableBirdsContainer.backgroundTransparency = 0f;
        choosableBirdsContainer.setParent(resourceChoosingScreen);

        for (int i = 0; i < 5; i++) {
            UIImage bird = new UIImage("Bird" + i, this);
            bird.position = new Dim2(Math.max(0, 0.2 * i), 0, 0.5, 0);
            bird.size = new Dim2(0.2, 0, 0.77, 0);
            bird.backgroundTransparency = 0f;
            bird.anchorPoint = new Vector2(0, 0.5);
            bird.setImageFillType(UIImage.FIT_IMAGE);
            bird.imagePath = "birds/back_of_bird.png";
            bird.setParent(choosableBirdsContainer);
        }

        UIFrame continueResourcesContainer = new UIFrame("ContinueResourcesContainer", this);
        continueResourcesContainer.backgroundTransparency = 0f;
        continueResourcesContainer.size = new Dim2(0.35, 0, 0.117, 0);
        continueResourcesContainer.anchorPoint = new Vector2(1, 1);
        continueResourcesContainer.position = new Dim2(0.85, 0, 0.85, 0);
        continueResourcesContainer.setParent(resourceChoosingScreen);
        continueResourcesContainer.setAttribute("ogsize", continueResourcesContainer.size.clone());
        continueResourcesContainer.setAttribute("hoversize", continueResourcesContainer.size.clone().dilate(1.15));
        continueResourcesContainer.setAttribute("presssize", continueResourcesContainer.size.clone().dilate(0.8));

        UIFrame continueResourcesButtonBg = new UIFrame("ContinueResourcesButtonBg", this);
        continueResourcesButtonBg.size.full();
        continueResourcesButtonBg.borderRadius = new Dim(0.4, 0);
        continueResourcesButtonBg.setParent(continueResourcesContainer);

        UIText continueResourcesButton = new UIText("ContinueResourcesButton", this);
        continueResourcesButton.ignore = true;
        continueResourcesButton.textScaled = true;
        continueResourcesButton.text = "CONTINUE";
        continueResourcesButton.textColor = Color.black;
        continueResourcesButton.size = continueResourcesButton.size.full().dilate(0.75);
        continueResourcesButton.position.center();
        continueResourcesButton.anchorPoint.center();
        continueResourcesButton.borderRadius = new Dim(0.4, 0);
        continueResourcesButton.setParent(continueResourcesButtonBg);

        UIFrame continueResourcesButtonCover = new UIFrame("ContinueResourcesButtonCover", this);
        continueResourcesButtonCover.backgroundTransparency = 0f;
        continueResourcesButtonCover.backgroundColor = Color.black;
        continueResourcesButtonCover.ignore = true;
        continueResourcesButtonCover.borderRadius = new Dim(0.4, 0);
        continueResourcesButtonCover.size.full();
        continueResourcesButtonCover.setZIndex(2);
        continueResourcesButtonCover.setParent(continueResourcesButtonBg);

        UIFrame continueResourcesDropshadow = new UIFrame("ContinueResourcesDropshadow", this);
        continueResourcesDropshadow.backgroundTransparency = 0f;
        continueResourcesDropshadow.backgroundColor = Color.black;
        continueResourcesDropshadow.ignore = true;
        continueResourcesDropshadow.borderRadius = new Dim(0.4, 0);
        continueResourcesDropshadow.size.full();
        continueResourcesDropshadow.setZIndex(-1);
        continueResourcesDropshadow.setParent(continueResourcesDropshadow);
        continueResourcesButtonBg.setAttribute("drop", continueResourcesDropshadow);
        continueResourcesButtonBg.setAttribute("animOnHoverRot", continueResourcesContainer);
        continueResourcesButtonBg.setAttribute("animOnPress", continueResourcesContainer);
        continueResourcesButtonBg.setAttribute("pressCover", continueResourcesButtonCover);
    }
}
