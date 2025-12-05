
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.Timer;

public class WingspanPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    public Game currentGame;
    private UIElement root, transition, startMenu, resourceChoosingScreen, gameScreen, chosenScreen, boardScreen, deckScreen, birdFeederScreen, handScreen, birdContainer, cyclingView, popupBackground, popupContainer, popupChoice1Frame, popupChoice2Frame;
    private UIText loadingTitle, popupPrompt, popupChoice1, popupChoice2;

    public WingspanPanel() {
        currentGame = new Game(this);
        initializeUI();
        // necessary for all our GUI, you can just ignore this
        root = UIElement.getRootForPanel(this);

        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
        loadResources();
        repaint();
        ImageHandler.loadGroup("StartMenu", () -> {
            boardScreen.visible = true;
            deckScreen.visible = true;
            birdFeederScreen.visible = true;
            handScreen.visible = true;
            Timer t = new Timer(1000, (e) -> {
                resourceChoosingScreen.visible = false;
                gameScreen.visible = false;
                UIElement.getByName("StartScreen").visible = true;
                boardScreen.visible = true;
                deckScreen.visible = false;
                birdFeederScreen.visible = false;
                handScreen.visible = false;
                loadingTitle.tweenTextTransparency(0f, 0.4, Tween.QUAD_IN_OUT);
                ((UIImage)UIElement.getByName("BirdSprite")).tweenImageTransparency(0f, 0.4, Tween.QUAD_IN_OUT);
                transition.tweenBackgroundTransparency(0f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> {
                    System.out.println("AIODSASIODAUIBSDOAUSYDAUSDBASUIDOB ASUIDO GASUIDGBASUIDG ASUID GASIUDG ASUIODG AUOISDG AUSIOD G");
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
        ((UIImage)UIElement.getByName("BirdSprite")).tweenImageTransparency(1f, 0.4, Tween.QUAD_IN_OUT);
        transition.tweenBackgroundTransparency(1f, 0.4, Tween.QUAD_IN_OUT).onFinish(() -> {
            if (between != null) between.run();
            loadingTitle.tweenTextTransparency(0f, 0.4, Tween.QUAD_IN_OUT);
            ((UIImage)UIElement.getByName("BirdSprite")).tweenImageTransparency(0f, 0.4, Tween.QUAD_IN_OUT);
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
        UIElement released = event.getElement(); // tells us what button was pressed on and then released on (so full click)
        if (released == null) return;
        System.out.println("Released on: " + released.getName());
        if (released != null && released.containsPoint(e.getX(), e.getY())) { // if we actually pressed and released something
            // we can do whatever with the button that was fully clicked here...
            currentGame.UIMouseReleased(event, released);

        }
        Object hasAnimOnPress = released.getAttribute("animOnPress"); // check if the element wants to be animated when pressed
        if (hasAnimOnPress != null) { // if so
            UIElement drop = (UIElement) hasAnimOnPress;
            drop.tweenSize((Dim2) drop.getAttribute("ogsize"), 0.05, Tween.QUAD_IN_OUT); // set it back to its original size
        }
        Object hasPressCover = released.getAttribute("pressCover"); // check if element wants to slightly dim when pressed
        if (released.getAttribute("pressCover") != null) { // if so
            UIFrame pressCover = (UIFrame) hasPressCover;
            pressCover.tweenBackgroundTransparency(0f, 0.075, Tween.QUAD_IN_OUT); // fade it out so u cant see it anymore
        }

        mouseMoved(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        root.handleClick(e);
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
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    @Override
    public void keyReleased(KeyEvent e) {
    	currentGame.UIKeyReleased(e);
    }
    
    @Override
    public void keyPressed(KeyEvent e) { }

    //to be called from back end
    public void clickedResource(RootMouseEvent event, UIElement released, boolean canContinue)
    {
        UIElement continueButton = UIElement.getByName("ContinueResourcesButtonBg");
        continueButton.setAttribute("Clickable", canContinue);
        continueButton.backgroundColor = canContinue ? Color.white : Color.lightGray;
    }

    //to be called from back end
    public void clickedStart(RootMouseEvent event, UIElement released)
    {
        ImageHandler.setGroup("foods/berry.png", "Foods");
        ImageHandler.setGroup("foods/fish.png", "Foods");
        ImageHandler.setGroup("foods/rat.png", "Foods");
        ImageHandler.setGroup("foods/seed.png", "Foods");
        ImageHandler.setGroup("foods/worm.png", "Foods");
        ImageHandler.loadGroup("BirdChoiceCards");
        ImageHandler.loadGroup("Bonus");
        ImageHandler.loadGroup("Foods");
        startMenu.visible = false;
        ImageHandler.clearGroupCache("StartMenu");
        resourceChoosingScreen.visible = true;
        resourceChoosingScreen.size = new Dim2().full().dilate(3);
        startMenu.size = new Dim2(0.5, 0, 0.6, 0);
        resourceChoosingScreen.tweenSize(new Dim2().full(), 0.4, Tween.QUAD_IN_OUT);
    }
    
    //to be called from back end
    public void clickedResourceContinue(RootMouseEvent event, UIElement released, boolean screenToShow)
    {
        UIElement.getByName("ChoosableBirdsContainer").visible = screenToShow;
        UIElement.getByName("ChoosableFoodsContainer").visible = screenToShow;
        UIElement.getByName("ChoosableBonusesContainer").visible = !screenToShow;
        
        UIElement continueButton = UIElement.getByName("ContinueResourcesButtonBg");
        continueButton.setAttribute("Clickable", false);
        continueButton.backgroundColor = Color.lightGray;
    }

    public void animateBird() {
        if (UIElement.performanceMode) return;
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
        backgroundFrame.setImagePath("images/wingspan_background.png"); // setting the picture to the BufferedImage of the sky
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

        UIFrame transitionContainer = new UIFrame("TransitionContainer", this);
        transitionContainer.backgroundTransparency = 0f;
        transitionContainer.size = new Dim2(0.3, 0, 0.1, 0);
        transitionContainer.anchorPoint.center();
        transitionContainer.position.center();
        transitionContainer.keepAspectRatio = true;
        transitionContainer.setParent(transition);

        UIFrame birdSpriteContainer = new UIFrame("BirdSpriteContainer", this);
        birdSpriteContainer.size = new Dim2(0.2, 0, 1, 0);
        birdSpriteContainer.position = new Dim2(0.05, 0, 0, 0);
        birdSpriteContainer.backgroundTransparency = 0f;
        birdSpriteContainer.setParent(transitionContainer);

        UIImage birdSprite = new UIImage("BirdSprite", this);
        birdSprite.size.full();
        birdSprite.anchorPoint.center();
        birdSprite.position.center();
        birdSprite.rotation = -30;
        birdSprite.backgroundTransparency = 0f;
        birdSprite.setParent(birdSpriteContainer);
        birdSprite.setImagePath("images/bird_flying.png");
        birdSprite.setImageFillType(UIImage.SPRITE_ANIMATION);
        birdSprite.setSpriteSheet(640, 640);
        birdSprite.playSpriteAnimation(0.08, true);

        loadingTitle = new UIText("LoadingTitle", this);
        loadingTitle.backgroundTransparency = 0f;
        loadingTitle.textColor = Color.white;
        loadingTitle.textScaled = true;
        loadingTitle.position = new Dim2(0.27, 0, 0, 0);
        loadingTitle.size = new Dim2(0.6, 0, 1, 0);
        loadingTitle.text = "WINGSPAN";
        loadingTitle.setParent(transitionContainer);

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

        gameScreen = new UIFrame("GameScreen", this); // invisible frame for the game
        gameScreen.anchorPoint.center(); // centered anchor point
        gameScreen.position.center(); // center in middle
        gameScreen.size.full(); // entire screen
        gameScreen.backgroundTransparency = 0; // invisible
        
        UIFrame infoCorner = new UIFrame("GameInfoCorner", this);
        infoCorner.backgroundTransparency = 0f;
        infoCorner.size = new Dim2(0.15, 0, 0.29, 0).dilate(1.3);
        infoCorner.position = new Dim2(0.02, 0, 0.04, 0);
        infoCorner.keepAspectRatio = true;
        infoCorner.setZIndex(3);
        infoCorner.setParent(gameScreen);
        
        UIImage gameStatsFrame = new UIImage("GameStatsFrame", this);
        gameStatsFrame.setParent(infoCorner);
        gameStatsFrame.setImagePath("images/game_stats_background.png");
        gameStatsFrame.setImageFillType(UIImage.FIT_IMAGE);
        gameStatsFrame.size = new Dim2(0.46, 0, 1, 0);
        gameStatsFrame.backgroundTransparency = 0f;
        
        UIImage actionCubeIcon = new UIImage("ActionCubeIcon", this);
        actionCubeIcon.setParent(gameStatsFrame);
        actionCubeIcon.setImagePath("images/p1_action_cube.png");
        actionCubeIcon.setImageFillType(UIImage.FIT_IMAGE);
        actionCubeIcon.backgroundTransparency = 0f;
        actionCubeIcon.size = new Dim2(0.45, 0, 0.15, 0);
        actionCubeIcon.position = new Dim2(0.12, 0, 0.05, 0);
        
        UIText actionCubesStat = new UIText("ActionCubesStat", this);
        actionCubesStat.setParent(gameStatsFrame);
        actionCubesStat.textScaled = true;
        actionCubesStat.text = "2";
        actionCubesStat.textColor = Color.black;
        actionCubesStat.horizontalAlignment = UIText.LEFT;
        actionCubesStat.backgroundTransparency = 0f;
        actionCubesStat.backgroundColor = Color.black;
        actionCubesStat.position = new Dim2(0.625, 0, 0.055, 0);
        actionCubesStat.size = new Dim2(0.4, 0, 0.15, 0);

        UIFrame foodsStatFrame = new UIFrame("FoodsStatFrame", this);
        foodsStatFrame.setParent(gameStatsFrame);
        foodsStatFrame.position = new Dim2(0.045, 0, 0.313, 0);
        foodsStatFrame.size = new Dim2(0.92, 0, 0.65, 0);
        foodsStatFrame.backgroundColor = Color.black;
        foodsStatFrame.backgroundTransparency = 0f;
        foodsStatFrame.strokeTransparency = 0f;
        foodsStatFrame.strokeColor = Color.decode("#fff7f7");
        
        ListLayout foodsStatLayout = new ListLayout();
        foodsStatLayout.direction = ListLayout.VERTICAL;
        foodsStatLayout.verticalAlignment = ListLayout.CENTER;
        foodsStatLayout.horizontalAlignment = ListLayout.CENTER;
        foodsStatLayout.spacing = new Dim(-0.006, 0);
        foodsStatFrame.layout = foodsStatLayout;

        UIFrame birdFeederButtonContainer = new UIFrame("BirdFeederButtonContainer", this);
        birdFeederButtonContainer.backgroundTransparency = 0f;
        birdFeederButtonContainer.size = new Dim2(0.42, 0, 0.5, 0);
        birdFeederButtonContainer.position = new Dim2(0.5, 0, 0.035, 0);
        birdFeederButtonContainer.setParent(infoCorner);

        UIImage birdFeederButton = new UIImage("BirdFeederButton", this);
        birdFeederButton.setImagePath("images/bird_feeder_button.png");
        birdFeederButton.setImageFillType(UIImage.FIT_IMAGE);
        birdFeederButton.size = new Dim2(1, 0, 0.9, 0).dilate(0.85);
        birdFeederButton.anchorPoint.center();
        birdFeederButton.position.center();
        birdFeederButton.backgroundTransparency = 0f;
        birdFeederButton.setParent(birdFeederButtonContainer);
        birdFeederButton.addReleaseListener((e) -> {
            choosePlayingScreen(birdFeederScreen);
        });
        animOnHover(birdFeederButton, birdFeederButton);
        animOnPress(birdFeederButton, birdFeederButton);

        UIFrame birdFeederButtonOutline = new UIFrame("BirdFeederButtonOutline", this);
        birdFeederButtonOutline.size.full();
        birdFeederButtonOutline.anchorPoint.center();
        birdFeederButtonOutline.position.center();
        birdFeederButtonOutline.backgroundTransparency = 0f;
        birdFeederButtonOutline.setParent(birdFeederButton);
        birdFeederButtonOutline.borderRadius = new Dim(0.25, 0);
        birdFeederButtonOutline.strokeColor = Color.white;
        birdFeederButtonOutline.strokeThickness = new Dim(0.07, 0);
        birdFeederButtonOutline.strokeTransparency = 1f;
        birdFeederButtonOutline.ignore = true;

        UIFrame birdFeederButtonCover = new UIFrame("BirdFeederButtonCover", this);
        birdFeederButtonCover.size = new Dim2(1.06, 0, 1.04, 0).dilate(1.01);
        birdFeederButtonCover.anchorPoint.center();
        birdFeederButtonCover.position.center();
        birdFeederButtonCover.backgroundTransparency = 0f;
        birdFeederButtonCover.backgroundColor = Color.black;
        birdFeederButtonCover.setParent(birdFeederButton);
        birdFeederButtonCover.setZIndex(1);
        birdFeederButtonCover.borderRadius = new Dim(0.34, 0);
        birdFeederButtonCover.ignore = true;
        pressCover(birdFeederButton, birdFeederButtonCover);

        UIFrame deckButtonContainer = new UIFrame("DeckButtonContainer", this);
        deckButtonContainer.backgroundTransparency = 0f;
        deckButtonContainer.size = new Dim2(0.42, 0, 0.5, 0);
        deckButtonContainer.position = new Dim2(0.5, 0, 0.485, 0);
        deckButtonContainer.setParent(infoCorner);

        UIImage deckButton = new UIImage("DeckButton", this);
        deckButton.setImagePath("images/deck_button.png");
        deckButton.setImageFillType(UIImage.FIT_IMAGE);
        deckButton.size = new Dim2(1, 0, 0.9, 0).dilate(0.85);
        deckButton.anchorPoint.center();
        deckButton.position.center();
        deckButton.backgroundTransparency = 0f;
        deckButton.setParent(deckButtonContainer);
        deckButton.addReleaseListener((e) -> {
            choosePlayingScreen(deckScreen);
        });
        animOnHover(deckButton, deckButton);
        animOnPress(deckButton, deckButton);

        UIFrame deckButtonOutline = new UIFrame("DeckButtonOutline", this);
        deckButtonOutline.size.full();
        deckButtonOutline.anchorPoint.center();
        deckButtonOutline.position.center();
        deckButtonOutline.backgroundTransparency = 0f;
        deckButtonOutline.setParent(deckButton);
        deckButtonOutline.borderRadius = new Dim(0.25, 0);
        deckButtonOutline.strokeColor = Color.white;
        deckButtonOutline.strokeThickness = new Dim(0.07, 0);
        deckButtonOutline.strokeTransparency = 1f;
        deckButtonOutline.ignore = true;

        UIFrame deckButtonCover = new UIFrame("DeckButtonCover", this);
        deckButtonCover.size = new Dim2(1.06, 0, 1.04, 0).dilate(1.01);
        deckButtonCover.anchorPoint.center();
        deckButtonCover.position.center();
        deckButtonCover.backgroundTransparency = 0f;
        deckButtonCover.backgroundColor = Color.black;
        deckButtonCover.setParent(deckButton);
        deckButtonCover.setZIndex(1);
        deckButtonCover.borderRadius = new Dim(0.34, 0);
        deckButtonCover.ignore = true;
        pressCover(deckButton, deckButtonCover);

        UIFrame handButtonContainer = new UIFrame("HandButtonContainer", this);
        handButtonContainer.backgroundTransparency = 0f;
        handButtonContainer.size = new Dim2(0.155, 0, 0.35, 0).dilate(1.1);
        handButtonContainer.keepAspectRatio = true;
        handButtonContainer.position = new Dim2(0.0225, 0, 0.95, 0);
        handButtonContainer.anchorPoint = new Vector2(0, 1);
        handButtonContainer.setParent(gameScreen);

        UIFrame handButton = new UIFrame("HandButton", this);
        handButton.backgroundTransparency = 0f;
        handButton.size.full().dilate(0.9);
        handButton.position.center();
        handButton.anchorPoint.center();
        handButton.addReleaseListener(e -> {
            choosePlayingScreen(handScreen);
        });
        handButton.setParent(handButtonContainer);

        UIImage handButtonBird = new UIImage("HandButtonBird", this);
        handButtonBird.setImagePath("birds/back_of_bird.png");
        handButtonBird.setImageFillType(UIImage.FIT_IMAGE);
        handButtonBird.setZIndex(1);
        handButtonBird.size = new Dim2(0.885, 0, 0.935, 0);
        handButtonBird.anchorPoint = new Vector2(0, 1);
        handButtonBird.position = new Dim2(0, 0, 1, 0);
        handButtonBird.backgroundTransparency = 0f;
        handButtonBird.ignore = true;
        handButtonBird.setParent(handButton);

        UIFrame handButtonBirdOutline = new UIFrame("HandButtonBirdOutline", this);
        handButtonBirdOutline.size = handButtonBird.size;
        handButtonBirdOutline.backgroundTransparency = 0f;
        handButtonBirdOutline.position = handButtonBird.position;
        handButtonBirdOutline.anchorPoint = handButtonBird.anchorPoint;
        handButtonBirdOutline.setZIndex(-1);
        handButtonBirdOutline.setParent(handButton);
        handButtonBirdOutline.borderRadius = new Dim(0.08, 0);
        handButtonBirdOutline.strokeColor = Color.white;
        handButtonBirdOutline.strokeThickness = new Dim(0.1, 0);
        handButtonBirdOutline.strokeTransparency = 1f;
        handButtonBirdOutline.ignore = true;

        UIImage handButtonBonus = new UIImage("HandButtonBonus", this);
        handButtonBonus.setImagePath("bonus/back_of_bonus.png");
        handButtonBonus.setImageFillType(UIImage.FIT_IMAGE);
        handButtonBonus.size = new Dim2(0.885, 0, 0.935, 0);
        handButtonBonus.anchorPoint = new Vector2(1, 0);
        handButtonBonus.position = new Dim2(1, 0, 0, 0);
        handButtonBonus.backgroundTransparency = 0f;
        handButtonBonus.setBrightness(0.8f);
        handButtonBonus.ignore = true;
        handButtonBonus.setParent(handButton);

        UIFrame handButtonBonusOutline = new UIFrame("HandButtonBonusOutline", this);
        handButtonBonusOutline.size = handButtonBonus.size;
        handButtonBonusOutline.backgroundTransparency = 0f;
        handButtonBonusOutline.position = handButtonBonus.position;
        handButtonBonusOutline.anchorPoint = handButtonBonus.anchorPoint;
        handButtonBonusOutline.setZIndex(-1);
        handButtonBonusOutline.setParent(handButton);
        handButtonBonusOutline.borderRadius = new Dim(0.08, 0);
        handButtonBonusOutline.strokeColor = Color.white;
        handButtonBonusOutline.strokeThickness = new Dim(0.1, 0);
        handButtonBonusOutline.strokeTransparency = 1f;
        handButtonBonusOutline.ignore = true;
        animOnHover(handButton, handButton);
        animOnPress(handButton, handButton);

        boardScreen = new UIFrame("BoardScreen", this);
        boardScreen.anchorPoint = new Vector2(1, 0.5);
        boardScreen.position = new Dim2(0.925, 0, 0.5, 0);
        boardScreen.size = new Dim2(0.68, 0, 0.8, 0);
        boardScreen.backgroundTransparency = 0f;
        boardScreen.visible = false;
        boardScreen.setParent(gameScreen);

        UIText boardTitle = new UIText("BoardTitle", this);
        boardTitle.backgroundTransparency = 0f;
        boardTitle.textColor = Color.white;
        boardTitle.textScaled = true;
        boardTitle.position = new Dim2(0.5, 0, 0.0075, 0);
        boardTitle.size = new Dim2(0.9, 0, 0.1, 0);
        boardTitle.textStrokeColor = Color.black;
        boardTitle.textStrokeTransparency = 1f;
        boardTitle.textStrokeThickness = new Dim(0.009, 0);
        boardTitle.horizontalAlignment = UIText.CENTER;
        boardTitle.anchorPoint.center();
        boardTitle.text = "Player Board";
        boardTitle.setParent(boardScreen);

        UIFrame boards = new UIFrame("Boards", this);
        boards.backgroundTransparency = 0f;
        boards.keepAspectRatio = true;
        boards.size.full();
        boards.position.center();
        boards.anchorPoint.center();
        boards.setParent(boardScreen);
        boards.setAttribute("Index", 1);
        boards.setAttribute("ViewBoard", (Runnable)() -> {
            int index = (int)boards.getAttribute("Index");
            if (index == currentGame.getPlayerTurn()) {
                boardTitle.text = "Player " + index + "'s Turn";
            } else {
                boardTitle.text = "Viewing Player " + index + "'s Board";
            }
            UIImage prevBoard = (UIImage)boards.getAttribute("Current");
            if (prevBoard != null) {
                UIFrame.getByName("Player" + prevBoard.getName().split("PlayerBoard")[1] + "Button").strokeColor = Color.white;
                prevBoard.visible = false;
            }
            UIImage newBoard = UIImage.getByName("PlayerBoard" + index);
            UIFrame.getByName("Player" + index + "Button").strokeColor = Color.decode("#0fb800");
            newBoard.visible = true;
            boards.setAttribute("Current", newBoard);
            choosePlayingScreen(boardScreen);
        });

        for (int i = 0; i < 5; i++) {
            int p = i + 1;
            UIImage playerBoard = new UIImage("PlayerBoard" + p, this);
            playerBoard.size = new Dim2(1, 0, 0.94, 0).dilate(0.85);
            playerBoard.position = new Dim2(0.5, 0, 0.475, 0);
            playerBoard.anchorPoint.center();
            playerBoard.backgroundTransparency = 0f;
            playerBoard.setImageFillType(UIImage.FIT_IMAGE);
            playerBoard.setImagePath("images/board.jpg");
            playerBoard.setParent(boards);
            playerBoard.visible = false;

            UIFrame playBirdButton = new UIFrame("PlayBirdButton" + p, this);
            playBirdButton.size = new Dim2(1, 0, 0.045, 0);
            playBirdButton.position = new Dim2(0.5, 0, 0, 0);
            playBirdButton.anchorPoint = new Vector2(0.5, 0);
            playBirdButton.backgroundTransparency = 0f;
            playBirdButton.setParent(playerBoard);
            playBirdButton.addClickListener((e) -> {
                if (currentGame.getPlayerTurn() != p) return;
                gameScreen.setAttribute("Action", "playBird");
               ((Runnable)gameScreen.getAttribute("PickAction")).run();
            });

            UIFrame getFoodButton = new UIFrame("GetFoodButton" + p, this);
            getFoodButton.size = new Dim2(1, 0, 0.315, 0);
            getFoodButton.position = new Dim2(0.5, 0, 0.045, 0);
            getFoodButton.anchorPoint = new Vector2(0.5, 0);
            getFoodButton.backgroundTransparency = 0f;
            getFoodButton.setParent(playerBoard);
            getFoodButton.addClickListener((e) -> {
                if (currentGame.getPlayerTurn() != p) return;
                gameScreen.setAttribute("Action", "getFood");
               ((Runnable)gameScreen.getAttribute("PickAction")).run();
            });
 
            UIFrame layEggsButton = new UIFrame("LayEggsButton" + p, this);
            layEggsButton.size = new Dim2(1, 0, 0.315, 0);
            layEggsButton.position = new Dim2(0.5, 0, 0.36, 0);
            layEggsButton.anchorPoint = new Vector2(0.5, 0);
            layEggsButton.backgroundTransparency = 0f;
            layEggsButton.setParent(playerBoard);
            layEggsButton.addClickListener((e) -> {
                if (currentGame.getPlayerTurn() != p) return;
                gameScreen.setAttribute("Action", "layEggs");
               ((Runnable)gameScreen.getAttribute("PickAction")).run();
            });

            UIFrame drawBirdsButton = new UIFrame("DrawBirdsButton" + p, this);
            drawBirdsButton.size = new Dim2(1, 0, 0.32, 0);
            drawBirdsButton.position = new Dim2(0.5, 0, 0.675, 0);
            drawBirdsButton.anchorPoint = new Vector2(0.5, 0);
            drawBirdsButton.backgroundTransparency = 0f;
            drawBirdsButton.setParent(playerBoard);
            drawBirdsButton.addClickListener((e) -> {
                if (currentGame.getPlayerTurn() != p) return;
                gameScreen.setAttribute("Action", "drawBirds");
               ((Runnable)gameScreen.getAttribute("PickAction")).run();
            });
        }

        gameScreen.setAttribute("ResetSelected", (Runnable)() -> {
            for(UIElement tagged : UIElement.getAllTagged("Check")) tagged.visible = false;
            UIElement.removeAllTagged("Selected");
            gameScreen.setAttribute("TradingEgg", false);
            selected.clear();
            UIFrame.getByName("ConfirmDrawFrame").setAttribute("Clickable", false);
            ((Runnable)UIFrame.getByName("ConfirmDrawFrame").getAttribute("Update")).run();
        });

        gameScreen.setAttribute("PickAction", (Runnable)() -> {
            ((Runnable)gameScreen.getAttribute("ResetSelected")).run();
            String action = (String)gameScreen.getAttributeOrDefault("Action", "");
            this.selected.clear();
            if (action.equals("playBird")) {
                choosePlayingScreen(handScreen);
            } else if (action.equals("getFood")) {
                choosePlayingScreen(birdFeederScreen);
            } else if (action.equals("layEggs")) {
                
            } else if (action.equals("drawBirds")) {
                choosePlayingScreen(deckScreen);
                Player p = currentGame.getPlayers().get(currentGame.getPlayerTurn() - 1);
                int birdGet = 0;
                int birdAmount = p.getBoard().get("wetland").size();
                if(birdAmount < 2) birdGet = 1;
                else if (birdAmount < 5) birdGet = 2;
                else birdGet = 3;
                gameScreen.setAttribute("CanSelectAmount", birdGet);
                //System.out.println(p.hasEnoughEggs(1)); 
                promptPlayer("Would you like to spend an egg to be able to draw one extra card?", "Yes", "No", (b) -> {
                    gameScreen.setAttribute("TradingEgg", b);
                });
                ((Runnable)(UIElement.getByName("ConfirmDrawFrame").getAttribute("Update"))).run();
            } else choosePlayingScreen(boardScreen);
            this.repaint();
        });

        /* UIFrame selectedInfoFrame = new UIFrame("SelectedInfoFrame", this);
        selectedInfoFrame.size = new Dim2(0.3, 0, 0.08, 0);
        selectedInfoFrame.keepAspectRatio = true;
        selectedInfoFrame.position = new Dim2(0.975, 0, 0.05, 0);
        selectedInfoFrame.anchorPoint = new Vector2(1, 0);
        selectedInfoFrame.backgroundTransparency = 1f;
        selectedInfoFrame.backgroundColor = Color.decode("#ffffff");
        selectedInfoFrame.strokeColor = Color.decode("#2c2c2c");
        selectedInfoFrame.strokeThickness = new Dim(0.05, 0);
        selectedInfoFrame.strokeTransparency = 1f;
        selectedInfoFrame.borderRadius = new Dim(0.15, 0);
        selectedInfoFrame.setZIndex(80);
        selectedInfoFrame.setParent(gameScreen);

        UIText selectedAmountStat = new UIText("SelectedAmountStat", this);
        selectedAmountStat.backgroundTransparency = 0f;
        selectedAmountStat.text = "You selected 0/1 cards"; */

        /* UIFrame bfbInfoContainer = new UIFrame("BFBInfoContainer", this);
        bfbInfoContainer.keepAspectRatio = true;
        bfbInfoContainer.size = new Dim2(1.5, 0, 0.85, 0).dilate(1.05);
        bfbInfoContainer.anchorPoint = new Vector2(0, .5);
        bfbInfoContainer.position = new Dim2(1.01, 0, 0.65, 0);
        bfbInfoContainer.backgroundTransparency = 0f;
        bfbInfoContainer.ignore = true;
        bfbInfoContainer.setParent(birdFeederButtonContainer);

        UIImage bfbCurvedArrow = new UIImage("BFBCurvedArrow", this);
        bfbCurvedArrow.size = new Dim2(0.7, 0, 0.8, 0).dilate(0.7);
        bfbCurvedArrow.setImageFillType(UIImage.FIT_IMAGE);
        bfbCurvedArrow.setImagePath("images/curved_arrow.png");
        bfbCurvedArrow.backgroundTransparency = 0f;
        bfbCurvedArrow.setParent(bfbInfoContainer);

        UIText bfbInfoText = new UIText("BFBInfoText", this);
        bfbInfoText.size = new Dim2(0.9, 0, 0.5, 0);
        bfbInfoText.position = new Dim2(0.45, 0, 0.7, 0);
        bfbInfoText.anchorPoint.center();
        bfbInfoText.textScaled = true;
        bfbInfoText.textColor = Color.white;
        bfbInfoText.backgroundTransparency = 0f;
        bfbInfoText.horizontalAlignment = UIText.CENTER;
        bfbInfoText.text = "Pick food tokens here!";
        bfbInfoText.setParent(bfbInfoContainer); */

        deckScreen = (UIFrame)boardScreen.clone("DeckScreen");
        deckScreen.setAttribute("Button", deckButton);

        UIFrame deckScreenContent = new UIFrame("DeckScreenContent", this);
        deckScreenContent.backgroundTransparency = 0f;
        deckScreenContent.size.full();
        deckScreenContent.position.center();
        deckScreenContent.anchorPoint.center();
        deckScreenContent.setParent(deckScreen);
        deckScreenContent.keepAspectRatio = true;

        UIFrame deckTopBar = new UIFrame("DeckTopBar", this);
        deckTopBar.backgroundTransparency = 0f;
        deckTopBar.position = new Dim2(0.5, 0, 0, 0);
        deckTopBar.size = new Dim2(0.9, 0, 0.15, 0);
        deckTopBar.anchorPoint.center();
        deckTopBar.setParent(deckScreenContent);

        ListLayout deckTopBarLayout = new ListLayout();
        deckTopBarLayout.direction = ListLayout.HORIZONTAL;
        deckTopBarLayout.verticalAlignment = ListLayout.CENTER;
        deckTopBarLayout.horizontalAlignment = ListLayout.MIDDLE;
        deckTopBarLayout.spacing = new Dim(0.02, 0);
        deckTopBar.layout = deckTopBarLayout;

        UIText deckTitle = new UIText("DeckTitle", this);
        deckTitle.backgroundTransparency = 0f;
        deckTitle.textColor = Color.white;
        deckTitle.textScaled = true;
        deckTitle.size = new Dim2(0.2, 0, 1, 0);
        deckTitle.textStrokeColor = Color.black;
        deckTitle.textStrokeTransparency = 1f;
        deckTitle.textStrokeThickness = new Dim(0.05, 0);
        deckTitle.horizontalAlignment = UIText.CENTER;
        deckTitle.text = "Deck";
        deckTitle.setParent(deckTopBar);

        UIFrame confirmDrawContainer = new UIFrame("ConfirmDrawContainer", this);
        confirmDrawContainer.size = new Dim2(0.2, 0, 1, 0);
        confirmDrawContainer.backgroundTransparency = 0f;
        confirmDrawContainer.setParent(deckTopBar);

        UIFrame confirmDrawFrame = new UIFrame("ConfirmDrawFrame", this);
        confirmDrawFrame.size = new Dim2(1, 0, 0.65, 0);
        confirmDrawFrame.position.center();
        confirmDrawFrame.anchorPoint.center();
        confirmDrawFrame.backgroundColor = Color.decode("#4ccc47");
        confirmDrawFrame.borderRadius = new Dim(0.3, 0);
        confirmDrawFrame.setParent(confirmDrawContainer);

        UIText confirmDraw = new UIText("ConfirmDraw", this);
        confirmDraw.size.full().dilate(0.7);
        confirmDraw.position.center();
        confirmDraw.anchorPoint.center();
        confirmDraw.textScaled = true;
        confirmDraw.ignore = true;
        confirmDraw.backgroundTransparency = 0f;
        confirmDraw.text = "Confirm";
        confirmDraw.setParent(confirmDrawFrame);
        animOnHover(confirmDrawFrame, confirmDrawFrame);
        animOnPress(confirmDrawFrame, confirmDrawFrame);
        confirmDrawFrame.setAttribute("Update", (Runnable)() -> {
            confirmDrawContainer.visible = gameScreen.getAttributeOrDefault("Action", "").equals("drawBirds");
            boolean clickable = confirmDrawFrame.getAttributeOrDefault("Clickable", false);
            confirmDrawFrame.backgroundColor = clickable ? Color.decode("#4ccc47") : Color.lightGray;
        });
        confirmDrawFrame.addReleaseListener((e) -> {
            boolean clickable = confirmDrawFrame.getAttributeOrDefault("Clickable", false);
            if (clickable) {
                currentGame.playActions("drawBirds");
                gameScreen.setAttribute("Action", "");
                ((Runnable)gameScreen.getAttribute("PickAction")).run();
            }
        });
        ((Runnable)(confirmDrawFrame.getAttribute("Update"))).run();

        UIFrame deckCardsContainer = new UIFrame("DeckCardsContainer", this);
        deckCardsContainer.backgroundTransparency = 0f;
        deckCardsContainer.size = new Dim2(1, 0, 0.6, 0).dilate(0.75);
        deckCardsContainer.position = new Dim2(0.5, 0, 0.35, 0);
        deckCardsContainer.anchorPoint.center();
        deckCardsContainer.setParent(deckScreenContent);

        UIFrame deckCard1Frame = new UIFrame("DeckCard1Frame", this);
        deckCard1Frame.size = new Dim2(0.15, 0, 0.5, 0).dilate(1.6);
        deckCard1Frame.position = new Dim2(0.5, 0, 0.4, 0);
        deckCard1Frame.anchorPoint.center();
        deckCard1Frame.backgroundTransparency = 0f;
        deckCard1Frame.setParent(deckCardsContainer);

        UIImage deckCard1 = new UIImage("DeckCard1", this);
        deckCard1.size.full();
        deckCard1.position.center();
        deckCard1.anchorPoint.center();
        deckCard1.backgroundTransparency = 0f;
        deckCard1.setAttribute("ChoiceIndex", 0);
        deckCard1.setImageFillType(UIImage.FIT_IMAGE);
        deckCard1.setParent(deckCard1Frame);
        animOnHover(deckCard1, deckCard1);
        animOnPress(deckCard1, deckCard1);
        deckCard1.addClickListener(e -> {
            cyclingView.setAttribute("Items", currentGame.getFaceUpTray());
            cyclingView.setAttribute("Index", currentGame.getFaceUpTray().indexOf(deckCard1.getAttribute("Card")));
            ((Runnable)cyclingView.getAttribute("Run")).run();
        });

        UIImage deckCard1Check = new UIImage("DeckCard1Check", this);
        deckCard1Check.size = new Dim2(0.2, 0, 0.1, 0);
        deckCard1Check.position = new Dim2(0.99, 0, 0.01, 0);
        deckCard1Check.anchorPoint.center();
        deckCard1Check.backgroundTransparency = 0f;
        deckCard1Check.addTag("Check");
        deckCard1Check.setImageFillType(UIImage.FIT_IMAGE);
        deckCard1Check.setImagePath("images/check_mark.png");
        deckCard1Check.setParent(deckCard1);
        deckCard1Check.ignore = true;
        deckCard1Check.visible = false;

        UIFrame deckCard2Frame = deckCard1Frame.clone("DeckCard2Frame");
        deckCard2Frame.position = new Dim2(0.825, 0, 0.53, 0);
        deckCard2Frame.rotation = 20;
        deckCard2Frame.setParent(deckCardsContainer);

        UIImage deckCard2 = deckCard1.clone("DeckCard2");
        deckCard2.setAttribute("ChoiceIndex", 1);
        deckCard2.setParent(deckCard2Frame);
        animOnHover(deckCard2, deckCard2);
        animOnPress(deckCard2, deckCard2);
        deckCard2.addClickListener(e -> {
            cyclingView.setAttribute("Items", currentGame.getFaceUpTray());
            cyclingView.setAttribute("Index", currentGame.getFaceUpTray().indexOf(deckCard2.getAttribute("Card")));
            ((Runnable)cyclingView.getAttribute("Run")).run();
        });

        UIImage deckCard2Check = deckCard1Check.clone("DeckCard2Check");
        deckCard2Check.setParent(deckCard2);

        UIFrame deckCard3Frame = deckCard1Frame.clone("DeckCard3Frame");
        deckCard3Frame.position = new Dim2(0.175, 0, 0.53, 0);
        deckCard3Frame.rotation = -20;
        deckCard3Frame.setParent(deckCardsContainer);

        UIImage deckCard3 = deckCard1.clone("DeckCard3");
        deckCard3.setAttribute("ChoiceIndex", 2);
        deckCard3.setParent(deckCard3Frame);
        animOnHover(deckCard3, deckCard3);
        animOnPress(deckCard3, deckCard3);
        deckCard3.addClickListener(e -> {
            cyclingView.setAttribute("Items", currentGame.getFaceUpTray());
            cyclingView.setAttribute("Index", currentGame.getFaceUpTray().indexOf(deckCard3.getAttribute("Card")));
            ((Runnable)cyclingView.getAttribute("Run")).run();
        });

        UIImage deckCard3Check = deckCard1Check.clone("DeckCard3Check");
        deckCard3Check.setParent(deckCard3);

        UIImage featherThingLeft = new UIImage("FeatherThingLeft", this);
        featherThingLeft.size = new Dim2(0.15, 0, 0.15, 0).dilate(1.2);
        featherThingLeft.position = new Dim2(0.15, 0, 0.7, 0);
        featherThingLeft.anchorPoint = new Vector2(0, 0.5);
        featherThingLeft.backgroundTransparency = 0f;
        featherThingLeft.setImagePath("images/feather_thing_left.png");
        featherThingLeft.setImageFillType(UIImage.FIT_IMAGE);
        featherThingLeft.setParent(deckScreenContent);

        UIImage featherThingRight = featherThingLeft.clone("FeatherThingRight");
        featherThingRight.position = new Dim2(0.85, 0, 0.7, 0);
        featherThingRight.anchorPoint = new Vector2(1, 0.5);
        featherThingRight.setImagePath("images/feather_thing_right.png");
        featherThingRight.setParent(deckScreenContent);

        UIFrame faceDownCardFrame = new UIFrame("FaceDownCardFrame", this);
        faceDownCardFrame.size = new Dim2(0.15, 0, 0.3, 0).dilate(1.1);
        faceDownCardFrame.position = new Dim2(0.5, 0, 0.7, 0);
        faceDownCardFrame.anchorPoint.center();
        faceDownCardFrame.rotation = 90;
        faceDownCardFrame.backgroundTransparency = 0f;
        faceDownCardFrame.setParent(deckScreenContent);

        UIImage faceDownCard = new UIImage("FaceDownCard", this);
        faceDownCard.size.full();
        faceDownCard.position.center();
        faceDownCard.anchorPoint.center();
        faceDownCard.backgroundTransparency = 0f;
        // faceDownCard.setBrightness(0.6f);
        faceDownCard.setImagePath("birds/back_of_bird.png");
        faceDownCard.setImageFillType(UIImage.FIT_IMAGE);
        faceDownCard.setParent(faceDownCardFrame);
        faceDownCard.addTag("FaceDown");
        faceDownCard.setAttribute("ChoiceIndex", 3);
        animOnHover(faceDownCard, faceDownCard);
        animOnPress(faceDownCard, faceDownCard);
        faceDownCard.addReleaseListener((e) -> {
            if (gameScreen.getAttributeOrDefault("Action", "").equals("drawBirds")) {
                Selectable selected = faceDownCard.getAttributeOrDefault("Selected", null);
                if (selected != null) {
                    this.selected.remove(selected);
                    UIImage.getByName("FaceDownCardCheck").visible = false;
                    faceDownCard.removeTag("Selected");
                    faceDownCard.setAttribute("Selected", null);
                } else {
                    Selectable a = new Selectable(null, faceDownCard);
                    faceDownCard.addTag("Selected");
                    this.selected.add(a);
                    UIImage.getByName("FaceDownCardCheck").visible = true;
                    faceDownCard.setAttribute("Selected", a);
                    if (this.selected.size() > gameScreen.getAttributeOrDefault("CanSelectAmount", 0)) {
                        Selectable old = this.selected.getFirst();
                        ArrayList<Card> items = (ArrayList<Card>)cyclingView.getAttribute("Items");
                        if (old.getElement() != null) old.getElement().removeTag("Selected");
                        if (items != null) {
                            Card c = (Card)old.getValue();
                            UIImage oldItem = null;
                            UIImage item1 = UIImage.getByName("Item1");
                            UIImage item2 = UIImage.getByName("Item2");
                            UIImage item3 = UIImage.getByName("Item3");
                            Card i1c = item1.getAttributeOrDefault("Card", null);
                            Card i2c = item2.getAttributeOrDefault("Card", null);
                            Card i3c = item3.getAttributeOrDefault("Card", null);
                            if (i1c != null && i1c.equals(c)) oldItem = item1; else if (i2c != null && i2c.equals(c)) oldItem = item2; else if (i3c != null && i3c.equals(c)) oldItem = item3;
                            if (oldItem != null) {
                                UIImage.getByName(oldItem.getName() + "Check").visible = false;
                            }
                            int in = items.indexOf(c);
                            if (in > -1) UIImage.getByName("DeckCard" + (in + 1) + "Check").visible = false;
                            this.selected.remove(old);
                        }
                    }
                }
                confirmDrawFrame.setAttribute("Clickable", this.selected.size() >= gameScreen.getAttributeOrDefault("CanSelectAmount", 0));
                ((Runnable)confirmDrawFrame.getAttribute("Update")).run();
                System.out.println(this.selected);
            }
        });

        UIImage faceDownCardCheck = deckCard1Check.clone("FaceDownCardCheck");
        faceDownCardCheck.setParent(faceDownCard);
        faceDownCardCheck.rotation = -90;
        faceDownCardCheck.position = new Dim2(0.01, 0, 0.01, 0);

        birdFeederScreen = boardScreen.clone("BirdFeederScreen");
        birdFeederScreen.setAttribute("Button", birdFeederButton);

        UIFrame birdFeederScreenContent = new UIFrame("BirdFeederScreenContent", this);
        birdFeederScreenContent.backgroundTransparency = 0f;
        birdFeederScreenContent.size.full();
        birdFeederScreenContent.position.center();
        birdFeederScreenContent.anchorPoint.center();
        birdFeederScreenContent.setParent(birdFeederScreen);
        birdFeederScreenContent.keepAspectRatio = true;
        
        UIText birdFeederTitle = new UIText("BirdFeederTitle", this);
        birdFeederTitle.backgroundTransparency = 0f;
        birdFeederTitle.textColor = Color.white;
        birdFeederTitle.textScaled = true;
        birdFeederTitle.position = new Dim2(0.5, 0, 0, 0);
        birdFeederTitle.size = new Dim2(0.9, 0, 0.15, 0);
        birdFeederTitle.textStrokeColor = Color.black;
        birdFeederTitle.textStrokeTransparency = 1f;
        birdFeederTitle.textStrokeThickness = new Dim(0.01, 0);
        birdFeederTitle.horizontalAlignment = UIText.CENTER;
        birdFeederTitle.anchorPoint .center();
        birdFeederTitle.text = "BIRD FEEDER";
        birdFeederTitle.setParent(birdFeederScreenContent);

        UIFrame birdFeederChoicesContainer = new UIFrame("BirdFeederChoicesContainer", this);
        birdFeederChoicesContainer.backgroundTransparency = 0f;
        birdFeederChoicesContainer.size = new Dim2(0.9, 0, 0.15, 0);
        birdFeederChoicesContainer.position = new Dim2(0.5, 0, 0.35, 0);
        birdFeederChoicesContainer.anchorPoint.center();
        birdFeederChoicesContainer.setParent(birdFeederScreenContent);

        ListLayout birdFeederChoicesLayout = new ListLayout();
        birdFeederChoicesLayout.direction = ListLayout.HORIZONTAL;
        birdFeederChoicesLayout.horizontalAlignment = ListLayout.CENTER;
        birdFeederChoicesLayout.verticalAlignment = ListLayout.CENTER;
        birdFeederChoicesLayout.spacing = new Dim(0.05, 0);
        birdFeederChoicesContainer.layout = birdFeederChoicesLayout;

        UIFrame rerollBirdFeederButtonContainer = new UIFrame("RerollBirdFeederButtonContainer", this);
        rerollBirdFeederButtonContainer.backgroundTransparency = 0f;
        rerollBirdFeederButtonContainer.size = new Dim2(0.25, 0, 0.1, 0).dilate(1.1);
        rerollBirdFeederButtonContainer.anchorPoint = new Vector2(1, 0.5);
        rerollBirdFeederButtonContainer.position = new Dim2(0.95, 0, 0.55, 0);
        rerollBirdFeederButtonContainer.setParent(birdFeederScreenContent);

        UIFrame rerollBirdFeederButton = new UIFrame("RerollBirdFeederButton", this);
        rerollBirdFeederButton.size.full().dilate(0.9);
        rerollBirdFeederButton.backgroundColor = Color.gray;
        rerollBirdFeederButton.strokeColor = Color.black;
        rerollBirdFeederButton.borderRadius = new Dim(0.3, 0);
        rerollBirdFeederButton.strokeThickness = new Dim(0.05, 0);
        rerollBirdFeederButton.strokeTransparency = 1f;
        rerollBirdFeederButton.position.center();
        rerollBirdFeederButton.anchorPoint.center();
        rerollBirdFeederButton.setParent(rerollBirdFeederButtonContainer);

        UIText rerollBirdFeederButtonText = new UIText("RerollBirdFeederButtonText", this);
        rerollBirdFeederButtonText.ignore = true;
        rerollBirdFeederButtonText.textScaled = true;
        rerollBirdFeederButtonText.text = "RE-ROLL";
        rerollBirdFeederButtonText.textColor = Color.black;
        rerollBirdFeederButtonText.size.full().dilate(0.75);
        rerollBirdFeederButtonText.position.center();
        rerollBirdFeederButtonText.anchorPoint.center();
        rerollBirdFeederButtonText.backgroundTransparency = 0f;
        rerollBirdFeederButtonText.setParent(rerollBirdFeederButton);

        UIFrame rerollBirdFeederCover = new UIFrame("RerollBirdFeederCover", this);
        rerollBirdFeederCover.size.full();
        rerollBirdFeederCover.position.center();
        rerollBirdFeederCover.anchorPoint.center();
        rerollBirdFeederCover.backgroundTransparency = 0f;
        rerollBirdFeederCover.borderRadius = rerollBirdFeederButton.borderRadius;
        rerollBirdFeederCover.backgroundColor = Color.black;
        rerollBirdFeederCover.ignore = true;
        rerollBirdFeederCover.setParent(rerollBirdFeederButton);
        animOnHover(rerollBirdFeederButton, rerollBirdFeederButton);
        animOnPress(rerollBirdFeederButton, rerollBirdFeederButton);
        pressCover(rerollBirdFeederButton, rerollBirdFeederCover);

        handScreen = (UIFrame)boardScreen.clone("HandScreen");
        handScreen.setAttribute("Button", handButton);

        UIFrame handScreenContent = new UIFrame("HandScreenContent", this);
        handScreenContent.backgroundTransparency = 0f;
        handScreenContent.size.full();
        handScreenContent.position.center();
        handScreenContent.anchorPoint.center();
        handScreenContent.setParent(handScreen);
        handScreenContent.keepAspectRatio = true;

        UIText handTitle = new UIText("HandTitle", this);
        handTitle.backgroundTransparency = 0f;
        handTitle.textColor = Color.white;
        handTitle.textScaled = true;
        handTitle.position = new Dim2(0.5, 0, 0, 0);
        handTitle.size = new Dim2(0.9, 0, 0.15, 0);
        handTitle.textStrokeColor = Color.black;
        handTitle.textStrokeTransparency = 1f;
        handTitle.textStrokeThickness = new Dim(0.01, 0);
        handTitle.horizontalAlignment = UIText.CENTER;
        handTitle.anchorPoint .center();
        handTitle.text = "Hand";
        handTitle.setParent(handScreenContent);

        UIFrame handCardsContainer = new UIFrame("HandCardsContainer", this);
        handCardsContainer.backgroundTransparency = 0.4f;
        handCardsContainer.backgroundColor = Color.black;
        handCardsContainer.borderRadius = new Dim(0.05, 0);
        handCardsContainer.size = new Dim2(0.8, 0, 0.6, 0);
        handCardsContainer.position = new Dim2(0.5, 0, 0.475, 0);
        handCardsContainer.anchorPoint.center();
        handCardsContainer.setParent(handScreenContent);

        choosePlayingScreen(boardScreen);

        cyclingView = new UIFrame("CyclingView", this);
        cyclingView.size.full();
        cyclingView.position.center();
        cyclingView.anchorPoint.center();
        cyclingView.backgroundTransparency = 0f;
        cyclingView.keepAspectRatio = true;
        cyclingView.visible = false;
        cyclingView.setParent(gameScreen);
        cyclingView.setZIndex(98);

        UIFrame cyclingViewContent = new UIFrame("CyclingViewContent", this);
        cyclingViewContent.size = new Dim2(0.53, 0, 0.67, 0);
        cyclingViewContent.position = new Dim2(0.5, 0, 0.485, 0);
        cyclingViewContent.anchorPoint.center();
        cyclingViewContent.backgroundTransparency = 0f;
        cyclingViewContent.setParent(cyclingView);

        UIFrame selectButtonFrame = new UIFrame("SelectButtonFrame", this);
        selectButtonFrame.size = new Dim2(0.2, 0, 0.1, 0).dilate(1.2);
        selectButtonFrame.position = new Dim2(0.5, 0, 1, 0);
        selectButtonFrame.anchorPoint.center();
        selectButtonFrame.backgroundTransparency = 1f;
        selectButtonFrame.borderRadius = new Dim(0.5, 0);
        selectButtonFrame.backgroundColor = Color.decode("#faf4f4");
        selectButtonFrame.strokeColor = Color.decode("#242424");
        selectButtonFrame.strokeTransparency = 1f;
        selectButtonFrame.setParent(cyclingViewContent);

        UIText selectButton = new UIText("SelectButton", this);
        selectButton.size.full().dilate(0.8);
        selectButton.position.center();
        selectButton.anchorPoint.center();
        selectButton.ignore = true;
        selectButton.text = "Select";
        selectButton.textColor = Color.decode("#242424");
        selectButton.textScaled = true;
        selectButton.backgroundTransparency = 0f;
        selectButton.setParent(selectButtonFrame);
        animOnHover(selectButtonFrame, selectButtonFrame);
        animOnPress(selectButtonFrame, selectButtonFrame);

        UIFrame cyclingViewBackground = new UIFrame("CyclingViewBackground", this);
        cyclingViewBackground.size.full().dilate(150);
        cyclingViewBackground.position.center();
        cyclingViewBackground.anchorPoint.center();
        cyclingViewBackground.backgroundColor = Color.decode("#422508");
        cyclingViewBackground.backgroundTransparency = 0.5f;
        cyclingViewBackground.setZIndex(-1);
        cyclingViewBackground.setParent(cyclingView);
        cyclingViewBackground.addReleaseListener((e) -> {
            ((Runnable)cyclingView.getAttribute("Stop")).run();
        });

        UIImage item1 = new UIImage("Item1", this);
        item1.size = new Dim2(0.15, 0, 0.3, 0).dilate(2);
        item1.position = new Dim2(0.5, 0, 0.45, 0);
        item1.anchorPoint.center();
        item1.backgroundTransparency = 0f;
        item1.setImageFillType(UIImage.FIT_IMAGE);
        item1.setImagePath("birds/back_of_bird.png");
        item1.setParent(cyclingView);

        UIImage item1Check = deckCard1Check.clone("Item1Check");
        item1Check.setParent(item1);
        item1Check.position = new Dim2(0.925, 0, 0, 0);

        UIImage item2 = (UIImage)item1.clone("Item2");
        item2.size.dilate(0.9);
        item2.position = new Dim2(0.35, 0, 0.55, 0);
        item2.setZIndex(-2);
        item2.setParent(cyclingView);

        UIImage item2Check = item1Check.clone("Item2Check");
        item2Check.setParent(item2);

        UIImage item3 = (UIImage)item2.clone("Item3");
        item3.position = new Dim2(0.65, 0, 0.55, 0);
        item3.setParent(cyclingView);

        UIImage item3Check = item1Check.clone("Item3Check");
        item3Check.setParent(item3);

        UIFrame exitCyclingViewFrame = new UIFrame("ExitCyclingViewFrame", this);
        exitCyclingViewFrame.size = new Dim2(0.065, 0, 0.1, 0).dilate(1.2);
        exitCyclingViewFrame.position = new Dim2(0.99, 0, 0.025, 0);
        exitCyclingViewFrame.anchorPoint = new Vector2(1, 0);
        exitCyclingViewFrame.backgroundTransparency = 0f;
        exitCyclingViewFrame.keepAspectRatio = true;
        exitCyclingViewFrame.visible = false;
        exitCyclingViewFrame.setZIndex(99);

        UIText exitCyclingViewButton = new UIText("ExitCyclingViewButton", this);
        exitCyclingViewButton.size.full().dilate(0.8);
        exitCyclingViewButton.position.center();
        exitCyclingViewButton.anchorPoint.center();
        exitCyclingViewButton.text = "X";
        exitCyclingViewButton.textColor = Color.decode("#ff5d5d");
        exitCyclingViewButton.textScaled = true;
        exitCyclingViewButton.backgroundTransparency = 0f;
        exitCyclingViewButton.setParent(exitCyclingViewFrame);
        exitCyclingViewButton.addReleaseListener((e) -> {
            ((Runnable)cyclingView.getAttribute("Stop")).run();
        });
        animOnHover(exitCyclingViewButton, exitCyclingViewButton);
        animOnPress(exitCyclingViewButton, exitCyclingViewButton);

        UIFrame backArrowFrame = new UIFrame("BackArrowFrame", this);
        backArrowFrame.size = new Dim2(0.065, 0, 0.1, 0).dilate(1.2);
        backArrowFrame.position = new Dim2(0.3, 0, 0.45, 0);
        backArrowFrame.anchorPoint.center();
        backArrowFrame.backgroundTransparency = 0f;
        backArrowFrame.setParent(cyclingView);

        UIImage backArrowButton = new UIImage("BackArrowButton", this);
        backArrowButton.size.full().dilate(0.8);
        backArrowButton.position.center();
        backArrowButton.anchorPoint.center();
        backArrowButton.setImagePath("images/arrow.png");
        backArrowButton.setImageFillType(UIImage.FIT_IMAGE);
        backArrowButton.backgroundTransparency = 0f;
        backArrowButton.setParent(backArrowFrame);
        backArrowButton.addReleaseListener((e) -> {
            ((Runnable)cyclingView.getAttribute("Previous")).run();
        });
        animOnHover(backArrowButton, backArrowButton);
        animOnPress(backArrowButton, backArrowButton);

        UIFrame nextArrowFrame = (UIFrame)backArrowFrame.clone("NextArrowFrame");
        nextArrowFrame.position = new Dim2(0.7, 0, 0.45, 0);
        nextArrowFrame.setParent(cyclingView);

        UIImage nextArrowButton = (UIImage)backArrowButton.clone("NextArrowButton");
        nextArrowButton.rotation = 180;
        nextArrowButton.setParent(nextArrowFrame);
        animOnHover(nextArrowButton, nextArrowButton);
        animOnPress(nextArrowButton, nextArrowButton);
        nextArrowButton.addReleaseListener((e) -> {
            ((Runnable)cyclingView.getAttribute("Next")).run();
        });

        selectButtonFrame.addReleaseListener((e) -> {
            ((Runnable)selectButtonFrame.getAttribute("UpdateVisibility")).run();
            String act = gameScreen.getAttributeOrDefault("Action", "");
            boolean clickable = act.equals("drawBirds") || selectButtonFrame.getAttributeOrDefault("Clickable", false);
            if (clickable) {
                ArrayList<Card> items = (ArrayList<Card>)cyclingView.getAttribute("Items");
                if (items == null) return;
                int i = (int)cyclingView.getAttribute("Index");
                Card card = items.get(i);
                if (act.equals("drawBirds")) {
                    Selectable selected = toggleSelectCard(card);
                    if (selected != null) {
                        selected.setElement(UIImage.getByName("DeckCard" + (i + 1)));
                        UIImage.getByName("DeckCard" + (i + 1)).addTag("Selected");
                    }
                    UIImage item = (UIImage)cyclingView.getAttribute("CurrentImage");
                    UIImage.getByName("DeckCard" + (i + 1) + "Check").visible = selected != null;
                    UIImage.getByName(item.getName() + "Check").visible = selected != null;
                    selectButton.text = selected != null ? "Deselect" : "Select";
                    if (this.selected.size() > gameScreen.getAttributeOrDefault("CanSelectAmount", 0)) {
                        Selectable old = this.selected.getFirst();
                        if (old.getElement() != null) old.getElement().removeTag("Selected");
                        if (old.getElement() != null && old.getElement().hasTag("FaceDown")) {
                            faceDownCardCheck.visible = false;
                        } else {
                            Card c = (Card)old.getValue();
                            UIImage oldItem = null;
                            Card i1c = item1.getAttributeOrDefault("Card", null);
                            Card i2c = item2.getAttributeOrDefault("Card", null);
                            Card i3c = item3.getAttributeOrDefault("Card", null);
                            if (i1c != null && i1c.equals(c)) oldItem = item1; else if (i2c != null && i2c.equals(c)) oldItem = item2; else if (i3c != null && i3c.equals(c)) oldItem = item3;
                            if (oldItem != null) {
                                UIImage.getByName(oldItem.getName() + "Check").visible = false;
                            }
                            int in = items.indexOf(c);
                            if (in > -1) UIImage.getByName("DeckCard" + (in + 1) + "Check").visible = false;
                        }
                        this.selected.remove(old);
                    }
                    confirmDrawFrame.setAttribute("Clickable", this.selected.size() >= gameScreen.getAttributeOrDefault("CanSelectAmount", 0));
                    ((Runnable)confirmDrawFrame.getAttribute("Update")).run();
                    System.out.println(this.selected);
                } else if (act.equals("playBird")) {
                    
                }
            }
        });

        selectButtonFrame.setAttribute("UpdateVisibility", (Runnable)() -> {
            ArrayList<Card> items = (ArrayList<Card>)cyclingView.getAttribute("Items");
            int i = (int)cyclingView.getAttribute("Index");
            String act = gameScreen.getAttributeOrDefault("Action", "");
            boolean valid = act.equals("drawBirds") || act.equals("playBird");
            selectButtonFrame.visible = (!act.equals("") && items.get(i).getClass() != BonusCard.class) && (act.equals("drawBirds") ? deckScreen.visible : true) && (act.equals("playBird") ? handScreen.visible : true) && valid;
            if (!selectButtonFrame.visible) return;
            Card c = items.get(i).getClass() != BonusCard.class ? items.get(i) : null;
            if (c != null && act.equals("playBird")) {
                Player p = currentGame.getPlayers().get(currentGame.getPlayerTurn() - 1);
                Bird bird = (Bird)c;
                boolean can = p.hasEnoughFood(bird);
                selectButtonFrame.setAttribute("Clickable", can);
                selectButtonFrame.backgroundColor = can ? Color.white : Color.gray;
                selectButton.text = "Play";
            } else selectButton.text = isSelected(c) ? "Deselect" : "Select";
        });

        cyclingView.setAttribute("CurrentImage", item1);
        cyclingView.setAttribute("NextImage", item2);
        cyclingView.setAttribute("PrevImage", item3);
        cyclingView.setAttribute("db", false);
        cyclingView.setAttribute("Running", false);
        cyclingView.setAttribute("Run", (Runnable) () -> {
            cyclingView.setAttribute("Running", true);
            ArrayList<Card> items = (ArrayList<Card>)cyclingView.getAttribute("Items");
            int i = (int)cyclingView.getAttribute("Index");
            Card currentCard = items.get(i);
            Card nextCard = items.get((i + 1) % items.size());
            Card prevCard = items.get((i - 1 + items.size()) % items.size());
            ((UIImage)cyclingView.getAttribute("CurrentImage")).setImagePath(currentCard.getImage());
            ((UIImage)cyclingView.getAttribute("NextImage")).setImagePath(nextCard.getImage());
            ((UIImage)cyclingView.getAttribute("PrevImage")).setImagePath(prevCard.getImage());
            ((UIImage)cyclingView.getAttribute("CurrentImage")).setAttribute("Card", currentCard);
            ((UIImage)cyclingView.getAttribute("NextImage")).setAttribute("Card", nextCard);
            ((UIImage)cyclingView.getAttribute("PrevImage")).setAttribute("Card", prevCard);
            cyclingViewBackground.backgroundTransparency = 0f;
            item1.imageTransparency = 0f;
            item2.imageTransparency = 0f;
            item3.imageTransparency = 0f;
            item1Check.imageTransparency = 0f;
            item2Check.imageTransparency = 0f;
            item3Check.imageTransparency = 0f;
            exitCyclingViewButton.textTransparency = 0f;
            nextArrowButton.imageTransparency = 0f;
            backArrowButton.imageTransparency = 0f;
            selectButtonFrame.backgroundTransparency = 0f;
            selectButton.textTransparency = 0f;
            exitCyclingViewFrame.visible = true;
            cyclingView.visible = true;
            ((Runnable)selectButtonFrame.getAttribute("UpdateVisibility")).run();
            UIImage.getByName(((UIImage)cyclingView.getAttribute("CurrentImage")).getName() + "Check").visible = isSelected(currentCard);
            UIImage.getByName(((UIImage)cyclingView.getAttribute("NextImage")).getName() + "Check").visible = isSelected(nextCard);
            UIImage.getByName(((UIImage)cyclingView.getAttribute("PrevImage")).getName() + "Check").visible = isSelected(prevCard);
            cyclingViewBackground.tweenBackgroundTransparency(0.5f, 0.3, Tween.QUAD_IN_OUT);
            item1.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            item2.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            item3.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            item1Check.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            item2Check.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            item3Check.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            nextArrowButton.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            backArrowButton.tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            exitCyclingViewButton.tweenTextTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            selectButtonFrame.tweenBackgroundTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            selectButton.tweenTextTransparency(1f, 0.3, Tween.QUAD_IN_OUT);

        });

        cyclingView.setAttribute("Stop", (Runnable) () -> {
            if ((boolean)cyclingView.getAttribute("db")) return;
            cyclingView.setAttribute("db", true);
            cyclingView.setAttribute("Running", false);
            cyclingViewBackground.tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            item1.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            item2.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            item3.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            item1Check.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            item2Check.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            item3Check.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            nextArrowButton.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            backArrowButton.tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            exitCyclingViewButton.tweenTextTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            selectButtonFrame.tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            selectButton.tweenTextTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            Timer t = new Timer(300, e -> {
                cyclingView.visible = false;
                exitCyclingViewFrame.visible = false;
                cyclingView.setAttribute("db", false);
            });
            t.setRepeats(false);
            t.start();
        });

        cyclingView.setAttribute("Next", (Runnable)() -> {
            if (!(boolean)cyclingView.getAttribute("Running") || (boolean)cyclingView.getAttribute("db")) return;
            cyclingView.setAttribute("db", true);
            UIImage current = (UIImage)cyclingView.getAttribute("CurrentImage");
            UIImage next = (UIImage)cyclingView.getAttribute("NextImage");
            UIImage prev = (UIImage)cyclingView.getAttribute("PrevImage");
            ArrayList<Card> items = (ArrayList<Card>)cyclingView.getAttribute("Items");
            final int i = ((int)cyclingView.getAttribute("Index") + 1) % items.size();
            cyclingView.setAttribute("Index", i);
            current.setZIndex(-2);
            current.tweenPosition(new Dim2(0.65, 0, 0.55, 0), 0.3, Tween.QUAD_IN_OUT);
            current.tweenSize(new Dim2(0.15, 0, 0.3, 0).dilate(2).dilate(0.9), 0.3, Tween.QUAD_IN_OUT);
            next.setZIndex(0);
            next.tweenPosition(new Dim2(0.5, 0, 0.45, 0), 0.3, Tween.QUAD_IN_OUT);
            next.tweenSize(new Dim2(0.15, 0, 0.3, 0).dilate(2), 0.3, Tween.QUAD_IN_OUT);
            prev.setZIndex(-2);
            prev.tweenPosition(new Dim2(0.35, 0, 0.55, 0), 0.3, Tween.QUAD_IN_OUT);
            prev.tweenSize(new Dim2(0.15, 0, 0.3, 0).dilate(2).dilate(0.9), 0.3, Tween.QUAD_IN_OUT);
            UIImage temp = (UIImage)cyclingView.getAttribute("CurrentImage");
            cyclingView.setAttribute("CurrentImage", cyclingView.getAttribute("NextImage"));
            cyclingView.setAttribute("NextImage", cyclingView.getAttribute("PrevImage"));
            cyclingView.setAttribute("PrevImage", temp);
            ((Runnable)selectButtonFrame.getAttribute("UpdateVisibility")).run();
            Timer t = new Timer(150, e -> {
                cyclingView.setAttribute("db", false);
                ((UIImage)cyclingView.getAttribute("NextImage")).setImagePath(items.get((i + 1) % items.size()).getImage());
                UIImage.getByName(((UIImage)cyclingView.getAttribute("NextImage")).getName() + "Check").visible = isSelected(items.get((i + 1) % items.size()));
                ((UIImage)cyclingView.getAttribute("NextImage")).setAttribute("Card", items.get((i + 1) % items.size()));
            });
            t.setRepeats(false);
            t.start();
        });

        cyclingView.setAttribute("Previous", (Runnable)() -> {
            if (!(boolean)cyclingView.getAttribute("Running") || (boolean)cyclingView.getAttribute("db")) return;
            cyclingView.setAttribute("db", true);
            UIImage current = (UIImage)cyclingView.getAttribute("CurrentImage");
            UIImage next = (UIImage)cyclingView.getAttribute("NextImage");
            UIImage prev = (UIImage)cyclingView.getAttribute("PrevImage");
            ArrayList<Card> items = (ArrayList<Card>)cyclingView.getAttribute("Items");
            final int i = ((int)cyclingView.getAttribute("Index") - 1 + items.size()) % items.size();
            cyclingView.setAttribute("Index", i);
            current.setZIndex(-2);
            current.tweenPosition(new Dim2(0.35, 0, 0.55, 0), 0.3, Tween.QUAD_IN_OUT);
            current.tweenSize(new Dim2(0.15, 0, 0.3, 0).dilate(2).dilate(0.9), 0.3, Tween.QUAD_IN_OUT);
            prev.setZIndex(0);
            prev.tweenPosition(new Dim2(0.5, 0, 0.45, 0), 0.3, Tween.QUAD_IN_OUT);
            prev.tweenSize(new Dim2(0.15, 0, 0.3, 0).dilate(2), 0.3, Tween.QUAD_IN_OUT);
            next.setZIndex(-2);
            next.tweenPosition(new Dim2(0.65, 0, 0.55, 0), 0.3, Tween.QUAD_IN_OUT);
            next.tweenSize(new Dim2(0.15, 0, 0.3, 0).dilate(2).dilate(0.9), 0.3, Tween.QUAD_IN_OUT);
            UIImage temp = (UIImage)cyclingView.getAttribute("CurrentImage");
            cyclingView.setAttribute("CurrentImage", cyclingView.getAttribute("PrevImage"));
            cyclingView.setAttribute("PrevImage", cyclingView.getAttribute("NextImage"));
            cyclingView.setAttribute("NextImage", temp);
            ((Runnable)selectButtonFrame.getAttribute("UpdateVisibility")).run();
            Timer t = new Timer(150, e -> {
                cyclingView.setAttribute("db", false);
                ((UIImage)cyclingView.getAttribute("PrevImage")).setImagePath(items.get((i - 1 + items.size()) % items.size()).getImage());
                UIImage.getByName(((UIImage)cyclingView.getAttribute("PrevImage")).getName() + "Check").visible = isSelected(items.get((i - 1 + items.size()) % items.size()));
                ((UIImage)cyclingView.getAttribute("PrevImage")).setAttribute("Card", items.get((i - 1 + items.size()) % items.size()));
            });
            t.setRepeats(false);
            t.start();
        });

        UIFrame playerSelectionContainer = new UIFrame("PlayerSelectionContainer", this);
        playerSelectionContainer.size = new Dim2(0.68, 0, 0.15, 0);
        playerSelectionContainer.position = new Dim2(0.925, 0, 0.95, 0);
        playerSelectionContainer.anchorPoint = new Vector2(1, 1);
        playerSelectionContainer.backgroundTransparency = 0f;
        playerSelectionContainer.setParent(gameScreen);

        UIFrame playerSelectionItems = new UIFrame("PlayerSelectionItems", this);
        playerSelectionItems.backgroundTransparency = 0f;
        playerSelectionItems.keepAspectRatio = true;
        playerSelectionItems.size.full();
        playerSelectionItems.position.center();
        playerSelectionItems.anchorPoint.center();
        playerSelectionItems.setParent(playerSelectionContainer);

        ListLayout playerSelectionLayout = new ListLayout();
        playerSelectionLayout.direction = ListLayout.HORIZONTAL;
        playerSelectionLayout.horizontalAlignment = ListLayout.CENTER;
        playerSelectionLayout.verticalAlignment = ListLayout.CENTER;
        playerSelectionLayout.spacing = new Dim(0.05, 0);
        playerSelectionItems.layout = playerSelectionLayout;

        UIFrame player1ButtonFrame = new UIFrame("Player1ButtonFrame", this);
        player1ButtonFrame.size = new Dim2(0.116, 0, 0.8, 0).dilate(0.9);
        player1ButtonFrame.backgroundTransparency = 0f;
        player1ButtonFrame.setParent(playerSelectionItems);

        UIFrame player1Button = new UIFrame("Player1Button", this);
        player1Button.size.full().dilate(0.9);
        player1Button.position.center();
        player1Button.anchorPoint.center();
        player1Button.backgroundColor = Color.decode("#ce173b");
        player1Button.borderRadius = new Dim(0.2, 0);
        player1Button.strokeColor = Color.white;
        player1Button.strokeThickness = new Dim(0.06, 0);
        player1Button.strokeTransparency = 1f;
        player1Button.setParent(player1ButtonFrame);
        player1Button.addReleaseListener((e) -> {
            boards.setAttribute("Index", 1);
            ((Runnable)boards.getAttribute("ViewBoard")).run();
        });
        animOnHover(player1Button, player1Button);
        animOnPress(player1Button, player1Button);

        UIText player1ButtonText = new UIText("Player1ButtonText", this);
        player1ButtonText.size.full().dilate(0.8);
        player1ButtonText.position.center();
        player1ButtonText.anchorPoint.center();
        player1ButtonText.text = "1";
        player1ButtonText.textColor = Color.white;
        player1ButtonText.textScaled = true;
        player1ButtonText.backgroundTransparency = 0f;
        player1ButtonText.ignore = true;
        player1ButtonText.setParent(player1Button);

        UIFrame player2ButtonFrame = player1ButtonFrame.clone("Player2ButtonFrame");
        player2ButtonFrame.setParent(playerSelectionItems);

        UIFrame player2Button = player1Button.clone("Player2Button");
        player2Button.backgroundColor = Color.decode("#e4b800");
        player2Button.setParent(player2ButtonFrame);
        player2Button.addReleaseListener((e) -> {
            boards.setAttribute("Index", 2);
            ((Runnable)boards.getAttribute("ViewBoard")).run();
        });
        animOnHover(player2Button, player2Button);
        animOnPress(player2Button, player2Button);

        UIText player2ButtonText = player1ButtonText.clone("Player2ButtonText");
        player2ButtonText.setParent(player2Button);
        player2ButtonText.text = "2";

        UIFrame player3ButtonFrame = player1ButtonFrame.clone("Player3ButtonFrame");
        player3ButtonFrame.setParent(playerSelectionItems);

        UIFrame player3Button = player1Button.clone("Player3Button");
        player3Button.backgroundColor = Color.decode("#91bf55");
        player3Button.setParent(player3ButtonFrame);
        player3Button.addReleaseListener((e) -> {
            boards.setAttribute("Index", 3);
            ((Runnable)boards.getAttribute("ViewBoard")).run();
        });
        animOnHover(player3Button, player3Button);
        animOnPress(player3Button, player3Button);

        UIText player3ButtonText = player1ButtonText.clone("Player3ButtonText");
        player3ButtonText.setParent(player3Button);
        player3ButtonText.text = "3";

        UIFrame player4ButtonFrame = player1ButtonFrame.clone("Player4ButtonFrame");
        player4ButtonFrame.setParent(playerSelectionItems);

        UIFrame player4Button = player1Button.clone("Player4Button");
        player4Button.backgroundColor = Color.decode("#0080ab");
        player4Button.setParent(player4ButtonFrame);
        player4Button.addReleaseListener((e) -> {
            boards.setAttribute("Index", 4);
            ((Runnable)boards.getAttribute("ViewBoard")).run();
        });
        animOnHover(player4Button, player4Button);
        animOnPress(player4Button, player4Button);

        UIText player4ButtonText = player1ButtonText.clone("Player4ButtonText");
        player4ButtonText.setParent(player4Button);
        player4ButtonText.text = "4";

        UIFrame player5ButtonFrame = player1ButtonFrame.clone("Player5ButtonFrame");
        player5ButtonFrame.setParent(playerSelectionItems);

        UIFrame player5Button = player1Button.clone("Player5Button");
        player5Button.backgroundColor = Color.decode("#6c2175");
        player5Button.setParent(player5ButtonFrame);
        player5Button.addReleaseListener((e) -> {
            boards.setAttribute("Index", 5);
            ((Runnable)boards.getAttribute("ViewBoard")).run();
        });
        animOnHover(player5Button, player5Button);
        animOnPress(player5Button, player5Button);

        UIText player5ButtonText = player1ButtonText.clone("Player5ButtonText");
        player5ButtonText.setParent(player5Button);
        player5ButtonText.text = "5";

        player1Button.rotation = 45;
        player1ButtonText.rotation = -45;

        ((Runnable)boards.getAttribute("ViewBoard")).run();
        
        popupBackground = new UIFrame("PopupBackground", this);
        popupBackground.backgroundColor = Color.black;
        popupBackground.backgroundTransparency = 0.5f;
        popupBackground.size.full();
        popupBackground.position.center();
        popupBackground.anchorPoint.center();
        popupBackground.setZIndex(100);
        popupBackground.visible = false;
        popupBackground.addReleaseListener((e) -> {
            String type = popupBackground.getAttributeOrDefault("Type", null);
            if (type != null) {
                if (type.equals("yesno")) {
                    Consumer<Boolean> done = popupBackground.getAttributeOrDefault("Done", null);
                    if (done != null) {
                        ((Runnable)popupBackground.getAttribute("Reset")).run();
                        done.accept(false);
                    }
                } else if (type.equals("food")) {
                    Consumer<String> done = popupBackground.getAttributeOrDefault("Done", null);
                    if (done != null) {
                        ((Runnable)popupBackground.getAttribute("Reset")).run();
                        done.accept(null);
                    }
                }
            }
        });
        popupBackground.setAttribute("Reset", (Runnable)() -> {
            popupBackground.tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupContainer.tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupChoice1Frame.tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupChoice2Frame.tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupChoice1.tweenTextTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupChoice2.tweenTextTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupChoice1Frame.tweenStrokeTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupChoice2Frame.tweenStrokeTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            popupPrompt.tweenTextTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            for (UIElement f : UIElement.getByName("PopupFoodChoices").getChildren()) {
                String name = f.getName().substring(0, f.getName().length() - 9);
                UIFrame.getByName(name + "Frame").tweenBackgroundTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
                UIFrame.getByName(name + "Frame").tweenStrokeTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
                UIImage.getByName(name + "Icon").tweenImageTransparency(0f, 0.3, Tween.QUAD_IN_OUT);
            }
            Timer t = new Timer(300, (e) -> {
                popupBackground.visible = false;
            });
            t.setRepeats(false);
            t.start();
        });
        
        popupContainer = new UIFrame("PopupContainer", this);
        popupContainer.borderRadius = new Dim(0.07, 0);
        //popupContainer.strokeColor = Color.lightGray;
        popupContainer.backgroundColor = Color.decode("#faf4f4");
        //popupContainer.strokeTransparency = 1f;
        //popupContainer.strokeThickness = new Dim(0.005, 0);
        popupContainer.setParent(popupBackground);
        popupContainer.position.center();
        popupContainer.anchorPoint.center();
        popupContainer.keepAspectRatio = true;
        popupContainer.size = new Dim2(0.5, 0, 0.4, 0);
        
        popupPrompt = new UIText("PopupPrompt", this);
        popupPrompt.backgroundTransparency = 0f;
        popupPrompt.textColor = Color.black;
        popupPrompt.size = new Dim2(0.9, 0, 0.5, 0);
        popupPrompt.anchorPoint.center();
        popupPrompt.horizontalAlignment = UIText.CENTER;
        popupPrompt.position = new Dim2(0.5, 0, 0.35, 0);
        popupPrompt.textScaled = true;
        popupPrompt.text = "Popup";
        popupPrompt.setParent(popupContainer);

        UIFrame popupChoices = new UIFrame("PopupChoices", this);
        popupChoices.size = new Dim2(0.9, 0, 0.25, 0);
        popupChoices.backgroundColor = Color.black;
        popupChoices.backgroundTransparency = 0f;
        popupChoices.position = new Dim2(0.5, 0, 0.9, 0);
        popupChoices.anchorPoint = new Vector2(0.5, 1);
        popupChoices.setParent(popupContainer);

        UIFrame popupFoodChoices = popupChoices.clone("PopupFoodChoices");
        popupFoodChoices.visible = false;
        popupFoodChoices.setParent(popupContainer);

        ListLayout popupChoicesLayout = new ListLayout();
        popupChoicesLayout.direction = ListLayout.HORIZONTAL;
        popupChoicesLayout.horizontalAlignment = ListLayout.CENTER;
        popupChoicesLayout.verticalAlignment = ListLayout.MIDDLE;
        popupChoicesLayout.spacing = new Dim(0.05, 0);
        popupChoices.layout = popupChoicesLayout;

        ListLayout popupFoodChoicesLayout = new ListLayout();
        popupFoodChoicesLayout.direction = ListLayout.HORIZONTAL;
        popupFoodChoicesLayout.horizontalAlignment = ListLayout.CENTER;
        popupFoodChoicesLayout.verticalAlignment = ListLayout.MIDDLE;
        popupFoodChoicesLayout.spacing = new Dim(0.05, 0);
        popupFoodChoices.layout = popupFoodChoicesLayout;

        UIFrame popupChoice1Container = new UIFrame("PopupChoice1Container", this);
        popupChoice1Container.size = new Dim2(0.3, 0, 0.9, 0);
        popupChoice1Container.backgroundTransparency = 0f;
        popupChoice1Container.setParent(popupChoices);

        popupChoice1Frame = new UIFrame("PopupChoice1Frame", this);
        popupChoice1Frame.size.full();
        popupChoice1Frame.position.center();
        popupChoice1Frame.anchorPoint.center();
        popupChoice1Frame.borderRadius = new Dim(0.45, 0);
        popupChoice1Frame.strokeColor = Color.black;
        popupChoice1Frame.strokeTransparency = 1f;
        popupChoice1Frame.strokeThickness = new Dim(0.075, 0);
        popupChoice1Frame.setParent(popupChoice1Container);
        popupChoice1Frame.addReleaseListener((e) -> {
            Consumer<Boolean> done = popupBackground.getAttributeOrDefault("Done", null);
            if (done != null) {
                ((Runnable)popupBackground.getAttribute("Reset")).run();
                done.accept(true);
            }
        });

        popupChoice1 = new UIText("PopupChoice1", this);
        popupChoice1.size.full().dilate(0.8);
        popupChoice1.position.center();
        popupChoice1.backgroundTransparency = 0f;
        popupChoice1.anchorPoint.center();
        popupChoice1.setParent(popupChoice1Frame);
        popupChoice1.textScaled = true;
        popupChoice1.ignore = true;
        popupChoice1.text = "Yes";
        animOnHover(popupChoice1Frame, popupChoice1Frame);
        animOnPress(popupChoice1Frame, popupChoice1Frame);

        UIFrame popupChoice2Container = popupChoice1Container.clone("PopupChoice2Container");
        popupChoice2Container.setParent(popupChoices);

        popupChoice2Frame = popupChoice1Frame.clone("PopupChoice2Frame");
        popupChoice2Frame.setParent(popupChoice2Container);
        popupChoice2Frame.addReleaseListener((e) -> {
            Consumer<Boolean> done = popupBackground.getAttributeOrDefault("Done", null);
            if (done != null) {
                ((Runnable)popupBackground.getAttribute("Reset")).run();
                done.accept(false);
            }
        });

        popupChoice2 = popupChoice1.clone("PopupChoice2");
        popupChoice2.text = "No";
        popupChoice2.setParent(popupChoice2Frame);
        animOnHover(popupChoice2Frame, popupChoice2Frame);
        animOnPress(popupChoice2Frame, popupChoice2Frame);

        String[] foods = new String[] {"Worm", "Seed", "Fish", "Berry", "Rat"};
        for (String food : foods) {
        	createFoodChoice(food);
        	
        	UIFrame foodStatFrame = new UIFrame(food + "StatFrame", this);
        	foodStatFrame.size = new Dim2(1, 0, 0.2, 0);
        	foodStatFrame.backgroundTransparency = 0f;
        	foodStatFrame.setParent(foodsStatFrame);
        	
        	UIText foodStat = new UIText(food + "Stat", this);
        	foodStat.setParent(foodStatFrame);
        	foodStat.textScaled = true;
        	foodStat.text = "2";
        	foodStat.textColor = Color.black;
        	foodStat.horizontalAlignment = UIText.LEFT;
            foodStat.backgroundTransparency = 0f;
            foodStat.backgroundColor = Color.black;
            foodStat.anchorPoint = new Vector2(0, 0.5);
            foodStat.position = new Dim2(0.65, 0, 0.52, 0);
            foodStat.size = new Dim2(0.4, 0, 0.8, 0);

            UIFrame birdFeederFoodContainer = new UIFrame(food + "BirdFeederFoodContainer", this);
            birdFeederFoodContainer.size = new Dim2(0.122, 0, 1, 0);
            birdFeederFoodContainer.backgroundTransparency = 0f;
            birdFeederFoodContainer.setParent(birdFeederChoicesContainer);

            UIFrame birdFeederFoodFrame = new UIFrame(food + "BirdFeederFoodFrame", this);
            birdFeederFoodFrame.size.full().dilate(0.9);
            birdFeederFoodFrame.position.center();
            birdFeederFoodFrame.anchorPoint.center();
            birdFeederFoodFrame.backgroundTransparency = 1f;
            birdFeederFoodFrame.backgroundColor = Color.decode("#dfd2c9");
            birdFeederFoodFrame.borderRadius = new Dim(0.3, 0);
            birdFeederFoodFrame.strokeColor = Color.white;
            birdFeederFoodFrame.strokeThickness = new Dim(0.03, 0);
            birdFeederFoodFrame.strokeTransparency = 1f;
            birdFeederFoodFrame.setParent(birdFeederFoodContainer);

            UIImage birdFeederFoodIcon = new UIImage(food + "BirdFeederFoodIcon", this);
            birdFeederFoodIcon.setParent(birdFeederFoodFrame);
            birdFeederFoodIcon.setImagePath("foods/" + food.toLowerCase() + "_ns.png");
            birdFeederFoodIcon.setImageFillType(UIImage.FIT_IMAGE);
            birdFeederFoodIcon.size = new Dim2(0.7, 0, 0.7, 0);
            birdFeederFoodIcon.position.center();
            birdFeederFoodIcon.anchorPoint.center();
            birdFeederFoodIcon.ignore = true;
            birdFeederFoodIcon.backgroundTransparency = 0f;

            UIFrame birdFeederFoodCover = new UIFrame(food + "BirdFeederFoodCover", this);
            birdFeederFoodCover.size.full().dilate(1.04);
            birdFeederFoodCover.position.center();
            birdFeederFoodCover.anchorPoint.center();
            birdFeederFoodCover.backgroundTransparency = 0f;
            birdFeederFoodCover.backgroundColor = Color.black;
            birdFeederFoodCover.setParent(birdFeederFoodFrame);
            birdFeederFoodCover.setZIndex(1);
            birdFeederFoodCover.ignore = true;
            birdFeederFoodCover.borderRadius = new Dim(0.3, 0);

            UIImage birdFeederFoodCheck = deckCard1Check.clone(food + "BirdFeederFoodCheck");
            birdFeederFoodCheck.setParent(birdFeederFoodFrame);
            birdFeederFoodCheck.visible = true;
            birdFeederFoodCheck.size = new Dim2(0.2, 0, 0.2, 0);
            birdFeederFoodCheck.position = new Dim2(1, 0, 0, 0);

            animOnHover(birdFeederFoodFrame, birdFeederFoodFrame);
            animOnPress(birdFeederFoodFrame, birdFeederFoodFrame);
            pressCover(birdFeederFoodFrame, birdFeederFoodCover);

            UIFrame popupFoodChoiceContainer = new UIFrame(food + "PopupFoodChoiceContainer", this);
            popupFoodChoiceContainer.size = new Dim2(0.14, 0, 1, 0);
            popupFoodChoiceContainer.backgroundTransparency = 0f;
            popupFoodChoiceContainer.setParent(popupFoodChoices);

            UIFrame popupFoodChoiceFrame = new UIFrame(food + "PopupFoodChoiceFrame", this);
            popupFoodChoiceFrame.size.full().dilate(0.9);
            popupFoodChoiceFrame.position.center();
            popupFoodChoiceFrame.anchorPoint.center();
            popupFoodChoiceFrame.backgroundTransparency = 1f;
            popupFoodChoiceFrame.backgroundColor = Color.decode("#dfd2c9");
            popupFoodChoiceFrame.borderRadius = new Dim(0.3, 0);
            popupFoodChoiceFrame.strokeColor = Color.white;
            popupFoodChoiceFrame.strokeThickness = new Dim(0.03, 0);
            popupFoodChoiceFrame.strokeTransparency = 1f;
            popupFoodChoiceFrame.setParent(popupFoodChoiceContainer);
            popupFoodChoiceFrame.addReleaseListener((e) -> {
                Consumer<String> done = popupBackground.getAttributeOrDefault("Done", null);
                if (done != null) {
                    ((Runnable)popupBackground.getAttribute("Reset")).run();
                    done.accept(food);
                }
            });

            UIImage popupFoodChoiceIcon = new UIImage(food + "PopupFoodChoiceIcon", this);
            popupFoodChoiceIcon.setParent(popupFoodChoiceFrame);
            popupFoodChoiceIcon.setImagePath("foods/" + food.toLowerCase() + "_ns.png");
            popupFoodChoiceIcon.setImageFillType(UIImage.FIT_IMAGE);
            popupFoodChoiceIcon.size = new Dim2(0.7, 0, 0.7, 0);
            popupFoodChoiceIcon.position.center();
            popupFoodChoiceIcon.anchorPoint.center();
            popupFoodChoiceIcon.ignore = true;
            popupFoodChoiceIcon.backgroundTransparency = 0f;

            UIFrame popupFoodChoiceCover = new UIFrame(food + "PopupFoodChoiceCover", this);
            popupFoodChoiceCover.size.full().dilate(1.04);
            popupFoodChoiceCover.position.center();
            popupFoodChoiceCover.anchorPoint.center();
            popupFoodChoiceCover.backgroundTransparency = 0f;
            popupFoodChoiceCover.backgroundColor = Color.black;
            popupFoodChoiceCover.setParent(popupFoodChoiceFrame);
            popupFoodChoiceCover.setZIndex(1);
            popupFoodChoiceCover.ignore = true;
            popupFoodChoiceCover.borderRadius = new Dim(0.3, 0);

            animOnHover(popupFoodChoiceFrame, popupFoodChoiceFrame);
            animOnPress(popupFoodChoiceFrame, popupFoodChoiceFrame);
            pressCover(popupFoodChoiceFrame, popupFoodChoiceCover);
        }

        promptPlayerFood("please", (a) -> System.out.println("clicked on " + a));
    }

    public void choosePlayingScreen(UIElement screen) {
        if (chosenScreen != null && screen != chosenScreen) {
            chosenScreen.visible = false;
            UIElement button = (UIElement)chosenScreen.getAttribute("Button");
            if (button != null) {
                if (button.getName().equals("HandButton")) {
                    UIElement.getByName(button.getName() + "BirdOutline").strokeColor = Color.white;
                    UIElement.getByName(button.getName() + "BonusOutline").strokeColor = Color.white;
                } else {
                    UIElement.getByName(button.getName() + "Outline").strokeColor = Color.white;
                }
            }
            if (chosenScreen == boardScreen) {
                UIImage currentBoard = (UIImage)(UIFrame.getByName("Boards").getAttribute("Current"));
                if (currentBoard != null) {
                    UIFrame.getByName("Player" + currentBoard.getName().split("PlayerBoard")[1] + "Button").strokeColor = Color.white;
                }
            }
        }
        screen.visible = true;
        chosenScreen = screen;
        UIElement button = (UIElement)screen.getAttribute("Button");
        if (button != null) {
            if (button.getName().equals("HandButton")) {
                UIElement.getByName(button.getName() + "BirdOutline").strokeColor = Color.decode("#54d648");
                UIElement.getByName(button.getName() + "BonusOutline").strokeColor = Color.decode("#54d648");
            } else {
                UIElement.getByName(button.getName() + "Outline").strokeColor = Color.decode("#54d648");
            }
        }
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

    private UIFrame createRow(int p) {
        UIFrame playerCardsContainer = (UIFrame)UIElement.getByName("Player" + p + "CardsContainer");
        UIFrame row = new UIFrame("Row" + p + "_" + playerCardsContainer.getChildren().size(), this);
        row.backgroundTransparency = 0f;
        row.size = new Dim2(1, 0, 0.6, 0);
        row.setParent(playerCardsContainer);

        ListLayout rowLayout = new ListLayout();
        rowLayout.direction = ListLayout.HORIZONTAL;
        rowLayout.horizontalAlignment = ListLayout.CENTER;
        rowLayout.verticalAlignment = ListLayout.CENTER;
        rowLayout.spacing = new Dim(0.02, 0);
        row.layout = rowLayout;

        return row;
    }

    private UIFrame createPlayerCardsContainer(int p) {
        UIFrame playerCardsContainer = new UIFrame("Player" + p + "CardsContainer", this);
        playerCardsContainer.backgroundTransparency = 0f;
        playerCardsContainer.size.full();
        playerCardsContainer.position.center();
        playerCardsContainer.anchorPoint.center();
        playerCardsContainer.visible = false;
        playerCardsContainer.setParent(UIFrame.getByName("HandCardsContainer"));

        ListLayout playerCardsRowsLayout = new ListLayout();
        playerCardsRowsLayout.direction = ListLayout.VERTICAL;
        playerCardsRowsLayout.horizontalAlignment = ListLayout.CENTER;
        playerCardsRowsLayout.verticalAlignment = ListLayout.CENTER;
        playerCardsContainer.layout = playerCardsRowsLayout;

        //System.out.println("Creating player " + p + " cards container");

        createRow(p);

        return playerCardsContainer;
    }

    public void addToPlayerHand(int p, Card card) {
        UIFrame playerCardsContainer = UIFrame.getByName("Player" + p + "CardsContainer");
        //System.out.println("Adding card: " + card + " to player " + p + " hand. Container? " + (playerCardsContainer != null));
        if (playerCardsContainer == null) playerCardsContainer = createPlayerCardsContainer(p);
        ArrayList<UIElement> rows = playerCardsContainer.getChildren();
        UIElement lastRow = rows.get(rows.size() - 1);
        UIFrame cardContainer = new UIFrame("HandCard" + p + "_" + rows.size() + "_" + lastRow.getChildren().size(), this);
        cardContainer.size = new Dim2(0.22, 0, 1, 0);
        cardContainer.backgroundTransparency = 0f;
        if (lastRow.getChildren().size() >= 5) lastRow = createRow(p);
        cardContainer.setParent(lastRow);
        UIImage cardImage = new UIImage("HandCardImage" + p + "_" + rows.size() + "_" + lastRow.getChildren().size(), this);
        cardImage.size.full();
        cardImage.position.center();
        cardImage.anchorPoint.center();
        cardImage.backgroundTransparency = 0f;
        cardImage.setImagePath(card.getImage());
        cardImage.setImageFillType(UIImage.FIT_IMAGE);
        cardImage.setParent(cardContainer);
        animOnHover(cardImage, cardImage);
        animOnPress(cardImage, cardImage);
        cardImage.setAttribute("Card", card);
        cardImage.addReleaseListener(e -> {
            Player player = currentGame.getPlayers().get(p);
            ArrayList<Card> all = new ArrayList<>(player.getBirdHand()); all.addAll(player.getBonusHand());
            cyclingView.setAttribute("Items", all);
            cyclingView.setAttribute("Index", all.indexOf(cardImage.getAttribute("Card")));
            ((Runnable)cyclingView.getAttribute("Run")).run();
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

    public void promptPlayer(String question, String option1, String option2, Consumer<Boolean> callback) {
        popupBackground.setAttribute("Type", "yesno");
        popupBackground.setAttribute("Done", callback);
        UIElement.getByName("PopupFoodChoices").visible = false;
        UIElement.getByName("PopupChoices").visible = true;
        popupPrompt.text = question;
        popupChoice1.text = option1;
        popupChoice2.text = option2;
        popupBackground.backgroundTransparency = 0f;
        popupContainer.backgroundTransparency = 0f;
        popupChoice1Frame.backgroundTransparency = 0f;
        popupChoice2Frame.backgroundTransparency = 0f;
        popupChoice1.textTransparency = 0f;
        popupChoice2.textTransparency = 0f;
        popupChoice1Frame.strokeTransparency = 0f;
        popupChoice2Frame.strokeTransparency = 0f;
        popupPrompt.textTransparency = 0f;
        popupPrompt.text = question;
        popupChoice1.text = option1;
        popupChoice2.text = option2;
        popupBackground.visible = true;
        popupBackground.tweenBackgroundTransparency(0.5f, 0.3, Tween.QUAD_IN_OUT);
        popupContainer.tweenBackgroundTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        popupChoice1.tweenTextTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        popupChoice2.tweenTextTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        popupChoice1Frame.tweenStrokeTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        popupChoice2Frame.tweenStrokeTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        popupPrompt.tweenTextTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
    }

    public void promptPlayerFood(String question, Consumer<String> callback) {
        popupBackground.setAttribute("Type", "food");
        popupBackground.setAttribute("Done", callback);
        UIElement.getByName("PopupFoodChoices").visible = true;
        UIElement.getByName("PopupChoices").visible = false;
        popupPrompt.text = question;
        popupBackground.backgroundTransparency = 0f;
        popupContainer.backgroundTransparency = 0f;
        popupPrompt.textTransparency = 0f;
        popupPrompt.text = question;
        for (UIElement f : UIElement.getByName("PopupFoodChoices").getChildren()) {
            String name = f.getName().substring(0, f.getName().length() - 9);
            UIFrame.getByName(name + "Frame").backgroundTransparency = 0f;
            UIFrame.getByName(name + "Frame").strokeTransparency = 0f;
            UIImage.getByName(name + "Icon").imageTransparency = 0f;
        }
        popupBackground.visible = true;
        popupBackground.tweenBackgroundTransparency(0.5f, 0.3, Tween.QUAD_IN_OUT);
        popupContainer.tweenBackgroundTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        for (UIElement f : UIElement.getByName("PopupFoodChoices").getChildren()) {
            String name = f.getName().substring(0, f.getName().length() - 9);
            UIFrame.getByName(name + "Frame").tweenBackgroundTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            UIFrame.getByName(name + "Frame").tweenStrokeTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
            UIImage.getByName(name + "Icon").tweenImageTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
        }
        popupPrompt.tweenTextTransparency(1f, 0.3, Tween.QUAD_IN_OUT);
    }

    private TreeSet<Selectable> selected = new TreeSet<>();

    public Selectable toggleSelectCard(Card card) {
        if (card == null) return null;
        Selectable found = null;
        for (Selectable v : selected) {
            if (v.getValue() != null && ((Card)v.getValue()).equals(card)) {
                found = v;
                break;
            }
        }
        if (found != null) {
            selected.remove(found);
            return null;
        } else {
            return selectCard(card);
        }
    }

    public Selectable selectCard(Card card) {
        if (card == null) return null;
        Selectable a = new Selectable(card, null);
        selected.add(a);
        return a;
    }

    public void deselectCard(Card card) {
        if (card == null) return;
        Selectable found = null;
        for (Selectable v : selected) {
            if (v.getValue() != null && ((Card)v.getValue()).equals(card)) {
                found = v;
                break;
            }
        }
        if (found != null) {
            selected.remove(found);
        }
    }

    public boolean isSelected(Card card) {
        if (card == null) return false;
        for (Selectable v : selected) {
            if (v.getValue() != null && ((Card)v.getValue()).equals(card)) return true;
        }

        return false;
    }
}

