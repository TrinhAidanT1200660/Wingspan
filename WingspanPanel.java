
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
            if (released.containsPoint(e.getX(), e.getY())) {
                if (released.getAttribute("startButton") != null) { // if its a start button, we can start game here
                    startMenu.tweenSize(new Dim2(1.5, 0, 1.8, 0), 0.5, Tween.QUAD_IN_OUT); // zoom in the main menu stuff by 3x
                    currentGame = new Game(released == UIElement.getByName("CompetitiveButtonBg"));
                    playTransition(() -> {
                        ArrayList<Bird> randomBirds = currentGame.pullRandomBirds(5);
                        for (int i = 0; i < randomBirds.size(); i++) {
                            String imageFileString = randomBirds.get(i).getImage();
                            ImageHandler.setGroup(imageFileString, "BirdChoiceCards");
                            UIImage birdImage = (UIImage)(UIElement.getByName("Bird" + i));
                            birdImage.setAttribute("selectionValue", randomBirds.get(i));
                            birdImage.setImagePath(imageFileString);
                        }
                        ImageHandler.setGroup("foods/berries.png", "Foods");
                        ImageHandler.setGroup("foods/fish.png", "Foods");
                        ImageHandler.setGroup("foods/rat.png", "Foods");
                        ImageHandler.setGroup("foods/seed.png", "Foods");
                        ImageHandler.setGroup("foods/worm.png", "Foods");
                        ImageHandler.loadGroup("BirdChoiceCards");
                        ImageHandler.loadGroup("Foods");
                        startMenu.visible = false;
                        ImageHandler.clearGroupCache("StartMenu");
                        resourceChoosingScreen.visible = true;
                        resourceChoosingScreen.size = new Dim2().full().dilate(3);
                        startMenu.size = new Dim2(0.5, 0, 0.6, 0);
                        resourceChoosingScreen.tweenSize(new Dim2().full(), 0.4, Tween.QUAD_IN_OUT);
                    });
                } else if (released.getAttribute("birdChoice") != null || released.getAttribute("foodChoice") != null || released.getAttribute("bonusChoice") != null) {
                    currentGame.toggleSelect(released);
                    boolean canContinue = currentGame.canContinueResources();
                    UIElement continueButton = UIElement.getByName("ContinueResourcesButtonBg");
                    continueButton.setAttribute("Clickable", canContinue);
                    continueButton.backgroundColor = canContinue ? Color.white : Color.lightGray;
                }

                // resources screen
                if (released == UIElement.getByName("ContinueResourcesButtonBg")) {
                    Object ready = UIElement.getByName("ContinueResourcesButtonBg").getAttribute("Clickable");
                    if (ready != null && (boolean)ready) {
                        playTransition((Runnable)() -> {
                            currentGame.continueSelection();
                            System.out.println("bout to get some");
                            ArrayList<BonusCard> cards = currentGame.pullRandomBonusCards(2);
                            System.out.println("ok got them " + cards);
                            BonusCard firstBonus = cards.get(0);
                            BonusCard secondBonus = cards.get(1);
                            ImageHandler.setGroup("bonus/back_of_bonus.png", "Bonus");
                            ImageHandler.loadGroup("Bonus");
                            for (int i = 0; i < 2; i++) {
                                UIImage bonusImage = (UIImage)(UIElement.getByName("Bonus" + i));
                                bonusImage.setAttribute("selectionValue", cards.get(i));
                                bonusImage.setImagePath("bonus/back_of_bonus.png");
                            }
                            UIElement.getByName("ChoosableBirdsContainer").visible = false;
                            UIElement.getByName("ChoosableFoodsContainer").visible = false;
                            UIElement.getByName("ChoosableBonusesContainer").visible = true;
                        });
                    }
                }
            }
            Object hasAnimOnPress = released.getAttribute("animOnPress"); // check if the element wants to be animated when pressed
            if (hasAnimOnPress != null) { // if so
                UIElement drop = (UIElement) hasAnimOnPress;
                drop.tweenSize((Dim2) drop.getAttribute("ogsize"), 0.05, Tween.QUAD_IN_OUT); // set it back to its original size
            }

            Object hasPressCover = released.getAttribute("pressCover"); // check if element wants to slightly dim when pressed
            if (hasPressCover != null) { // if so
                UIFrame pressCover = (UIFrame) hasPressCover;
                pressCover.tweenBackgroundTransparency(0f, 0.075, Tween.QUAD_IN_OUT); // fade it out so u cant see it anymore
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

        UIFrame blackBackground = new UIFrame("BackgroundFrame", this); // background image of the sky
        blackBackground.setZIndex(-35);
        blackBackground.size.full(); // take 100% of the screen
        blackBackground.backgroundColor = Color.black;

        UIImage backgroundFrame = new UIImage("Background", this); // background image of the sky
        backgroundFrame.setImagePath("images/wingspan_background.png"); // setting the picture to the BufferedImage of the
        // sky
        backgroundFrame.size.full(); // take 100% of the screen
        backgroundFrame.backgroundTransparency = 0f; // no background color
        backgroundFrame.setImageFillType(UIImage.CROP_IMAGE); // setting it so even if screen is an awkward size the
        // picture will crop itself to fit the whole screen
        backgroundFrame.imageTransparency = 0.9f;

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
        birdImage.setImagePath("images/wingspan_bird.png"); // setting picture to the BufferedImage of the bird
        ImageHandler.setGroup("images/wingspan_bird.png", "StartMenu");
        birdImage.size.full(); // 100% size of its container
        birdImage.anchorPoint.center(); // centered anchor point
        birdImage.position.center(); // centered position
        birdImage.backgroundTransparency = 0f; // no background
        birdImage.setImageFillType(UIImage.FIT_IMAGE); // setting it so even if screen is an awkward size the picture
        // will fit to the biggest size it can without stretching
        birdImage.setParent(birdContainer); // setting the startscreen frame as its parent
        animOnHoverRot(birdHitbox, birdImage);
        animOnPress(birdHitbox, birdImage);

        // buttons on the start screen
        // peaceful button
        UIFrame peacefulContainer = new UIFrame("PeacefulContainer", this);
        peacefulContainer.backgroundTransparency = 0f;
        peacefulContainer.size = new Dim2(0.35, 0, 0.117, 0);
        peacefulContainer.anchorPoint = new Vector2(0.5, 0.5);
        peacefulContainer.position = new Dim2(0.825, 0, 0.5, 0);
        peacefulContainer.setParent(startMenu);

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
        animDropshadow(peacefulButtonBg, peacefulDropshadow);
        animOnHoverRot(peacefulButtonBg, peacefulContainer);
        animOnPress(peacefulButtonBg, peacefulContainer);
        pressCover(peacefulButtonBg, peacefulButtonCover);
        peacefulButtonBg.setAttribute("startButton", true);

        // competitive button
        UIFrame competitiveContainer = new UIFrame("CompetitiveContainer", this);
        competitiveContainer.backgroundTransparency = 0f;
        competitiveContainer.size = new Dim2(0.35, 0, 0.117, 0);
        competitiveContainer.anchorPoint = new Vector2(0.5, 0.5);
        competitiveContainer.position = new Dim2(0.825, 0, 0.67, 0);
        competitiveContainer.setParent(startMenu);

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
        animDropshadow(competitiveButtonBg, competitiveDropshadow);
        animOnHoverRot(competitiveButtonBg, competitiveContainer);
        animOnPress(competitiveButtonBg, competitiveContainer);
        pressCover(competitiveButtonBg, competitiveButtonCover);
        competitiveButtonBg.setAttribute("startButton", true);

        // title image
        UIImage title = new UIImage("Title", this);
        title.setImagePath("images/wingspan_title.png");
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
        resourceChoosingScreen.keepAspectRatio = true;
        resourceChoosingScreen.backgroundTransparency = 0; // invisible

        UIText playerChoosingTitle = new UIText("PlayerChoosingTitle", this);
        playerChoosingTitle.backgroundTransparency = 0f;
        playerChoosingTitle.textColor = Color.white;
        playerChoosingTitle.textScaled = true;
        playerChoosingTitle.size = new Dim2(0.25, 0, 0.1, 0);
        playerChoosingTitle.position = new Dim2(0.5, 0, 0.07, 0);
        playerChoosingTitle.anchorPoint = new Vector2(0.5, 0);
        playerChoosingTitle.text = "Player 1";
        playerChoosingTitle.setParent(resourceChoosingScreen);
        playerChoosingTitle.textStrokeColor = Color.BLACK;
        playerChoosingTitle.textStrokeTransparency = 1f;
        playerChoosingTitle.textStrokeThickness = new Dim(0.007, 0);

        UIFrame resourcesChoicesFrame = new UIFrame("ResourcesChoicesFrame", this);
        resourcesChoicesFrame.position = new Dim2(0.5, 0, 0.55, 0);
        resourcesChoicesFrame.anchorPoint.center();
        resourcesChoicesFrame.size = new Dim2(0.85, 0, 0.65, 0);
        resourcesChoicesFrame.keepAspectRatio = true;
        resourcesChoicesFrame.backgroundTransparency = 0f;
        resourcesChoicesFrame.setParent(resourceChoosingScreen);

        UIFrame choosableBonusesContainer = new UIFrame("ChoosableBonusesContainer", this);
        choosableBonusesContainer.visible = false;
        choosableBonusesContainer.position.center();
        choosableBonusesContainer.anchorPoint = new Vector2(0.5, 0.5);
        choosableBonusesContainer.size = new Dim2(1, 0, 0.7, 0);
        choosableBonusesContainer.backgroundTransparency = 0f;
        choosableBonusesContainer.setParent(resourcesChoicesFrame);

        ListLayout bonusChoicesLayout = new ListLayout();
        bonusChoicesLayout.direction = ListLayout.HORIZONTAL;
        bonusChoicesLayout.verticalAlignment = ListLayout.CENTER;
        bonusChoicesLayout.horizontalAlignment = ListLayout.CENTER;
        bonusChoicesLayout.spacing = new Dim(0.005, 0);
        choosableBonusesContainer.layout = bonusChoicesLayout;

        for (int i = 0; i < 2; i++) {
            UIFrame chooseableBonusContainer = new UIFrame("BonusContainer" + i, this);
            chooseableBonusContainer.backgroundTransparency = 0f;
            chooseableBonusContainer.size = new Dim2(0.185, 0, 0.82, 0);
            chooseableBonusContainer.setParent(choosableBonusesContainer);

            UIImage bonus = new UIImage("Bonus" + i, this);
            bonus.size.full().dilate(0.85);
            bonus.backgroundTransparency = 0f;
            bonus.position.center();
            bonus.anchorPoint.center();
            bonus.setBrightness(0.6f);
            bonus.setAttribute("bonusChoice", true);
            bonus.setImageFillType(UIImage.FIT_IMAGE);
            bonus.setParent(chooseableBonusContainer);
            animOnHover(bonus, bonus);
            animOnPress(bonus, bonus);
            bonus.setAttribute("Select", (Runnable)() -> {
                bonus.setBrightness(1f);
                Dim2 newSize = new Dim2().full().dilate(0.95);
                bonus.setAttribute("ogsize", newSize);
                bonus.setAttribute("hoversize", newSize.clone().dilate(1.1));
                bonus.setAttribute("presssize", newSize.clone().dilate(0.85));
            });
            bonus.setAttribute("Deselect", (Runnable)() -> {
                bonus.setBrightness(0.6f);
                Dim2 newSize = new Dim2().full().dilate(0.85);
                bonus.setAttribute("ogsize", newSize);
                bonus.setAttribute("hoversize", newSize.clone().dilate(1.1));
                bonus.setAttribute("presssize", newSize.clone().dilate(0.85));
                bonus.tweenSize(newSize, 0.1, Tween.QUAD_IN_OUT);
            });
        }

        UIFrame choosableBirdsContainer = new UIFrame("ChoosableBirdsContainer", this);
        choosableBirdsContainer.position = new Dim2(0.5, 0, 0, 0);
        choosableBirdsContainer.anchorPoint = new Vector2(0.5, 0);
        choosableBirdsContainer.size = new Dim2(1, 0, 0.7, 0);
        choosableBirdsContainer.backgroundTransparency = 0f;
        choosableBirdsContainer.setParent(resourcesChoicesFrame);

        ListLayout birdChoicesLayout = new ListLayout();
        birdChoicesLayout.direction = ListLayout.HORIZONTAL;
        birdChoicesLayout.verticalAlignment = ListLayout.CENTER;
        birdChoicesLayout.horizontalAlignment = ListLayout.CENTER;
        birdChoicesLayout.spacing = new Dim(0.005, 0);
        choosableBirdsContainer.layout = birdChoicesLayout;

        for (int i = 0; i < 5; i++) {
            UIFrame chooseableBirdContainer = new UIFrame("BirdContainer" + i, this);
            chooseableBirdContainer.backgroundTransparency = 0f;
            chooseableBirdContainer.size = new Dim2(0.185, 0, 0.82, 0);
            chooseableBirdContainer.setParent(choosableBirdsContainer);

            UIImage bird = new UIImage("Bird" + i, this);
            bird.size.full().dilate(0.85);
            bird.backgroundTransparency = 0f;
            bird.position.center();
            bird.anchorPoint.center();
            bird.setBrightness(0.6f);
            bird.setAttribute("birdChoice", true);
            bird.setImageFillType(UIImage.FIT_IMAGE);
            bird.setParent(chooseableBirdContainer);
            animOnHover(bird, bird);
            animOnPress(bird, bird);
            bird.setAttribute("Select", (Runnable)() -> {
                bird.setBrightness(1f);
                Dim2 newSize = new Dim2().full().dilate(0.95);
                bird.setAttribute("ogsize", newSize);
                bird.setAttribute("hoversize", newSize.clone().dilate(1.1));
                bird.setAttribute("presssize", newSize.clone().dilate(0.85));
            });
            bird.setAttribute("Deselect", (Runnable)() -> {
                bird.setBrightness(0.6f);
                Dim2 newSize = new Dim2().full().dilate(0.85);
                bird.setAttribute("ogsize", newSize);
                bird.setAttribute("hoversize", newSize.clone().dilate(1.1));
                bird.setAttribute("presssize", newSize.clone().dilate(0.85));
                bird.tweenSize(newSize, 0.1, Tween.QUAD_IN_OUT);
            });
        }

        UIFrame continueResourcesHolder = new UIFrame("ContinueResourcesHolder", this);
        continueResourcesHolder.backgroundTransparency = 0f;
        continueResourcesHolder.size = new Dim2(0.18, 0, 0.1, 0).dilate(1.1);
        continueResourcesHolder.anchorPoint = new Vector2(1, 1);
        continueResourcesHolder.position = new Dim2(0.95, 0, 0.95, 0);
        continueResourcesHolder.setParent(resourcesChoicesFrame);

        UIFrame continueResourcesContainer = new UIFrame("ContinueResourcesContainer", this);
        continueResourcesContainer.backgroundTransparency = 0f;
        continueResourcesContainer.size.full();
        continueResourcesContainer.anchorPoint.center();
        continueResourcesContainer.position.center();
        continueResourcesContainer.setParent(continueResourcesHolder);

        UIFrame continueResourcesButtonBg = new UIFrame("ContinueResourcesButtonBg", this);
        continueResourcesButtonBg.size.full();
        continueResourcesButtonBg.backgroundColor = Color.lightGray;
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
        continueResourcesButton.backgroundTransparency = 0f;
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
        continueResourcesDropshadow.setParent(continueResourcesContainer);
        animDropshadow(continueResourcesButtonBg, continueResourcesDropshadow);
        animOnHoverRot(continueResourcesButtonBg, continueResourcesContainer);
        animOnPress(continueResourcesButtonBg, continueResourcesContainer);
        pressCover(continueResourcesButtonBg, continueResourcesButtonCover);

        UIFrame choosableFoodsContainer = new UIFrame("ChoosableFoodsContainer", this);
        choosableFoodsContainer.position = new Dim2(0.05, 0, 0.95, 0);
        choosableFoodsContainer.anchorPoint = new Vector2(0, 1);
        choosableFoodsContainer.size = new Dim2(0.25, 0, 0.1, 0).dilate(1.33);
        choosableFoodsContainer.backgroundTransparency = 0f;
        choosableFoodsContainer.setParent(resourcesChoicesFrame);

        ListLayout foodChoicesLayout = new ListLayout();
        foodChoicesLayout.direction = ListLayout.HORIZONTAL;
        foodChoicesLayout.verticalAlignment = ListLayout.CENTER;
        foodChoicesLayout.horizontalAlignment = ListLayout.LEFT;
        foodChoicesLayout.spacing = new Dim(0.005, 0);
        choosableFoodsContainer.layout = foodChoicesLayout;

        createFoodChoice("Berries");
        createFoodChoice("Fish");
        createFoodChoice("Worm");
        createFoodChoice("Seed");
        createFoodChoice("Rat");
    }

    public void createFoodChoice(String foodName) {
        UIElement choosableFoodsContainer = UIElement.getByName("ChoosableFoodsContainer");

        UIFrame choiceContainer = new UIFrame(foodName + "ChoiceContainer", this);
        choiceContainer.backgroundTransparency = 0f;
        choiceContainer.size = new Dim2(0.2, 0, 1, 0);
        choiceContainer.setParent(choosableFoodsContainer);

        UIImage choiceIcon = new UIImage(foodName + "ChoiceIcon", this);
        choiceIcon.setImagePath("foods/" + foodName.toLowerCase() + ".png");
        choiceIcon.backgroundTransparency = 0f;
        choiceIcon.size.full().dilate(0.7);
        choiceIcon.setBrightness(0.4f);
        choiceIcon.anchorPoint.center();
        choiceIcon.position.center();
        choiceIcon.setAttribute("foodChoice", true);
        choiceIcon.setAttribute("selectionValue", foodName.toLowerCase());
        choiceIcon.setParent(choiceContainer);
        animOnPress(choiceIcon, choiceIcon);
        animOnHover(choiceIcon, choiceIcon);
        choiceIcon.setAttribute("Select", (Runnable)() -> {
            choiceIcon.setBrightness(1f);
            Dim2 newSize = new Dim2().full().dilate(1.15);
            choiceIcon.setAttribute("ogsize", newSize);
            choiceIcon.setAttribute("hoversize", newSize.clone().dilate(1.1));
            choiceIcon.setAttribute("presssize", newSize.clone().dilate(0.85));
        });
        choiceIcon.setAttribute("Deselect", (Runnable)() -> {
            choiceIcon.setBrightness(0.4f);
            Dim2 newSize = new Dim2().full().dilate(0.7);
            choiceIcon.setAttribute("ogsize", newSize);
            choiceIcon.setAttribute("hoversize", newSize.clone().dilate(1.1));
            choiceIcon.setAttribute("presssize", newSize.clone().dilate(0.85));
            choiceIcon.tweenSize(newSize, 0.1, Tween.QUAD_IN_OUT);
        });
    }

    public void animOnHover(UIElement element, UIElement toAnimate) {
        element.setAttribute("animOnHover", toAnimate);
        if (toAnimate.getAttribute("ogsize") == null) toAnimate.setAttribute("ogsize", toAnimate.size.clone());
        toAnimate.setAttribute("hoversize", toAnimate.size.clone().dilate(1.15));
    }

    public void animOnHoverRot(UIElement element, UIElement toAnimate) {
        element.setAttribute("animOnHoverRot", toAnimate);
        if (toAnimate.getAttribute("ogsize") == null) toAnimate.setAttribute("ogsize", toAnimate.size.clone());
        toAnimate.setAttribute("hoversize", toAnimate.size.clone().dilate(1.15));
    }

    public void animOnPress(UIElement element, UIElement toAnimate) {
        element.setAttribute("animOnPress", toAnimate);
        if (toAnimate.getAttribute("ogsize") == null) toAnimate.setAttribute("ogsize", toAnimate.size.clone());
        toAnimate.setAttribute("presssize", toAnimate.size.clone().dilate(0.8));
    }

    public void pressCover(UIElement element, UIElement toAnimate) {
        element.setAttribute("pressCover", toAnimate);
    }

    public void animDropshadow(UIElement element, UIElement toAnimate) {
        element.setAttribute("drop", toAnimate);
    }
}

