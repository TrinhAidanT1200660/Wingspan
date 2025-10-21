import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.Timer;

class Vector2 {
    // x and y coordinates; default (0, 0)
    private double x = 0;
    private double y = 0;

    public Vector2() { }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public String toString() { return "(" + x + ", " + y + ")"; }
}

class Dim2 {
    /*
     * this class represents a 2D coordinate, similar to Vector2 with x and y, but it allows for:
     * scale: a percentage of the screen size; half screen would be 0.5 scale for example. 
     * offset: a number that isnt affected by screen size. if you had 500 offset along x, and screen size was 400x400, then 500 offset would be off the screen 
     * and you wouldn't be able to see it.
     */
    private double xScale = 0;
    private int xOffset = 0;
    private double yScale = 0;
    private int yOffset = 0;

    public Dim2() { }

    public Dim2(double xScale, int xOffset, double yScale, int yOffset) {
        this.xScale = xScale;
        this.xOffset = xOffset;
        this.yScale = yScale;
        this.yOffset = yOffset;
    }

    public double getXScale() { return xScale; }
    public int getXOffset() { return xOffset; }
    public double getYScale() { return yScale; }
    public int getYOffset() { return yOffset; }
    public void setXScale(double xScale) { this.xScale = xScale; }
    public void setXOffset(int xOffset) { this.xOffset = xOffset; }
    public void setYScale(double yScale) { this.yScale = yScale; }
    public void setYOffset(int yOffset) { this.yOffset = yOffset; }
    public String toString() { return "{(" + xScale + ", " + xOffset + "), (" + yScale + ", " + yOffset + ")}"; }

    // ok so basically what this method does is it returns a new Dim2 that starts from Dim2 and finshes a fraction of the way to Dim2 goal, determined by a (alpha). just used for animations/tweening
    public static Dim2 lerp(Dim2 start, Dim2 goal, double a) {
        double xScale = start.xScale + (goal.xScale - start.xScale) * a;
        double xOffset = start.xOffset + (goal.xOffset - start.xOffset) * a;
        double yScale = start.yScale + (goal.yScale - start.yScale) * a;
        double yOffset = start.yOffset + (goal.yOffset - start.yOffset) * a;
        return new Dim2(xScale, (int)xOffset, yScale, (int)yOffset);
    }
}

interface ClickListener { 
    // used to set custom click events
    void onClick(MouseEvent e);
}

interface PressListener { 
    // used to set custom events when they press down
    void onPress(MouseEvent e);
}

interface ReleaseListener { 
    // used to set custom events when they release
    void onRelease(MouseEvent e);
}

interface HoverListener { 
    // used to set custom hover events
    void onHover(MouseEvent e);
}

interface ExitListener { 
    // used to set custom exit events
    void onExit(MouseEvent e);
}

interface TweenEvent { 
    // used to set custom events when a tween finishes
    void run();
}

class Tween {
    // private variables for tween
    private Object startValue;
    private Object endValue;
    public long startTime;
    private long duration;
    public long endTime;
    public String property;
    private String propertyType;
    private int animationType;
    public boolean complete;
    public boolean cancelTween;
    public boolean skipTween;
    protected TweenEvent onFinishEvent;
    public UIElement element;

    // easing types, influence the way animation will progress/start/end
    public static int LINEAR = 0;
    public static int QUAD_IN = 1;
    public static int QUAD_OUT = 2;
    public static int QUAD_IN_OUT = 3;
    public static int CUBIC_IN = 4;
    public static int CUBIC_OUT = 5;
    public static int CUBIC_IN_OUT = 6;
    public static int OVERSHOOT = 7;
    public static int ELASTIC = 8;

    public Tween(UIElement element, Object startValue, Object endValue, long duration, String property, String propertyType, int animationType) {
        this.element = element;
        this.startValue = startValue;
        this.endValue = endValue;
        this.duration = duration;
        this.property = property;
        this.propertyType = propertyType;
        this.animationType = animationType;
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + this.duration;
    }

    /*
     * alpha is like a percentage of how "complete" the tween is. if im tweening position
     * from (xScale: 0, xOffset: 0), (yScale: 0, yOffset: 0) to (xScale: 0.9, xOffset: 0), (yScale: 0.9, yOffset: 0)
     * and alpha was 0.5, then the new position would be (xScale: 0.45, xOffset: 0), (yScale: 0.45, yOffset: 0)
     * alpha is calcualted by getting how much time has passed since the animation started, and dividing that by animation duration
     * alpha in some cases can go over 1 or below 0, for example in easing style overshoot
     */
    public double getAlpha() {
        long timePassed = System.currentTimeMillis() - startTime;
        return (double)timePassed / duration;
    }

    public Object simulate() {
        return simulate(getAlpha());
    }

    // this is to calculate the current spot in the animation using alpha
    public Object simulate(double alpha) {
        switch (propertyType.toLowerCase()) {
            case "int":
                return tweenInt((int)startValue, (int)endValue, alpha);
            case "double":
                return tweenDouble((double)startValue, (double)endValue, alpha);
            case "float":
                return tweenFloat((float)startValue, (float)endValue, alpha);
            case "dim2":
                return tweenDim2((Dim2)startValue, (Dim2)endValue, alpha);
            case "vector2":
                return tweenVector2((Vector2)startValue, (Vector2)endValue, alpha);
            case "color":
                return tweenColor((Color)startValue, (Color)endValue, alpha);
            default:
                throw new IllegalArgumentException(propertyType + " isn't a valid tweenable property type!");
        }
    }

