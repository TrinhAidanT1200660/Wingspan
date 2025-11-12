import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
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

    public Vector2() {
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Vector2 center() {
        x = 0.5;
        y = 0.5;
        return this;
    }
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

    public Dim2() {
    }

    public Dim2(double xScale, int xOffset, double yScale, int yOffset) {
        this.xScale = xScale;
        this.xOffset = xOffset;
        this.yScale = yScale;
        this.yOffset = yOffset;
    }

    public double getXScale() {
        return xScale;
    }

    public int getXOffset() {
        return xOffset;
    }

    public double getYScale() {
        return yScale;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setXScale(double xScale) {
        this.xScale = xScale;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setYScale(double yScale) {
        this.yScale = yScale;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public String toString() {
        return "{(" + xScale + ", " + xOffset + "), (" + yScale + ", " + yOffset + ")}";
    }

    // ok so basically what this method does is it returns a new Dim2 that starts from Dim2 and finshes a fraction of the way to Dim2 goal, determined by a (alpha). just used for animations/tweening
    public static Dim2 lerp(Dim2 start, Dim2 goal, double a) {
        double xScale = start.xScale + (goal.xScale - start.xScale) * a;
        double xOffset = start.xOffset + (goal.xOffset - start.xOffset) * a;
        double yScale = start.yScale + (goal.yScale - start.yScale) * a;
        double yOffset = start.yOffset + (goal.yOffset - start.yOffset) * a;
        return new Dim2(xScale, (int) xOffset, yScale, (int) yOffset);
    }

    public Dim2 full() {
        xScale = 1;
        xOffset = 0;
        yScale = 1;
        yOffset = 0;
        return this;
    }

    public Dim2 reset() {
        xScale = 0;
        xOffset = 0;
        yScale = 0;
        yOffset = 0;
        return this;
    }

    public Dim2 topLeft() {
        return reset();
    }

    public Dim2 bottomRight() {
        return full();
    }

    public Dim2 center() {
        xScale = 0.5;
        xOffset = 0;
        yScale = 0.5;
        yOffset = 0;
        return this;
    }

    public Dim2 dilate(double factor) {
        xScale *= factor;
        xOffset *= factor;
        yScale *= factor;
        yOffset *= factor;
        return this;
    }

    public Dim2 clone() {
        return new Dim2(xScale, xOffset, yScale, yOffset);
    }
}

class Dim {

    /*
     * this is just Dim2 but without x and y just scale and offset
     */
    private double scale = 0;
    private int offset = 0;

    public Dim() {
    }

    public Dim(double scale, int offset) {
        this.scale = scale;
        this.offset = offset;
    }

    public double getScale() {
        return scale;
    }

    public int getOffset() {
        return offset;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String toString() {
        return "(" + scale + ", " + offset + ")";
    }

    // ok so basically what this method does is it returns a new Dim that starts from Dim and finshes a fraction of the way to Dim goal, determined by a (alpha). just used for animations/tweening
    public static Dim lerp(Dim start, Dim goal, double a) {
        double scale = start.scale + (goal.scale - start.scale) * a;
        double offset = start.offset + (goal.offset - start.offset) * a;
        return new Dim(scale, (int) offset);
    }
}

interface ClickListener {

    // used to set custom click events
    void onClick(RootMouseEvent e);
}

interface PressListener {

    // used to set custom events when they press down
    void onPress(RootMouseEvent e);
}

interface ReleaseListener {

    // used to set custom events when they release
    void onRelease(RootMouseEvent e);
}

interface HoverListener {

    // used to set custom hover events
    void onHover(RootMouseEvent e);
}

interface ExitListener {

    // used to set custom exit events
    void onExit(RootMouseEvent e);
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
    public static int EXPONENTIAL_IN_OUT = 9;

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
        return (double) timePassed / duration;
    }

    public Object simulate() {
        return simulate(getAlpha());
    }

    // this is to calculate the current spot in the animation using alpha
    public Object simulate(double alpha) {
        switch (propertyType.toLowerCase()) {
            case "int":
                return tweenInt((int) startValue, (int) endValue, alpha);
            case "double":
                return tweenDouble((double) startValue, (double) endValue, alpha);
            case "float":
                return tweenFloat((float) startValue, (float) endValue, alpha);
            case "dim2":
                return tweenDim2((Dim2) startValue, (Dim2) endValue, alpha);
            case "dim":
                return tweenDim((Dim) startValue, (Dim) endValue, alpha);
            case "vector2":
                return tweenVector2((Vector2) startValue, (Vector2) endValue, alpha);
            case "color":
                return tweenColor((Color) startValue, (Color) endValue, alpha);
            default:
                throw new IllegalArgumentException(propertyType + " isn't a valid tweenable property type!");
        }
    }

    // methods below determine value in animation given alpha
    private int tweenInt(int startValue, int endValue, double alpha) {
        return (int) tweenDouble(startValue, endValue, alpha);
    }

    private double tweenDouble(double startValue, double endValue, double alpha) {
        return startValue + (endValue - startValue) * handleAnimationAlpha(alpha, animationType);
    }

    private float tweenFloat(float startValue, float endValue, double alpha) {
        return Math.min(1.0f, Math.max((float) (startValue + (endValue - startValue) * handleAnimationAlpha(alpha, animationType)), 0.0f));
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

    private Dim tweenDim(Dim startValue, Dim endValue, double alpha) {
        return Dim.lerp(startValue, endValue, handleAnimationAlpha(alpha, animationType));
    }

    private Color tweenColor(Color startValue, Color endValue, double alpha) {
        alpha = Math.min(1.0, Math.max(0, handleAnimationAlpha(alpha, animationType)));
        int r = (int) (startValue.getRed() + (endValue.getRed() - startValue.getRed()) * alpha);
        int g = (int) (startValue.getGreen() + (endValue.getGreen() - startValue.getGreen()) * alpha);
        int b = (int) (startValue.getBlue() + (endValue.getBlue() - startValue.getBlue()) * alpha);
        int a = (int) (startValue.getAlpha() + (endValue.getAlpha() - startValue.getAlpha()) * alpha);
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

    private double exponentialInOut(double a) {
        return a == 0 ? 0 : a == 1 ? 1 : a < 0.5 ? Math.pow(2, 20 * a - 10) / 2 : (2 - Math.pow(2, -20 * a + 10)) / 2;
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
        } else if (animationType == 9) {
            a = exponentialInOut(a);
        }
        return a;
    }
}

// only reason for this class is in the case that the user rotates the root element so like u want to get the mouse data relative to that ykwim
class RootMouseEvent {

    private MouseEvent event;
    private UIElement element;
    private int x = 0;
    private int y = 0;

    public RootMouseEvent(UIElement element, MouseEvent event) {
        this.event = event;
        this.element = element;

        if (element != null) {
            JPanel panel = element.panel;
            /*
            * so here we gotta calculate the element's position RELATIVE TO the anchor point
            * absolutePosition represents the top left corner of the element which is what java is looking for
            * we need to calculate these so we can check if a coordinate is inside the element or not
             */

            UIElement root = UIElement.getRootForPanel(panel);

            double anchorScreenX = root.absolutePosition.getX() + root.absoluteSize.getX() * root.anchorPoint.getX();
            double anchorScreenY = root.absolutePosition.getY() + root.absoluteSize.getY() * root.anchorPoint.getY();

            /*
            * here we move the coordinate so it's relative to the element anchor point. this is needed because to
            * "unrotate" the element, you have to rotate the coordinate AROUND the same anchor point (which we'll do eventually)
             */
            double mouseRelativeToAnchorX = event.getX() - anchorScreenX;
            double mouseRelativeToAnchorY = event.getY() - anchorScreenY;

            /*
            * here we "cancel" the element's rotation so we can pretend the rotation is 0
            * and so that the coordinate and element TECHINCALLY have the same rotation
            * having it at 0 deg just makes the math easier 
             */
            double rotationRadians = Math.toRadians(-root.absoluteRotation); // negative rotation to unrotate
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

            /* int panelWidth = panel.getWidth();
            int panelHeight = panel.getHeight();
            double ratioX = (double)root.absoluteSize.getX() / panelWidth;
            double ratioY = (double)root.absoluteSize.getY() / panelHeight;

            this.x = (int)(unrotatedMouseX * ratioX);
            this.y = (int)(unrotatedMouseY * ratioY); */
            this.x = (int) (unrotatedMouseX - root.absolutePosition.getX());
            this.y = (int) (unrotatedMouseY - root.absolutePosition.getY());
        } else {
            this.x = event.getX();
            this.y = event.getY();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public MouseEvent getEvent() {
        return event;
    }

    public UIElement getElement() {
        return element;
    }
}

class ListLayout {
    // direction options or like which way to stack the items
    public static int VERTICAL = 0;
    public static int HORIZONTAL = 1;

    // alignment options for vertical / horizontal alignment
    public static int TOP = 2;
    public static int MIDDLE = 3;
    public static int CENTER = 3; // middle center same thing
    public static int BOTTOM = 4;
    public static int LEFT = 5;
    public static int RIGHT = 6;

    public int direction = VERTICAL; // whether items stack vertically or horizontally
    public int verticalAlignment = TOP; // how to align vertically (top/middle/bottom)
    public int horizontalAlignment = LEFT; // how to align horizontally (left/center/right)
    public Dim padding = new Dim(); // padding is like the margin of the actual content area so if u have a 200x200 area and like padding is 10 then the actual content area would be 180x180
    public Dim spacing = new Dim(); // spacing is like the space between each child
    public boolean dirty = true; // used to know if we need to resort children; only true when new child is added

    public void applyLayout(UIElement parent) {
        ArrayList<UIElement> children = parent.getChildren();

        if (dirty) {
            children.sort((a, b) -> Integer.compare(a.layoutOrder, b.layoutOrder));
            dirty = false;
        }
        
        double absolutePadding = (direction == VERTICAL) ? padding.getScale() * parent.absoluteSize.getY() + padding.getOffset() : padding.getScale() * parent.absoluteSize.getX() + padding.getOffset();
        double absoluteSpacing = (direction == VERTICAL) ? spacing.getScale() * parent.absoluteSize.getY() + spacing.getOffset() : spacing.getScale() * parent.absoluteSize.getX() + spacing.getOffset();

        // start positions at padding (cus like if theres padding u gotta move the content area from the left a little bit)
        double x = absolutePadding;
        double y = absolutePadding;

        // this will store total space used by all children and spacing, depends on direction
        double absoluteContentSize = 0;

        for (UIElement child : children) {
            if (!child.visible) continue;

            // get the size of the child depending on layout direction
            double size = (direction == VERTICAL) ? child.absoluteSize.getY() : child.absoluteSize.getX();

            absoluteContentSize += size + absoluteSpacing; // add it to the total and stuff
        }

        // remove last spacing cus we don't want that extra gap at the end yk
        absoluteContentSize -= absoluteSpacing;

        // so we gotta find the actual size we can use, which is the bounds of the parent - padding (on both left and right or top and bottom)
        double availableWidth = parent.absoluteSize.getX() - absolutePadding * 2;
        double availableHeight = parent.absoluteSize.getY() - absolutePadding * 2;

        // alignment for the direction we chose
        double offset = 0;
        if (direction == VERTICAL) {
            if (verticalAlignment == MIDDLE || verticalAlignment == CENTER) offset = (availableHeight - absoluteContentSize) / 2; else if (verticalAlignment == BOTTOM) offset = (availableHeight - absoluteContentSize);
            y += offset;
        } else {
            if (horizontalAlignment == CENTER || horizontalAlignment == MIDDLE) offset = (availableWidth - absoluteContentSize) / 2; else if (horizontalAlignment == RIGHT) offset = (availableWidth - absoluteContentSize);
            x += offset;
        }

        // ok now we gota update the positioning of the children to like fit the stacking of the list and stuff
        for (UIElement child : children) {
            if (!child.visible) continue;

            double childX = x;
            double childY = y;

            // handle the alignment that's opposite to the current one (perperendiuclaar)
            if (direction == VERTICAL) {
                if (horizontalAlignment == CENTER || horizontalAlignment == MIDDLE) childX = (availableWidth - child.absoluteSize.getX()) / 2 + absolutePadding; else if (horizontalAlignment == RIGHT) childX = availableWidth - child.absoluteSize.getX() + absolutePadding;
            } else {
                if (verticalAlignment == MIDDLE || verticalAlignment == CENTER) childY = (availableHeight - child.absoluteSize.getY()) / 2 + absolutePadding; else if (verticalAlignment == BOTTOM) childY = availableHeight - child.absoluteSize.getY() + absolutePadding;
            }

            child.position = new Dim2(0, (int)childX, 0, (int)childY);

            if (direction == VERTICAL) y += child.absoluteSize.getY() + absoluteSpacing; else x += child.absoluteSize.getX() + absoluteSpacing;
        }
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
    protected int absoluteBorderRadius = 0;
    protected int absoluteStrokeThickness = 0;
    public Vector2 anchorPoint = new Vector2(); // controls where to "anchor" the ui element. 0,0 would be top left corner of the element; 0.5,0.5 would be middle; 1,1 would be bottom right corner
    public Dim2 position = new Dim2(); // position using scale and offset
    public Dim2 size = new Dim2(0, 100, 0, 100); // size using scale and offset; default size 100x100 pixels
    public double rotation = 0; // rotation in degrees
    public Color backgroundColor = Color.WHITE; // background color of frame.
    public float backgroundTransparency = 1f; // this number will determine the element's background's transparency. 1 -> fully visible, 0 -> invisible
    public Dim borderRadius = new Dim(); // rounded corners
    public Dim strokeThickness = new Dim();
    public float strokeTransparency = 0.0f;
    public Color strokeColor = Color.BLUE;
    private int zIndex = 0; // represents the layer the element is on. keep in mind that zIndex is relative, meaning all children will inherit its parent's zIndex
    public boolean visible = true;
    public boolean keepAspectRatio = false;
    public boolean cropOverflow = false;
    private double aspectRatio = -1;
    private UIElement parent; // the element that this element is inside of (if there is one)
    private ArrayList<UIElement> children = new ArrayList<>(); // all the elements that are inside this one
    private ArrayList<ClickListener> clickListeners = new ArrayList<>(); // used for click events
    private ArrayList<PressListener> pressListeners = new ArrayList<>(); // used for press down events
    private ArrayList<ReleaseListener> releaseListeners = new ArrayList<>(); // used for release events
    private ArrayList<HoverListener> hoverListeners = new ArrayList<>(); // used for hover events
    private ArrayList<ExitListener> exitListeners = new ArrayList<>(); // used for hover exit events
    private UIElement pressed;
    private static UIElement previouslyHovered = null;
    protected JPanel panel;
    private boolean resort = false; // used whenever we need to resort the drawing order based on z-index
    protected AffineTransform mostRecentTransform; // used to check if mouse is inside an element
    private HashMap<String, Object> attributes = new HashMap<>();
    private static HashMap<String, UIElement> byName = new HashMap<>();
    private String name;
    public boolean ignore = false;
    public ListLayout layout;
    public int layoutOrder;

    // animation related stuff
    protected static HashSet<UIImage> sprites = new HashSet<>();
    private static ArrayList<Tween> tweens = new ArrayList<>();
    private static Timer timer;
    private static boolean isTweening = false;

    public UIElement(String name, JPanel panel) {
        UIElement old = byName.put(name, this);
        if (old != null) {
            throw new IllegalArgumentException("The name \"" + name + "\" has already been used.");
        }
        this.panel = panel;
        this.name = name;
        // this is making the root element in case there isn't one
        UIElement root = getRootForPanel(panel);
        if (this != root) {
            root.addChild(this);
        }
    }

    // used for making roots
    private UIElement(String name, JPanel panel, boolean isRoot) {
        this.name = name;
        if (isRoot) {
            this.panel = panel;
        }
    }

    // get the root element for a jpanel; acts like the whole container for all the UIElements basically
    public static UIElement getRootForPanel(JPanel p) {
        UIElement root = rootElements.get(p);
        if (root == null) {
            root = new UIElement("Root" + p, p, true);
            root.size = new Dim2(1, 0, 1, 0);
            root.position = new Dim2(0, 0, 0, 0);
            root.anchorPoint = new Vector2(0, 0);
            root.backgroundTransparency = 0f;
            rootElements.put(p, root);
        }
        return root;
    }

    public void setZIndex(int zIndex) {
        resort = true;
        this.zIndex = zIndex;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setName(String name) {
        byName.remove(this.name);
        this.name = name;
        byName.put(name, this);
    }

    public String getName() {
        return name;
    }

    public static UIElement getByName(String name) {
        return byName.get(name);
    }

    // used to add children to element
    public void addChild(UIElement child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
            child.parent.resort = true;
            if (child.parent.layout != null) child.parent.layout.dirty = true;
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
        if (parent != null) {
            return parent.absoluteSize.getY();
        }
        return panel.getHeight();
    }

    // returns either current width of panel or width of its parent if it has one. used for scale values
    private double getWidth() {
        if (parent != null) {
            return parent.absoluteSize.getX();
        }
        return panel.getWidth();
    }

    /*
     * this method checks if a given coordinate is inside this element even if it's rotated
     * the trick is instead of rotating the element back, we can just rotate the mouse coordinate
     * in the opposite way to "cancel out". then we check if the new point is inside the box using 
     * a regular check
     * 
     * sigh this used to work but its not accurate after some testing
     */
 /* public boolean containsPoint(double x, double y) {
        // bear with me here
        /*
         * so here we gotta calculate the element's position RELATIVE TO the anchor point
         * absolutePosition represents the top left corner of the element which is what java is looking for
         * we need to calculate these so we can check if a coordinate is inside the element or not

        double anchorScreenX = absolutePosition.getX() + absoluteSize.getX() * anchorPoint.getX();
        double anchorScreenY = absolutePosition.getY() + absoluteSize.getY() * anchorPoint.getY();

        /*
        * here we move the coordinate so it's relative to the element anchor point. this is needed because to
        * "unrotate" the element, you have to rotate the coordinate AROUND the same anchor point (which we'll do eventually)
        
        double mouseRelativeToAnchorX = x - anchorScreenX;
        double mouseRelativeToAnchorY = y - anchorScreenY;

        /*
         * here we "cancel" the element's rotation so we can pretend the rotation is 0
         * and so that the coordinate and element TECHINCALLY have the same rotation
         * having it at 0 deg just makes the math easier 
         
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
         

        double unrotatedMouseX = mouseRelativeToAnchorX * rotationCos - mouseRelativeToAnchorY * rotationSin + anchorScreenX;
        double unrotatedMouseY = mouseRelativeToAnchorX * rotationSin + mouseRelativeToAnchorY * rotationCos + anchorScreenY;

        /*
         * now that the element is "unrotated", we can do a normal box check
         * imagine the element was rotated 45 deg or smth. because we did all the
         * calculations above, we can pretend its 0 deg and check if coordinate is 
         * within the bounds of the box, as normal
         
        double elementLeft = absolutePosition.getX();
        double elementTop = absolutePosition.getY();
        double elementRight = elementLeft + absoluteSize.getX();
        double elementBottom = elementTop + absoluteSize.getY();

        return unrotatedMouseX >= elementLeft && unrotatedMouseX <= elementRight && unrotatedMouseY >= elementTop  && unrotatedMouseY <= elementBottom;
    } */
    public boolean containsPoint(double x, double y) {
        // the only case where mostRecentTransform would be null is when its drawing for the first time
        if (mostRecentTransform == null) {
            return false;
        }

        try {
            /* 
             * alr if u refer to my old containsPoint method above , i explain why we transform. to briefly put it,
             * we wanna check if a certain coordinate is inside a rotated polygon; all u have to do is rotate
             * the coordinate in the opposite direction of the rotated polygon in order to "nullify" or cancel 
             * the rotation of the polygon and then badabing badaboom dude
             */
            // why does this need a try catch block bro
            AffineTransform inverse = mostRecentTransform.createInverse();
            Point2D p = inverse.transform(new Point2D.Double(x, y), null);

            /*
             * NOW we can do a normal box check cus we cancelled out the rotation
             */
            double elementLeft = absolutePosition.getX();
            double elementTop = absolutePosition.getY();
            double elementRight = elementLeft + absoluteSize.getX();
            double elementBottom = elementTop + absoluteSize.getY();

            return p.getX() >= elementLeft && p.getX() <= elementRight && p.getY() >= elementTop && p.getY() <= elementBottom;

        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
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
            if (aspectRatio == -1 || Double.isNaN(aspectRatio)) {
                resetAspectRatio(); // if aspect ratio hasnt been properly been set yet then reset it
            }
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

        absoluteBorderRadius = (int) (borderRadius.getScale() * Math.min(width, height) + borderRadius.getOffset());
        absoluteStrokeThickness = (int) (strokeThickness.getScale() * Math.min(width, height) + strokeThickness.getOffset());

        customUpdateAbsolute(width, height);
    }

    protected void drawCustom(Graphics2D g2d) {
        // this is meant for my subclasses
    }

    protected void customUpdateAbsolute(double width, double height) {
        // this is meant for my subclasses
    }

    // draw element on screen
    public void draw(Graphics g) {
        if (!visible) {
            return; // if this element has visible = false, then don't even bother drawing
        }
        Graphics2D g2d = (Graphics2D) g.create(); // graphics2d is a subclass of graphics and it has more features like rotation

        updateAbsoluteValues(); // we need to update absolute values. they act as a translation so that java can use those values to display what we want

        /*
         * so here we gotta calculate the element's position RELATIVE TO the anchor point
         * absolutePosition represents the top left corner of the element which is what java is looking for
         * we need to calculate these so we can rotate element based around these values
         */
        double anchorScreenX = absolutePosition.getX() + absoluteSize.getX() * anchorPoint.getX();
        double anchorScreenY = absolutePosition.getY() + absoluteSize.getY() * anchorPoint.getY();

        // so AffineTransform is what handles like all the transofmrations like rotation and translation 
        AffineTransform transform = g2d.getTransform();
        /*
         * so remember, children inherit their parents properties
         * so i want to ADD ON the transformations of this element TO the previous element's transformations (its parent)
         * its parents transofmrations are given to us because we pass the graphics object of the parent so the child can use it
         * 
         * ok now the reason i want to store the transform because for the life of me i cannot find out how to 
         * do the proper calculations for checking if the mouse is over a UIElement; it used to work right but
         * the thing is i realized that some children were rotating as if the origin was themselves which i realized was wrong;
         * it should be rotating around the parents origin and i just found that doing it this way is easier
         */
        transform.rotate(Math.toRadians(rotation), anchorScreenX, anchorScreenY);
        g2d.setTransform(transform);
        mostRecentTransform = new AffineTransform(transform);

        if (cropOverflow) {
            g2d.setClip((int) absolutePosition.getX(), (int) absolutePosition.getY(), (int) absoluteSize.getX(), (int) absoluteSize.getY());
        }
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (backgroundTransparency > 0) {
            g2d.setColor(backgroundColor); 
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(backgroundTransparency, 1f)))); // setting transparency for the element
            g2d.fillRoundRect((int) absolutePosition.getX(), (int) absolutePosition.getY(), (int) absoluteSize.getX(), (int) absoluteSize.getY(), absoluteBorderRadius, absoluteBorderRadius);
        }

        if (strokeTransparency > 0) {
            g2d.setColor(strokeColor);
            g2d.setStroke(new BasicStroke(absoluteStrokeThickness)); // set border thickness
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(strokeTransparency, 1f))));
            g2d.drawRoundRect((int) absolutePosition.getX(), (int) absolutePosition.getY(), (int) absoluteSize.getX(), (int) absoluteSize.getY(), absoluteBorderRadius, absoluteBorderRadius);
        }

        if (resort) {
            children.sort((a, b) -> Integer.compare(a.zIndex, b.zIndex));
            resort = false;
        }

        drawCustom(g2d);

        if (layout != null) {
            layout.applyLayout(this);
        }

        for (UIElement child : children) {
            child.draw(g2d);
        }

        // drawing children
        g2d.dispose(); // good practice to dispose to clean up resources from the graphics object we cloned (g.create())
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // alright now lets handle clicking
    private UIElement findTopmostElement(int x, int y) {
        UIElement root = getRootForPanel(panel);
        if (!root.containsPoint(x, y)) {
            return null;
        }
        // first we gotta check if any of this element's children that may be on top of our element contain the given coordinate
        for (int i = children.size() - 1; i >= 0; i--) {
            UIElement child = children.get(i);
            //System.out.println(child.zIndex);
            if (child.visible && !child.ignore) { // if child has visible = true (we dont check if the mouse is in the bounds of the child because what if a child of the child is not in the bounds of the child we still wanna check that)
                UIElement deeper = child.findTopmostElement(x, y); // then run findTopmostElement again. if there is another child in that child, this whole loop will happen again until it finally gets to the descendant it's hovering over
                if (deeper != null) {
                    return deeper;
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

    public RootMouseEvent handleClick(MouseEvent e) {
        UIElement top = findTopmostElement(e.getX(), e.getY());
        if (top != null) {
            RootMouseEvent event = new RootMouseEvent(top, e);
            for (ClickListener listener : top.clickListeners) { // if we're indeed hovering over something then call all the clickListeners on the element
                listener.onClick(event);
            }
            return event;
        }
        return new RootMouseEvent(null, e);
    }

    public void addPressListener(PressListener listener) {
        pressListeners.add(listener);
    }

    public void removePressListener(PressListener listener) {
        pressListeners.remove(listener);
    }

    public RootMouseEvent handlePress(MouseEvent e) {
        UIElement top = findTopmostElement(e.getX(), e.getY());
        if (top != null) {
            RootMouseEvent event = new RootMouseEvent(top, e);
            for (PressListener listener : top.pressListeners) { // if we're indeed hovering over something then call all the clickListeners on the element
                listener.onPress(event);
            }
            pressed = top;
            return event;
        }
        return new RootMouseEvent(null, e);
    }

    public void addReleaseListener(ReleaseListener listener) {
        releaseListeners.add(listener);
    }

    public void removeReleaseListener(ReleaseListener listener) {
        releaseListeners.remove(listener);
    }

    public RootMouseEvent handleRelease(MouseEvent e) {
        if (pressed == null) {
            return new RootMouseEvent(null, e);
        }
        RootMouseEvent event = new RootMouseEvent(pressed, e);
        for (ReleaseListener listener : pressed.releaseListeners) {
            listener.onRelease(event);
        }
        pressed = null;
        return event;
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
    public RootMouseEvent[] handleMouseMovement(MouseEvent e) {
        UIElement nowHovered = findTopmostElement(e.getX(), e.getY());

        RootMouseEvent[] returning = new RootMouseEvent[2];
        RootMouseEvent eventPrev = new RootMouseEvent(previouslyHovered, e);
        returning[1] = eventPrev;
        RootMouseEvent eventNow = new RootMouseEvent(nowHovered, e);
        returning[0] = eventNow;

        if (nowHovered != previouslyHovered && previouslyHovered != null) {
            for (ExitListener l : previouslyHovered.exitListeners) {
                l.onExit(eventPrev);
            }
        }
        if (nowHovered != previouslyHovered && nowHovered != null) {
            for (HoverListener l : nowHovered.hoverListeners) {
                l.onHover(eventNow);
            }
        }

        previouslyHovered = nowHovered;

        return returning;
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
        long duration = (long) (time * 1000); // duration is calculated by multiplying time (in seconds) by 1000 to get milliseconds
        Tween tween = new Tween(this, startValue, endValue, duration, property, propertyType, animationStyle); // creates a new tween object
        tweens.add(tween); // we add it to arraylist of all tweens to keep track of them

        startTimer(); // then make a new "loop" or timer. we only want one timer globally. if we make a new timer for every tween then it can lag
        return tween;
    }

    protected void startTimer() {
        if (isTweening) {
            return;
        }
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
                            } else {
                                i.currentFrame = i.totalFrames - 1;
                                i.playing = false;
                                iter.remove();
                            }
                        }
                    }
                }

                rootPanels.add(i.panel);
            }

            if (allSpritesComplete) {
                sprites.clear();
            }
        }

        if (allSpritesComplete && allTweensComplete) {
            timer.stop();
            isTweening = false;
        }

        for (TweenEvent event : onFinishEvents) {
            event.run();
        }

        for (JPanel root : rootPanels) {
            root.repaint();
        }
    }

    // set element's property to newValue here
    private static void setProperty(Tween tween, Object newValue) {
        UIElement e = tween.element;
        if (tween.property.equals("pos")) {
            e.position = (Dim2) newValue;
        } else if (tween.property.equals("size")) {
            e.size = (Dim2) newValue;
        } else if (tween.property.equals("bgt")) {
            e.backgroundTransparency = (float) newValue;
        } else if (tween.property.equals("stt")) {
            e.strokeTransparency = (float) newValue;
        } else if (tween.property.equals("bgc")) {
            e.backgroundColor = (Color) newValue;
        } else if (tween.property.equals("stc")) {
            e.strokeColor = (Color) newValue;
        } else if (tween.property.equals("stth")) {
            e.strokeThickness = (Dim) newValue;
        } else if (tween.property.equals("bdr")) {
            e.borderRadius = (Dim) newValue;
        } else if (tween.property.equals("rot")) {
            e.rotation = (double) newValue;
        } else if (tween.property.equals("anchp")) {
            e.anchorPoint = (Vector2) newValue;
        } else if (tween.property.equals("imgt")) {
            ((UIImage) e).imageTransparency = (float) newValue;
        } else if (tween.property.equals("txtt")) {
            ((UIText) e).textTransparency = (float) newValue;
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

    public Tween tweenStrokeThickness(Dim endStrokeThickness, double time) {
        return addTween(strokeThickness, endStrokeThickness, time, "stth", "dim", Tween.LINEAR);
    }

    public Tween tweenStrokeThickness(Dim endStrokeThickness, double time, int animationStyle) {
        return addTween(strokeThickness, endStrokeThickness, time, "stth", "dim", animationStyle);
    }

    public Tween tweenBorderRadius(Dim endBorderRadius, double time) {
        return addTween(borderRadius, endBorderRadius, time, "bdr", "dim", Tween.LINEAR);
    }

    public Tween tweenBorderRadius(Dim endBorderRadius, double time, int animationStyle) {
        return addTween(borderRadius, endBorderRadius, time, "bdr", "dim", animationStyle);
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

    //////////////// attributes
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public Object setAttribute(String key, Object value) {
        return attributes.put(key, value);
    }

    public void destroy() {
        if (parent != null) {
            parent.children.remove(this);
            parent = null;
        }
        for (UIElement child : children) {
            child.destroy();
        }
        children.clear();
        attributes.clear();
        byName.remove(name);
    }
}

class UIFrame extends UIElement {

    // so this is literally just UIElement
    public UIFrame(String name, JPanel p) {
        super(name, p);
    }
}

class UIImage extends UIElement {

    public float imageTransparency = 1f; // if there's an image, this number will determine it's transparency. 1 -> fully visible, 0 -> invisible
    private String imagePath = ""; // draws an image if this is not null
    public BufferedImage image = null;
    private BufferedImage toDraw = null;
    private float brightness = 1f;
    private boolean dirtyBrightness = false;
    private boolean dirtyImagePath = false;
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

    public UIImage(String name, JPanel panel) {
        super(name, panel);
    }

    public void setImageFillType(int fillType) {
        imageFillType = fillType % 4;
    }

    public int getImageFillType() {
        return imageFillType;
    }

    private BufferedImage updateBrightness() {
        if (image == null) return null;
        try {
            RescaleOp op = new RescaleOp(brightness, 0, null);
            BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            op.filter(image, out);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return image;
        }
    }

    public void setBrightness(float newBrightness) {
        if (brightness != newBrightness) {
            brightness = newBrightness;
            dirtyBrightness = true;
        }
    }

    public float getBrightness() {
        return brightness;
    }

    public void setImagePath(String newImagePath) {
        if (!imagePath.equals(newImagePath)) {
            imagePath = newImagePath;
            dirtyImagePath = true;
            if (brightness != 1f) dirtyBrightness = true;
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public BufferedImage handleDirtyPath() {
        if (dirtyImagePath) {
            dirtyImagePath = false;
            image = ImageHandler.get(imagePath);
            toDraw = image;
        }
        return image;
    }

    protected void drawCustom(Graphics2D g2d) {
        // draw image if there is one set
        handleDirtyPath();
        if (dirtyBrightness) {
            dirtyBrightness = false;
            toDraw = updateBrightness();
        }
        if (toDraw != null && imageTransparency > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(imageTransparency, 1f)))); // setting image transparency
            int xPos = (int) absolutePosition.getX();
            int yPos = (int) absolutePosition.getY();
            int xSize = (int) absoluteSize.getX();
            int ySize = (int) absoluteSize.getY();

            if (imageFillType == FILL_IMAGE) { // stretch to fill
                g2d.drawImage(toDraw, xPos, yPos, xSize, ySize, null);
            } else if (imageFillType == FIT_IMAGE) {
                // fit while preserving aspect ratio of the image
                // we get its native dimensions first
                int imgWidth = toDraw.getWidth();
                int imgHeight = toDraw.getHeight();

                /*
                 * here we calculate ratios of width and height by dividing the absoluteSize by the image native size
                 * then we find the smaller one (width or height) and that'll be the aspect ratio
                 */
                double widthRatio = (double) xSize / imgWidth;
                double heightRatio = (double) ySize / imgHeight;
                double ratio = Math.min(widthRatio, heightRatio);

                // these are now the "new" dimensions to fit the image in the box
                int drawWidth = (int) (imgWidth * ratio);
                int drawHeight = (int) (imgHeight * ratio);

                // center image inside the element
                int drawX = xPos + (xSize - drawWidth) / 2;
                int drawY = yPos + (ySize - drawHeight) / 2;

                g2d.drawImage(toDraw, drawX, drawY, drawWidth, drawHeight, null);
            } else if (imageFillType == CROP_IMAGE) {
                int imgWidth = toDraw.getWidth();
                int imgHeight = toDraw.getHeight();

                double widthRatio = (double) xSize / imgWidth;
                double heightRatio = (double) ySize / imgHeight;
                double ratio = Math.max(widthRatio, heightRatio); // fill, crop overflow

                // so this calculates the size as big as it can be with the ratio. its as if we asked if we scaled the image to fill the box, how big would it become?
                int drawWidth = (int) (imgWidth * ratio);
                int drawHeight = (int) (imgHeight * ratio);

                /*
                 * imagine the rect is 200 pixels wide, and the scaled image is 300 pixels wide; the image is 100 pixels too big
                 * we want to cut off 50 on the left, 50 on the right so that its CENTERED
                 * same vertically
                 */
                int offsetX = (drawWidth - xSize) / 2;
                int offsetY = (drawHeight - ySize) / 2;

                /* 
                 * we want to know where to start drawing the image from the source; so we divide the amount we need to cut off from above 
                 * but remember, we applied ratio; so we have to divide by it. if ratio was 1, that means the picture is perfectly a square
                 * like 100x100
                 */
                int topLeftBound = (int) (offsetX / ratio);
                int topRightBound = (int) (offsetY / ratio);
                int bottomLeftBound = (int) ((offsetX + xSize) / ratio);
                int bottomRightBound = (int) ((offsetY + ySize) / ratio);

                g2d.drawImage(toDraw, xPos, yPos, xPos + xSize, yPos + ySize, topLeftBound, topRightBound, bottomLeftBound, bottomRightBound, null);
            } else if (imageFillType == SPRITE_ANIMATION) {
                int framesPerRow = toDraw.getWidth() / frameWidth; // the amount of frames a row is determined by dividing the image native width by how many pixels the user specified

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

                double widthRatio = (double) xSize / frameWidth;
                double heightRatio = (double) ySize / frameHeight;
                double ratio = Math.min(widthRatio, heightRatio);

                // scaled frame dimensions (preserve aspect ratio)
                int drawWidth = (int) (frameWidth * ratio);
                int drawHeight = (int) (frameHeight * ratio);

                // center the image in the box
                int drawX = xPos + (xSize - drawWidth) / 2;
                int drawY = yPos + (ySize - drawHeight) / 2;

                g2d.drawImage(toDraw, drawX, drawY, drawX + drawWidth, drawY + drawHeight, startingSpriteX, startingSpriteY, startingSpriteX + frameWidth, startingSpriteY + frameHeight, null);
            }
        }
    }

    public Tween tweenImageTransparency(float endImageTransparency, double time) {
        return addTween(imageTransparency, endImageTransparency, time, "imgt", "float", Tween.LINEAR);
    }

    public Tween tweenImageTransparency(float endImageTransparency, double time, int animationStyle) {
        return addTween(imageTransparency, endImageTransparency, time, "imgt", "float", animationStyle);
    }

    public void setSpriteSheet(int frameWidth, int frameHeight) {
        image = handleDirtyPath();
        if (image == null) {
            return;
        }
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

class UIText extends UIElement {

    public static String defaultFontName = "Arial";

    public String fontName = defaultFontName;
    public int fontStyle = Font.PLAIN;
    public int fontSize = 200;
    public float textTransparency = 1f;
    public Color textColor = Color.black;
    public String text = "Text";
    public float textStrokeTransparency = 0f;
    public Color textStrokeColor = Color.black;
    public Dim textStrokeThickness = new Dim();
    public int absoluteTextStrokeThickness = 0;
    public boolean textScaled = false;
    public boolean textWrapped = false;
    private boolean textDirty = true;
    private Vector2 oldAbsSize = new Vector2();

    public static int LEFT = 0;
    public static int CENTER = 1;
    public static int RIGHT = 2;

    public static int TOP = 0;
    public static int MIDDLE = 1;
    public static int BOTTOM = 2;

    public int horizontalAlignment = CENTER;
    public int verticialAlignment = MIDDLE;

    public UIText(String name, JPanel panel) {
        super(name, panel);
    }

    protected void customUpdateAbsolute(double width, double height) {
        absoluteTextStrokeThickness = (int) (textStrokeThickness.getScale() * Math.min(width, height) + textStrokeThickness.getOffset());
    }

    protected void drawCustom(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (textScaled) textWrapped = true; // you need text wrapped for text scaled so it always fits in the container

        Font font = new Font(fontName, fontStyle, fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        ArrayList<String> lines = new ArrayList<>();
        if (textWrapped) {
            lines = wrapText(g2d);
        } else {
            lines.add(text);
        }

        double width = absoluteSize.getX();
        double height = absoluteSize.getY();

        textDirty = !(oldAbsSize.getX() == absoluteSize.getX() && oldAbsSize.getY() == absoluteSize.getY());
        oldAbsSize = new Vector2(absoluteSize.getX(), absoluteSize.getY());

        // so we gotta find the maximum possible size it can be to fit in the container
        if (textScaled && !textDirty) {
            double textHeight = lines.size() * fm.getHeight();
            double maxLineWidth = 0;
            int newSize = 1;
            for (String line : lines) {
                maxLineWidth = Math.max(maxLineWidth, fm.stringWidth(line)); // get the width of the longest line
                newSize = (int)(maxLineWidth / line.length());
            }

            while ((textHeight > height || maxLineWidth > width) && newSize > 1) { // so basically while the text is too tall or too wide for the container and the font size isnt 0
                newSize--;
                font = new Font(fontName, fontStyle, newSize);
                g2d.setFont(font);
                fm = g2d.getFontMetrics();
                lines = wrapText(g2d);
                textHeight = lines.size() * fm.getHeight();
                maxLineWidth = 0;
                for (String line : lines) {
                    maxLineWidth = Math.max(maxLineWidth, fm.stringWidth(line)); // get the width of the longest line
                }
            }
            textDirty = false;
        }

        float totalTextHeight = (float)lines.size() * fm.getHeight();
        float startY = (float)absolutePosition.getY();

        if (verticialAlignment == MIDDLE) startY += (height - totalTextHeight) / 2 + fm.getAscent(); else if (verticialAlignment == BOTTOM) startY += height - totalTextHeight + fm.getAscent(); else startY += fm.getAscent();

        for (String line : lines) {
            float textWidth = fm.stringWidth(line);
            float x = (float) absolutePosition.getX();

            if (horizontalAlignment == CENTER)
                x += (width - textWidth) / 2;
            else if (horizontalAlignment == RIGHT)
                x += width - textWidth;

            if (textStrokeTransparency > 0) {
                FontRenderContext frc = g2d.getFontRenderContext();
                GlyphVector gv = g2d.getFont().createGlyphVector(frc, line);
                Shape outline = gv.getOutline(x, startY);
                g2d.setColor(textStrokeColor);
                g2d.setStroke(new BasicStroke((float) absoluteTextStrokeThickness));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(textStrokeTransparency, 1f))));
                g2d.draw(outline);
            }

            if (textTransparency > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(textTransparency, 1f))));
                g2d.setColor(textColor);
                g2d.drawString(line, x, startY);
            }

            startY += fm.getHeight();
        }
    }

    private ArrayList<String> wrapText(Graphics2D g2d) {
        ArrayList<String> lines = new ArrayList<>();
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split(" "); // splits ENTIRE text into words
        String line = ""; // temporary string thats gonna hold the current line we're building. once we're done with it and its longer than the container width, we add it to lines arraylist and start a new line

        for (String word : words) { // looping thru all the words
            String wholeLine = line.length() == 0 ? word : line + " " + word; // if line is empty, then wholeLine is just the word; else wholeLine is line + space + word
            int lineWidth = fm.stringWidth(wholeLine); // get the width of the wholeLine if we added this word to it
            if (lineWidth > absoluteSize.getX() && line.length() > 0) { // if the wholeLine is wider than the container width AND line is not empty
                lines.add(line); // then we add the current line to lines arraylist (wihtout the new word)
                line = word; // start the next line with the word that we couldnt add
            } else {
                line = wholeLine; // if it still fits then we just update line to wholeLine
            }
        }
        if (line.length() > 0) lines.add(line); // if there's any remaining text in line after the loop then add it as well
        return lines;
    }

    public Tween tweenTextTransparency(float endTextTransparency, double time) {
        return addTween(textTransparency, endTextTransparency, time, "txtt", "float", Tween.LINEAR);
    }

    public Tween tweenTextTransparency(float endTextTransparency, double time, int animationStyle) {
        return addTween(textTransparency, endTextTransparency, time, "txtt", "float", animationStyle);
    }
}