    // methods below determine value in animation given alpha

    private int tweenInt(int startValue, int endValue, double alpha) {
        return (int)tweenDouble(startValue, endValue, alpha);
    }

    private double tweenDouble(double startValue, double endValue, double alpha) {
        return startValue + (endValue - startValue) * handleAnimationAlpha(alpha, animationType);
    }

    private float tweenFloat(float startValue, float endValue, double alpha) {
        return Math.min(1.0f, Math.max((float)(startValue + (endValue - startValue) * handleAnimationAlpha(alpha, animationType)), 0.0f));
    }

    private Vector2 tweenVector2(Vector2 startValue, Vector2 endValue, double alpha) {
        alpha = handleAnimationAlpha(alpha, animationType);
        double x = startValue.getX() + (endValue.getX() - startValue.getX()) * alpha;
        double y = startValue.getY() + (endValue.getY() - startValue.getY()) * alpha;
        return new Vector2(x, y);
    }

    private Dim2 tweenDim2(Dim2 startValue, Dim2 endValue, double alpha) {
        return Dim2.lerp(startValue, endValue, handleAnimationAlpha(alpha, animationType));
    }

    private Color tweenColor(Color startValue, Color endValue, double alpha) {
        alpha = Math.min(1.0, Math.max(0, handleAnimationAlpha(alpha, animationType)));
        int r = (int)(startValue.getRed() + (endValue.getRed() - startValue.getRed()) * alpha);
        int g = (int)(startValue.getGreen() + (endValue.getGreen() - startValue.getGreen()) * alpha);
        int b = (int)(startValue.getBlue() + (endValue.getBlue() - startValue.getBlue()) * alpha);
        int a = (int)(startValue.getAlpha() + (endValue.getAlpha() - startValue.getAlpha()) * alpha);
        return new Color(r, g, b, a);
    }

    // cancel
    public void cancel() {
        cancelTween = true;
    }

    // skip to end
    public void skipToEnd() {
        skipTween = true;
    }

    // if user wants to do something when the animation is finished they can set that here
    public void onFinish(TweenEvent e) {
        onFinishEvent = e;
    }

    // easing types

    private double quadIn(double a) {
        return a * a;
    }

    private double quadOut(double a) {
        return 1 - ((1 - a) * (1 - a));
    }

    private double quadInOut(double a) {
        return a < 0.5 ? 2 * a * a : 1 - Math.pow(-2 * a + 2, 2) / 2;
    }

    private double cubicIn(double a) {
        return Math.pow(a, 3);
    }

    private double cubicOut(double a) {
        return 1 - Math.pow(1 - a, 3);
    }

    private double cubicInOut(double a) {
        return a < 0.5 ? 4 * a * a * a : 1 - Math.pow(-2 * a + 2, 3) / 2;
    }

    private double elastic(double a) {
        return a == 0 ? 0 : a == 1 ? 1 : -Math.pow(2, 10 * a - 10) * Math.sin((a * 10 - 10.75) * (2 * Math.PI) / 3);
    }

    private double overshoot(double a) {
        double c1 = 1.70158 * 1.525;
        return a < 0.5 ? (Math.pow(2 * a, 2) * ((c1 + 1) * 2 * a - c1)) / 2 : (Math.pow(2 * a - 2, 2) * ((c1 + 1) * (a * 2 - 2) + c1) + 2) / 2;
    }

    // this takes the animation type given and returns alpha based on that
    private double handleAnimationAlpha(double a, int animationType) {
        if (animationType == 1) {
            a = quadIn(a);
        } else if (animationType == 2) {
            a = quadOut(a);
        } else if (animationType == 3) {
            a = quadInOut(a);
        } else if (animationType == 4) {
            a = cubicIn(a);
        } else if (animationType == 5) {
            a = cubicOut(a);
        } else if (animationType == 6) {
            a = cubicInOut(a);
        } else if (animationType == 7) {
            a = overshoot(a);
        } else if (animationType == 8) {
            a = elastic(a);
        }
        return a;
    }
}

class UIElement {
    // oh boy
    // this is like the big dog like the framework for everything

    // private/public values
    private static HashMap<JPanel, UIElement> rootElements = new HashMap<>(); // rootElements act like containers for all the UIElements; each panel has its own "root element". this is used to control all UIElements and handle zIndex (which are essentially layers) and handle clicking
    protected Vector2 absolutePosition = new Vector2(); // the final literal position on the screen, represented in pixels, after scale and offset calculations
    protected Vector2 absoluteSize = new Vector2(); // the final literal size on the screen, represented in pixels, after scale and offset calculations
    protected double absoluteRotation = 0; // the final literal rotation on the screen after adding rotations from its ancestors (degrees)
    public Vector2 anchorPoint = new Vector2(); // controls where to "anchor" the ui element. 0,0 would be top left corner of the element; 0.5,0.5 would be middle; 1,1 would be bottom right corner
    public Dim2 position = new Dim2(); // position using scale and offset
    public Dim2 size = new Dim2(0, 100, 0, 100); // size using scale and offset; default size 100x100 pixels
    public double rotation = 0; // rotation in degrees
    public Color backgroundColor = Color.WHITE; // background color of frame.
    public float backgroundTransparency = 1f; // this number will determine the element's background's transparency. 1 -> fully visible, 0 -> invisible
    public int borderRadius = 0; // rounded corners
    public int strokeThickness = 0;
    public float strokeTransparency = 0.0f;
    public Color strokeColor = Color.BLUE;
    private int zIndex = 0; // represents the layer the element is on. keep in mind that zIndex is relative, meaning all children will inherit its parent's zIndex
    public boolean visible = true;
    public boolean keepAspectRatio = false;
    private double aspectRatio = -1;
    protected UIElement parent; // the element that this element is inside of (if there is one)
    protected ArrayList<UIElement> children = new ArrayList<>(); // all the elements that are inside this one
    private ArrayList<ClickListener> clickListeners = new ArrayList<>(); // used for click events
    private ArrayList<PressListener> pressListeners = new ArrayList<>(); // used for press down events
    private ArrayList<ReleaseListener> releaseListeners = new ArrayList<>(); // used for release events
    private ArrayList<HoverListener> hoverListeners = new ArrayList<>(); // used for hover events
    private ArrayList<ExitListener> exitListeners = new ArrayList<>(); // used for hover exit events
    private HashSet<UIElement> pressed = new HashSet<>();
    private boolean isHovered = false;
    protected JPanel panel;
    private boolean resort = false; // used whenever we need to resort the drawing order based on z-index
    
    // animation related stuff
    protected static HashSet<UIImage> sprites = new HashSet<>();
    private static ArrayList<Tween> tweens = new ArrayList<>();
    private static Timer timer;
    private static boolean isTweening = false;

    public UIElement(JPanel panel) {
        this.panel = panel;
        // this is making the root element in case there isn't one
        if (!rootElements.containsKey(panel)) {
            UIElement root = new UIElement(panel, true);
            root.size = new Dim2(1, 0, 1, 0);
            root.position = new Dim2(0, 0, 0, 0);
            root.anchorPoint = new Vector2(0, 0);
            root.backgroundTransparency = 0f;
            rootElements.put(panel, root);
        }
        UIElement root = rootElements.get(panel);
        if (this != root) {
            root.addChild(this);
        }
    }

    // used for making roots
    private UIElement(JPanel panel, boolean isRoot) {
        if (isRoot) this.panel = panel;
    }

    // get the root element for a jpanel; acts like the whole container for all the UIElements basically
    public static UIElement getRootForPanel(JPanel p) {
        return rootElements.get(p);
    }

    public void setZIndex(int zIndex) {
        resort = true;
        this.zIndex = zIndex;
    }
    public int getZIndex() {
        return zIndex;
    }

    // used to add children to element
    public void addChild(UIElement child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
            child.parent.resort = true;
        }
        resort = true;
        child.parent = this;
        children.add(child);
    }

    // remove element's parent and set it to root element
    public void clearParent() {
        getRootForPanel(panel).addChild(this);
    }

    // remove element's parent and add element to a given element
    public void setParent(UIElement p) {
        p.addChild(this);
    }

    public ArrayList<UIElement> getChildren() {
        return children;
    }

    public void resetAspectRatio() {
        aspectRatio = absoluteSize.getY() / absoluteSize.getX();
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    // returns either current height of panel or height of its parent if it has one. used for scale values
    private double getHeight() {
        if (parent != null) return parent.absoluteSize.getY();
        return panel.getHeight();
    }

    // returns either current width of panel or width of its parent if it has one. used for scale values
    private double getWidth() {
        if (parent != null) return parent.absoluteSize.getX();
        return panel.getWidth();
    }

    /*
     * this method checks if a given coordinate is inside this element even if it's rotated
     * the trick is instead of rotating the element back, we can just rotate the mouse coordinate
     * in the opposite way to "cancel out". then we check if the new point is inside the box using 
     * a regular check
     */
    public boolean containsPoint(double x, double y) {
        // bear with me here
        /*
         * so here we gotta calculate the element's position RELATIVE TO the anchor point
         * absolutePosition represents the top left corner of the element which is what java is looking for
         * we need to calculate these so we can check if a coordinate is inside the element or not
         */
        double anchorScreenX = absolutePosition.getX() + absoluteSize.getX() * anchorPoint.getX();
        double anchorScreenY = absolutePosition.getY() + absoluteSize.getY() * anchorPoint.getY();

        /*
        * here we move the coordinate so it's relative to the element anchor point. this is needed because to
        * "unrotate" the element, you have to rotate the coordinate AROUND the same anchor point (which we'll do eventually)
        */
        double mouseRelativeToAnchorX = x - anchorScreenX;
        double mouseRelativeToAnchorY = y - anchorScreenY;

        /*
         * here we "cancel" the element's rotation so we can pretend the rotation is 0
         * and so that the coordinate and element TECHINCALLY have the same rotation
         * having it at 0 deg just makes the math easier 
         */
        double rotationRadians = Math.toRadians(-absoluteRotation); // negative rotation to unrotate
        double rotationCos = Math.cos(rotationRadians);
        double rotationSin = Math.sin(rotationRadians);

        /*
         * this part is confusing but it's based on a formula in math to rotate a point around a given point:
         * Px, Py: original point (in this case the given coordinate)
         * Ox, Oy: origin of rotation (anchorScreenX, anchorScreenY)
         * P'x = Px - Ox: translate point so origin is (0, 0)
         * P'y = Py - Oy: same as above
         * xnew = (P'x * cos θ) - (P'y * sin θ)
         * ynew = (P'x * sin θ) + (P'y * cos θ)
         * then you add back Ox, Oy so that you move the point so its relative to screen space now
         */

        double unrotatedMouseX = mouseRelativeToAnchorX * rotationCos - mouseRelativeToAnchorY * rotationSin + anchorScreenX;
        double unrotatedMouseY = mouseRelativeToAnchorX * rotationSin + mouseRelativeToAnchorY * rotationCos + anchorScreenY;

        /*
         * now that the element is "unrotated", we can do a normal box check
         * imagine the element was rotated 45 deg or smth. because we did all the
         * calculations above, we can pretend its 0 deg and check if coordinate is 
         * within the bounds of the box, as normal
         */
        double elementLeft = absolutePosition.getX();
        double elementTop = absolutePosition.getY();
        double elementRight = elementLeft + absoluteSize.getX();
        double elementBottom = elementTop + absoluteSize.getY();

        return unrotatedMouseX >= elementLeft && unrotatedMouseX <= elementRight && unrotatedMouseY >= elementTop  && unrotatedMouseY <= elementBottom;
    }

    protected void updateAbsoluteValues() {
        // all the values that are used in this function
        double width = getWidth();
        double height = getHeight();
        double xScaleSize = size.getXScale();
        double yScaleSize = size.getYScale();
        int xOffsetSize = size.getXOffset();
        int yOffsetSize = size.getYOffset();
        double xScalePos = position.getXScale();
        double yScalePos = position.getYScale();
        int xOffsetPos = position.getXOffset();
        int yOffsetPos = position.getYOffset();

        /* 
         * so here im calculating the literal size of element by 
         * multiplying screen size (or parent size if there is one) by the scale value and then adding on the offset 
         * and then i apply aspect ratio which i explain even more below
         */
        double sizeX = (width * xScaleSize) + xOffsetSize;
        double sizeY = (height * yScaleSize) + yOffsetSize;
        if (keepAspectRatio) { // so if keep aspect ratio is on for this element
            if (aspectRatio == -1 || Double.isNaN(aspectRatio)) resetAspectRatio(); // if aspect ratio hasnt been properly been set yet then reset it

            /*
             * if this is the first time updateAbsoluteValues is running, aspectRatio is a NaN because absolutesize hasnt been properly set yet
             * in resetAspectRatio() it does absoluteSize.y divided by absoluteSize.x. that would return undefined if absoluteSize isnt properly set yet
             * so in that case we dont apply aspect ratio UNTIL updateAbsoluteValues runs at least once
             */
            if (!Double.isNaN(aspectRatio)) {
                /*
                 * remember:
                 * ratio = sizeY / sizeX
                 * sizeY = sizeX * ratio
                 * sizeX = sizeY / ratio
                 */
                double currentRatio = sizeY / sizeX;
                if (currentRatio > aspectRatio) { // if the current height is too tall, we have to bound it to its width
                    sizeY = sizeX * aspectRatio;
                } else if (currentRatio < aspectRatio) { // if the current width is too wide, we have to bound it to its height
                    sizeX = sizeY / aspectRatio;
                }
            }
        }
        absoluteSize.setX(sizeX);
        absoluteSize.setY(sizeY);

        /* 
         * here im calculating the displacement from the anchor point.
         * for example: if anchor point was 0.5,0.5 then the displacement along x and y would be half the literal size of the element
         */
        double displacementX = sizeX * anchorPoint.getX();
        double displacementY = sizeY * anchorPoint.getY();

        /* 
         * here i calculate the literal position of the element by multiplying screen size (or parent size if there is one) 
         * by the scale value and then adding on the offset and then subtracting by the displacement from the anchor point 
         */
        double posX = (width * xScalePos) + xOffsetPos - displacementX;
        double posY = (height * yScalePos) + yOffsetPos - displacementY;

        // set absolute rotation to rotation that user set
        absoluteRotation = rotation;

        if (parent != null) { // if this child has a parent...
            /* 
             * then displace the child (this element) by its parent's position 
             * so that this element's position becomes relative to its parent's and not whole screen
             * on top of that, add on the rotation of its parent so child rotates along with it
             */
            posX += parent.absolutePosition.getX();
            posY += parent.absolutePosition.getY();
            absoluteRotation += parent.absoluteRotation;
        }

        absolutePosition.setX(posX);
        absolutePosition.setY(posY);
    }

    // draw element on screen
    public void draw(Graphics g) {
        if (!visible) return; // if this element has visible = false, then don't even bother drawing

        Graphics2D g2d = (Graphics2D)g.create(); // graphics2d is a subclass of graphics and it has more features like rotation
        updateAbsoluteValues(); // we need to update absolute values. they act as a translation so that java can use those values to display what we want

        /*
         * so here we gotta calculate the element's position RELATIVE TO the anchor point
         * absolutePosition represents the top left corner of the element which is what java is looking for
         * we need to calculate these so we can rotate element based around these values
         */

        if (backgroundTransparency > 0) {
        	double anchorScreenX = absolutePosition.getX() + absoluteSize.getX() * anchorPoint.getX();
            double anchorScreenY = absolutePosition.getY() + absoluteSize.getY() * anchorPoint.getY();
	        g2d.rotate(Math.toRadians(absoluteRotation), anchorScreenX, anchorScreenY);
	        g2d.setColor(backgroundColor);
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(backgroundTransparency, 1f)))); // setting transparency for the element
	        g2d.fillRoundRect((int)absolutePosition.getX(), (int)absolutePosition.getY(), (int)absoluteSize.getX(), (int)absoluteSize.getY(), borderRadius, borderRadius);
        }

        if (strokeTransparency > 0) {
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, strokeTransparency));
	        g2d.setColor(strokeColor);
	        g2d.setStroke(new BasicStroke(strokeThickness)); // set border thickness
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(strokeTransparency, 1f))));
	        g2d.drawRoundRect((int)absolutePosition.getX(), (int)absolutePosition.getY(), (int)absoluteSize.getX(), (int)absoluteSize.getY(), borderRadius, borderRadius);
        }
        
        if (resort) {
            children.sort((a, b) -> Integer.compare(a.zIndex, b.zIndex));
            resort = false;
        }

        // drawing children
        for (UIElement child : children) {
            child.draw(g);
        }

        g2d.dispose(); // good practice to dispose to clean up resources from the graphics object we cloned (g.create())
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // alright now lets handle clicking
    private UIElement findTopmostElement(int x, int y) {
        return findTopmostElement(x, y, false);
    }

    private UIElement findTopmostElement(int x, int y, boolean ignoreChildren) {
        // first we gotta check if any of this element's children that may be on top of our element contain the given coordinate
        boolean isRoot = rootElements.get(panel) == this;
        if (!ignoreChildren || isRoot) {
            for (int i = children.size() - 1; i >= 0; i--) {
                UIElement child = children.get(i);
                if (child.visible && child.containsPoint(x, y)) { // if child has visible = true and the coordinate is in the bounds of the child...
                    UIElement deeper = child.findTopmostElement(x, y); // then run findTopmostElement again. if there is another child in that child, this whole loop will happen again until it finally gets to the descendant it's hovering over
                    return deeper != null ? deeper : child;
                }
            }
        }

        if (containsPoint(x, y)) { // if none of our children contain our coordinate, then just check if we're hovering over this element
            return this; // if so then return this element
        }

        return null; // else return null
    }

    public void addClickListener(ClickListener listener) {
        clickListeners.add(listener);
    }

    public void removeClickListener(ClickListener listener) {
        clickListeners.remove(listener);
    }

    public void handleClick(MouseEvent e) {
        UIElement top = findTopmostElement(e.getX(), e.getY());
        if (top != null) {
            for (ClickListener listener : top.clickListeners) { // if we're indeed hovering over something then call all the clickListeners on the element
                listener.onClick(e);
            }
        }
    }
    
    public void addPressListener(PressListener listener) {
    	pressListeners.add(listener);
    }

    public void removePressListener(PressListener listener) {
        pressListeners.remove(listener);
    }

    public void handlePress(MouseEvent e) {
        UIElement top = findTopmostElement(e.getX(), e.getY());
        if (top != null) {
            pressed.add(top);
            for (PressListener listener : top.pressListeners) { // if we're indeed hovering over something then call all the clickListeners on the element
                listener.onPress(e);
            }
        }
    }
    
    public void addReleaseListener(ReleaseListener listener) {
        releaseListeners.add(listener);
    }

    public void removeReleaseListener(ReleaseListener listener) {
    	releaseListeners.remove(listener);
    }

    public void handleRelease(MouseEvent e) {
        for (UIElement element : pressed) {
            for (ReleaseListener listener : element.releaseListeners) {
                listener.onRelease(e);
            }
        }
        pressed.clear();
    }

    // hovering/exiting have basically the same logic as clicking

    public void addHoverListener(HoverListener listener) {
        hoverListeners.add(listener);
    }

    public void removeHoverListener(HoverListener listener) {
        hoverListeners.remove(listener);
    }

    public void addExitListener(ExitListener listener) {
        exitListeners.add(listener);
    }

    public void removeExitListener(ExitListener listener) {
        exitListeners.remove(listener);
    }

    /* this is for mouse hovering/exiting but only based on top most element
    public void handleMouseMovement(MouseEvent e) {
        UIElement top = findTopmostElement(e.getX(), e.getY()); // find top most element that we're hovering over
        if (top != currentlyHovering) { // if what we're NOW hovering over is not the same as previously then that means we're hovering over something else now
            if (currentlyHovering != null) { // if we were previously hovering over something
                currentlyHovering.isHovered = false; // flag it as not hovering anymore
                for (ExitListener l : currentlyHovering.exitListeners) { // call all the exit listeners on it
                    l.onExit(e);
                }
            }

            if (top != null) { // if what we're hovering now exists
                top.isHovered = true; // flag it has hovering
                for (HoverListener l : top.hoverListeners) { // call all the hover listeners on it
                    l.onHover(e);
                }
            }

            currentlyHovering = top; // update currently hovering
        }
    } */

    // this neglects topmost and detects hovering/exiting even if ur hovering on top of an element on top of it
    public void handleMouseMovement(MouseEvent e) {
        UIElement nowHovered = findTopmostElement(e.getX(), e.getY(), true);

        if (nowHovered != null && !isHovered) {
            isHovered = true;
            for (HoverListener l : hoverListeners) {
                l.onHover(e);
            }
        } else if (nowHovered == null && isHovered) {
            isHovered = false;
            for (ExitListener l : exitListeners) {
                l.onExit(e);
            }
        }

        for (UIElement child : children) {
            if (child.visible) {
                child.handleMouseMovement(e);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // yooooo animations

    protected Tween addTween(Object startValue, Object endValue, double time, String property, String propertyType, int animationStyle) {
        /*
         * startValue: initial value of property
         * endValue: goal value of property
         * time: how long it takes in seconds to finish this animation
         * property: what you're animating; for example, position, size, color...
         * propertyType: represents the class of the property we're adjusting; for example, position is Dim2, rotation is double, etc.
         * animationStyle: the easing style of the animation, refer to the static values in Tween class
         */
        long duration = (long)(time * 1000); // duration is calculated by multiplying time (in seconds) by 1000 to get milliseconds
        Tween tween = new Tween(this, startValue, endValue, duration, property, propertyType, animationStyle); // creates a new tween object
        tweens.add(tween); // we add it to arraylist of all tweens to keep track of them

        startTimer(); // then make a new "loop" or timer. we only want one timer globally. if we make a new timer for every tween then it can lag
        return tween;
    }

    protected void startTimer() {
        if (isTweening) return;
        timer = new Timer(8, e -> {
            updateAllTweens();
        }); // create timer, every 8 miliseconds we progress the animation. 8 ms is around 120 fps
        timer.start(); // start timer
        isTweening = true; 
    }

    private static void updateAllTweens() {
        boolean allTweensComplete = true; // by default this is true. we loop thru all tweens below, and if one them isn't complete, this will be set to false
        boolean allSpritesComplete = true;
        ArrayList<TweenEvent> onFinishEvents = new ArrayList<>();
        HashSet<JPanel> rootPanels = new HashSet<>();

        if (!tweens.isEmpty()) {
            for (int i = 0; i < tweens.size(); i++) {
                Tween tween = tweens.get(i);
                if (!tween.complete) { // if tween is not complete
                    // then get the new value based on current time and update the property to that value
                    Object newValue;
                    if (tween.skipTween || tween.cancelTween) { // if the user requested to cancel/skip the tween
                        tween.complete = true; // flag it as complete
                        newValue = tween.simulate(tween.skipTween ? 1 : 0); // get the new value. if skipped tween, then get it at 100% complete; if cancelled, get it at 0% (its initial value)
                        if (tween.onFinishEvent != null) {
                            onFinishEvents.add(tween.onFinishEvent); // if the user set a finish event then call that method
                        }
                        tweens.remove(tween);
                        i--;
                    } else {
                        long currentTime = System.currentTimeMillis(); // get current time
                        if (currentTime >= tween.endTime) { // if current time surpasses the expected end time
                            tween.complete = true; // flag it as complete
                            newValue = tween.simulate(1); // get the new value at 100% complete
                            if (tween.onFinishEvent != null) {
                                onFinishEvents.add(tween.onFinishEvent); // if the user set a finish event then call that method
                            }
                            tweens.remove(tween);
                            i--;
                        } else { // if the current time still hasn't passed end time
                            newValue = tween.simulate(); // then just get the new value based on alpha
                            allTweensComplete = false; // flag allcomplete as false because this tween hasn't finished yet
                        }
                    }
                    setProperty(tween, newValue); // update the element's property to newValue
                    rootPanels.add(tween.element.panel);
                }
            }

            // if all tweens are complete
            if (allTweensComplete || tweens.isEmpty()) {
                tweens.clear(); // remove all tweens in the array list
            }

            for (TweenEvent event : onFinishEvents) {
                event.run();
            }
        }

        if (!sprites.isEmpty()) {
            Iterator<UIImage> iter = sprites.iterator();
            while (iter.hasNext()) {
                UIImage i = iter.next();
                if (i.imageFillType == UIImage.SPRITE_ANIMATION && i.image != null && i.playing) {
                    allSpritesComplete = false;
                    long now = System.currentTimeMillis();
                    if (now - i.lastFrame >= (i.frameDuration * 1000)) {
                        i.lastFrame = now;
                        i.currentFrame++;
                        if (i.currentFrame >= i.totalFrames) {
                            if (i.loopAnimation) {
                                i.currentFrame = 0;
                            }
                            else {
                                i.currentFrame = i.totalFrames - 1;
                                i.playing = false;
                                iter.remove();
                            }
                        }
                    }
                }

                rootPanels.add(i.panel);
            }

            if (allSpritesComplete) sprites.clear();
        }

        if (allSpritesComplete && allTweensComplete) {
            timer.stop();
            isTweening = false;
        }

        for (JPanel root : rootPanels) {
            root.repaint();
        }
    }

    // set element's property to newValue here
    private static void setProperty(Tween tween, Object newValue) {
        UIElement e = tween.element;
        if (tween.property.equals("pos")) {
            e.position = (Dim2)newValue;
        } else if (tween.property.equals("size")) {
            e.size = (Dim2)newValue;
        } else if (tween.property.equals("bgt")) {
            e.backgroundTransparency = (float)newValue;
        } else if (tween.property.equals("stt")) {
            e.strokeTransparency = (float)newValue;
        } else if (tween.property.equals("bgc")) {
            e.backgroundColor = (Color)newValue;
        } else if (tween.property.equals("stc")) {
            e.strokeColor = (Color)newValue;
        } else if (tween.property.equals("stth")) {
            e.strokeThickness = (int)newValue;
        } else if (tween.property.equals("rot")) {
            e.rotation = (double)newValue;
        } else if (tween.property.equals("anchp")) {
            e.anchorPoint = (Vector2)newValue;
        } else if (tween.property.equals("imgt")) {
            ((UIImage)e).imageTransparency = (float)newValue;
        }
    }

    public Tween tweenPosition(Dim2 endPosition, double time) {
        return addTween(position, endPosition, time, "pos", "dim2", Tween.LINEAR);
    }

    public Tween tweenPosition(Dim2 endPosition, double time, int animationStyle) {
        return addTween(position, endPosition, time, "pos", "dim2", animationStyle);
    }

    public Tween tweenSize(Dim2 endSize, double time) {
        return addTween(size, endSize, time, "size", "dim2", Tween.LINEAR);
    }

    public Tween tweenSize(Dim2 endSize, double time, int animationStyle) {
        return addTween(size, endSize, time, "size", "dim2", animationStyle);
    }

    public Tween tweenBackgroundTransparency(float endBackgroundTransparency, double time) {
        return addTween(backgroundTransparency, endBackgroundTransparency, time, "bgt", "float", Tween.LINEAR);
    }

    public Tween tweenBackgroundTransparency(float endBackgroundTransparency, double time, int animationStyle) {
        return addTween(backgroundTransparency, endBackgroundTransparency, time, "bgt", "float", animationStyle);
    }

    public Tween tweenStrokeTransparency(float endStrokeTransparency, double time) {
        return addTween(strokeTransparency, endStrokeTransparency, time, "stt", "float", Tween.LINEAR);
    }

    public Tween tweenStrokeTransparency(float endStrokeTransparency, double time, int animationStyle) {
        return addTween(strokeTransparency, endStrokeTransparency, time, "stt", "float", animationStyle);
    }

    public Tween tweenBackgroundColor(Color endBackgroundColor, double time) {
        return addTween(backgroundColor, endBackgroundColor, time, "bgc", "color", Tween.LINEAR);
    }

    public Tween tweenBackgroundColor(Color endBackgroundColor, double time, int animationStyle) {
        return addTween(backgroundColor, endBackgroundColor, time, "bgc", "color", animationStyle);
    }

    public Tween tweenStrokeColor(Color endStrokeColor, double time) {
        return addTween(strokeColor, endStrokeColor, time, "stc", "color", Tween.LINEAR);
    }

    public Tween tweenStrokeColor(Color endStrokeColor, double time, int animationStyle) {
        return addTween(strokeColor, endStrokeColor, time, "stc", "color", animationStyle);
    }

    public Tween tweenStrokeThickness(int endStrokeThickness, double time) {
        return addTween(strokeThickness, endStrokeThickness, time, "stth", "int", Tween.LINEAR);
    }

    public Tween tweenStrokeThickness(Color endStrokeThickness, double time, int animationStyle) {
        return addTween(strokeThickness, endStrokeThickness, time, "stth", "int", animationStyle);
    }

    public Tween tweenRotation(double endRotation, double time) {
        return addTween(rotation, endRotation, time, "rot", "double", Tween.LINEAR);
    }

    public Tween tweenRotation(double endRotation, double time, int animationStyle) {
        return addTween(rotation, endRotation, time, "rot", "double", animationStyle);
    }

    public Tween tweenAnchorPoint(Vector2 endAnchorPoint, double time) {
        return addTween(anchorPoint, endAnchorPoint, time, "anchp", "vector2", Tween.LINEAR);
    }

    public Tween tweenAnchorPoint(Vector2 endAnchorPoint, double time, int animationStyle) {
        return addTween(anchorPoint, endAnchorPoint, time, "anchp", "vector2", animationStyle);
    }
}

class UIFrame extends UIElement {
    // so this is literally just UIElement
    public UIFrame(JPanel p) {
        super(p);
    }
}

class UIImage extends UIElement {
    public float imageTransparency = 1f; // if there's an image, this number will determine it's transparency. 1 -> fully visible, 0 -> invisible
    public BufferedImage image = null; // draws an image if this is not null
    protected int imageFillType = 0; // by default it's 0 which represents fill. fill will make the image take the whole size, allowing for stretching, while fit will prevent stretching by setting image size to its native dimensions
    public static int FILL_IMAGE = 0;
    public static int FIT_IMAGE = 1;
    public static int CROP_IMAGE = 2;
    public static int SPRITE_ANIMATION = 3;

    // animated images!??!?!?!
    protected int frameWidth = 0;
    protected int frameHeight = 0;
    protected int currentFrame = 0;
    protected int totalFrames = 1;
    protected double frameDuration = 0.1;
    protected boolean loopAnimation = false;
    protected boolean playing = false;
    protected long lastFrame = 0;

    public UIImage(JPanel panel) {
        super(panel);
    }

    public void setImageFillType(int fillType) {
        imageFillType = fillType % 4;
    }

    public int getImageFillType() {
        return imageFillType;
    }

    public void draw(Graphics g) {
        if (!visible) return;

        Graphics2D g2d = (Graphics2D) g.create();
        updateAbsoluteValues();

        // draw background as a rect
        
        if (backgroundTransparency > 0) {
            double anchorScreenX = absolutePosition.getX() + absoluteSize.getX() * anchorPoint.getX();
            double anchorScreenY = absolutePosition.getY() + absoluteSize.getY() * anchorPoint.getY();
            
            g2d.rotate(Math.toRadians(absoluteRotation), anchorScreenX, anchorScreenY);
            g2d.setColor(backgroundColor);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(backgroundTransparency, 1f))));
            g2d.fillRoundRect((int)absolutePosition.getX(), (int)absolutePosition.getY(), (int)absoluteSize.getX(), (int)absoluteSize.getY(), borderRadius, borderRadius);
        }


        if (strokeTransparency > 0) {
            g2d.setColor(strokeColor);
            g2d.setStroke(new BasicStroke(strokeThickness)); // set border thickness
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(strokeTransparency, 1f))));
            g2d.drawRoundRect((int)absolutePosition.getX(), (int)absolutePosition.getY(), (int)absoluteSize.getX(), (int)absoluteSize.getY(), borderRadius, borderRadius);
        }
        
        // draw image if there is one set
        if (image != null && imageTransparency > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(imageTransparency, 1f)))); // setting image transparency

            int xPos = (int)absolutePosition.getX();
            int yPos = (int)absolutePosition.getY();
            int xSize = (int)absoluteSize.getX();
            int ySize = (int)absoluteSize.getY();

            if (imageFillType == FILL_IMAGE) { // stretch to fill
                g2d.drawImage(image, xPos, yPos, xSize, ySize, null);
            } else if (imageFillType == FIT_IMAGE) {
                // fit while preserving aspect ratio of the image
                // we get its native dimensions first
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();

                /*
                 * here we calculate ratios of width and height by dividing the absoluteSize by the image native size
                 * then we find the smaller one (width or height) and that'll be the aspect ratio
                 */
                double widthRatio = (double)xSize / imgWidth;
                double heightRatio = (double)ySize / imgHeight;
                double ratio = Math.min(widthRatio, heightRatio);

                // these are now the "new" dimensions to fit the image in the box
                int drawWidth = (int)(imgWidth * ratio);
                int drawHeight = (int)(imgHeight * ratio);

                // center image inside the element
                int drawX = xPos + (xSize - drawWidth) / 2;
                int drawY = yPos + (ySize - drawHeight) / 2;

                g2d.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
            } else if (imageFillType == CROP_IMAGE) {
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();

                double widthRatio = (double) xSize / imgWidth;
                double heightRatio = (double) ySize / imgHeight;
                double ratio = Math.max(widthRatio, heightRatio); // fill, crop overflow

                int drawWidth = (int)(imgWidth * ratio);
                int drawHeight = (int)(imgHeight * ratio);

                int drawX = xPos + (xSize - drawWidth) / 2;
                int drawY = yPos + (ySize - drawHeight) / 2;

                // Draw only visible portion
                g2d.setClip(xPos, yPos, xSize, ySize); 
                g2d.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
                g2d.setClip(null); // reset clip
            } else if (imageFillType == SPRITE_ANIMATION) {
                int framesPerRow = image.getWidth() / frameWidth; // the amount of frames a row is determined by dividing the image native width by how many pixels the user specified
                
                /*
                 * below, we gotta find the top corner of the frame on the sprite
                 *  +-----+-----+-----+
                 *  |  0  |  1  |  2  |
                 *  +-----+-----+-----+
                 *  |  3  |  4  |  5  |
                 *  +-----+-----+-----+
                 *  look at that for example
                 *  imagine the current frame = 2 and framesPerRow is 3 and frameWidth is 100 and frameHeight is 50
                 *  4 % 3 * 100 = 100; this is x
                 *  4 / 3 * 50 = 50; this is y; so the 5th frame (2nd row 2nd column) would start at (100, 50)
                 */

                int startingSpriteX = (currentFrame % framesPerRow) * frameWidth;
                int startingSpriteY = (currentFrame / framesPerRow) * frameHeight;

                double widthRatio = (double)xSize / frameWidth;
                double heightRatio = (double)ySize / frameHeight;
                double ratio = Math.min(widthRatio, heightRatio);

                // scaled frame dimensions (preserve aspect ratio)
                int drawWidth = (int)(frameWidth * ratio);
                int drawHeight = (int)(frameHeight * ratio);

                // center the image in the box
                int drawX = xPos + (xSize - drawWidth) / 2;
                int drawY = yPos + (ySize - drawHeight) / 2;

                g2d.drawImage(image, drawX, drawY, drawX + drawWidth, drawY + drawHeight, startingSpriteX, startingSpriteY, startingSpriteX + frameWidth, startingSpriteY + frameHeight, null);
            }
        }

        for (UIElement child : children) {
            child.draw(g);
        }

        g2d.dispose();
    }

    public Tween tweenImageTransparency(float endImageTransparency, double time) {
        return addTween(imageTransparency, endImageTransparency, time, "imgt", "float", Tween.LINEAR);
    }

    public Tween tweenImageTransparency(float endImageTransparency, double time, int animationStyle) {
        return addTween(imageTransparency, endImageTransparency, time, "imgt", "float", animationStyle);
    }

    public void setSpriteSheet(int frameWidth, int frameHeight) {
        if (image == null) return;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.totalFrames = (image.getWidth() / frameWidth) * (image.getHeight() / frameHeight);
        this.currentFrame = 0;
    }

    public void playSpriteAnimation(double frameDuration, boolean loop) {
        this.frameDuration = frameDuration;
        loopAnimation = loop;
        playing = true;
        lastFrame = System.currentTimeMillis();
        currentFrame = 0;
        sprites.add(this);
        startTimer();
    }

    public void stopSpriteAnimation() {
        playing = false;
        sprites.remove(this);
    }
}
