package graphics;

import model.BNode_;
import model.BTree_;
import model.graphics.TreeItemFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Graphics class used to display the B Tree.
 * Subclasses canvas
 */
public class BTreeDisplay extends Canvas {

    /**
     * The BTreeGraphics object which wraps the B Tree to be displayed
     */
    private final BTreeGraphics treeGraphics;

    /**
     * The factory class used to create new items in the tree.
     */
    private final TreeItemFactory<? extends Comparable<?>> treeItemFactory;

    /**
     * The offset x amount for the display graphics
     */
    private double xOffset;
    /**
     * The offset y amount for the display graphics
     */
    private double yOffset;
    /**
     * The zoom scale amount for the display graphics
     */
    private double scale;

    private double prevMouseX;
    private double prevMouseY;
    private boolean isLeftPressed;

    /**
     * Constructor with factory input.
     * @param treeItemFactory the factory used to create the empty tree and items
     */
    public BTreeDisplay(TreeItemFactory<?> treeItemFactory) {
        // 3 is the default minimum number of children of a B Tree
        this(treeItemFactory, treeItemFactory.createEmptyTree(3));
    }

    /**
     * Constructor with factory input as well as tree input.
     * @param treeItemFactory the factory used to create items.
     * @param tree the B Tree.
     */
    public BTreeDisplay(TreeItemFactory<?> treeItemFactory, BTree_<?> tree) {
        super();
        this.treeItemFactory = treeItemFactory;
        this.treeGraphics = new BTreeGraphics(tree);
        this.xOffset = 0;
        this.yOffset = 0;
        this.scale = 1;
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {super.mouseClicked(e);}
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                isLeftPressed = e.getButton() == MouseEvent.BUTTON1;
                prevMouseX = e.getX();
                prevMouseY = e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getButton() == MouseEvent.BUTTON1)
                    isLeftPressed = false;
                prevMouseX = e.getX();
                prevMouseY = e.getY();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {super.mouseExited(e);}
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                // Arbitrary constant
                scaleDisplay(1 + e.getPreciseWheelRotation() * -0.2, e.getX(), e.getY());
                repaint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (isLeftPressed) {
                    double xDiff = e.getX() - prevMouseX;
                    double yDiff = e.getY() - prevMouseY;
                    prevMouseX = e.getX();
                    prevMouseY = e.getY();
                    moveDisplay(xDiff, yDiff);
                    repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        this.addMouseWheelListener(mouseAdapter);
        System.out.println(Arrays.toString(this.getMouseListeners()));
    }

    /**
     * Getter for x offset
     * @return the x offset
     */
    public double getXOffset() {
        return xOffset;
    }
    /**
     * Getter for y offset
     * @return the y offset
     */
    public double getYOffset() {
        return yOffset;
    }
    /**
     * Getter for zoom scale
     * @return the zoom scale
     */
    public double getScale() {
        return scale;
    }
    /**
     * Setter for x offset
     * @param xOffset the new x offset
     */
    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
    }
    /**
     * Setter for y offset
     * @param yOffset the new y offset
     */
    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
    }
    /**
     * Setter for zoom scale
     * @param scale the new zoom scale
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Adds an item into the B Tree using a string representation of the item.
     * @param itemStrRep the string representation of the item
     */
    public void addItem(String itemStrRep) {
        getTree().add(treeItemFactory.createItemFromString(itemStrRep));
    }

    /**
     * Removes an item from the B Tree using a string representation of the item.
     * @param itemStrRep the string representation of the item
     */
    public void deleteItem(String itemStrRep) {
        getTree().delete(treeItemFactory.createItemFromString(itemStrRep));
    }

    /**
     * Getter for the B Tree
     * @return the B Tree
     */
    public BTree_ getTree() {
        return treeGraphics.getTree();
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.translate(getXOffset(), getYOffset());
        graphics2D.scale(getScale(), getScale());
        setBackground(Color.WHITE);
        treeGraphics.draw(graphics);
    }

    /**
     * Moves the display to offset the graphics
     * @param xAmount change in the x direction
     * @param yAmount change in the y direction
     */
    public void moveDisplay(double xAmount, double yAmount) {
        setXOffset(getXOffset() + xAmount);
        setYOffset(getYOffset() + yAmount);
    }

    /**
     * Scales the zoom to change the display size of the graphics
     * @param multiplier the multiplier to be applied to the scale
     * @param xPos the x position where the scale is occuring relative to the canvas.
     * @param yPos the x position where the scale is occuring relative to the canvas.
     */
    public void scaleDisplay(double multiplier, double xPos, double yPos) {
        setScale(getScale() * multiplier);
    }

    /**
     * Returns the bounds of a string to be printed.
     * Rectangle2D values have an initial 0,0 position
     * @param text the text whose bounds are to be found
     * @param graphics the graphics instance
     * @return the bounds noted by a Rectangle2D object
     */
    static Rectangle2D getStringBounds(String text, Graphics graphics) {
        return graphics.getFontMetrics().getStringBounds(text, graphics);
    }

    /**
     * Returns an array of bounds of string to be printed.
     * Rectangle2D values have an initial 0,0 position.
     * <br>
     * <b>The bounds of each string are also positioned to line up next to each other,
     * as if the text were lined horizontally.</b>
     * @param textArray the array of text whose bounds are to be found
     * @param graphics the graphics instance
     * @param spacing the spacing in between each string bounds
     * @return the bounds noted by a Rectangle2D object
     */
    static Rectangle2D[] getStringBounds(String[] textArray,
                                                 Graphics graphics,
                                                 double spacing) {
        Rectangle2D[] boundsArray = new Rectangle2D[textArray.length];
        double accumulatingX = 0;  // Removes the extra spacing for a total of (n-1) spacings
        Rectangle2D rect;
        for (int i = 0; i < textArray.length; i++) {
            rect = getStringBounds(textArray[i], graphics);
            rect.setRect(accumulatingX, 0, rect.getWidth(), rect.getHeight());
            accumulatingX += rect.getWidth() + spacing;
            boundsArray[i] = rect;
        }
        return boundsArray;
    }

    /**
     * Fills a rectangle with padding in a given graphics instance
     * @param graphics the graphics instance
     * @param x the x position
     * @param y the y position
     * @param w the width of the rectangle without padding
     * @param h the height of the rectangle without padding
     * @param padding the padding
     */
    static void fillRoundRectPadding(Graphics2D graphics,
                                     double x, double y, double w, double h, double arc, double padding) {
        graphics.fillRoundRect(
                (int) (x - padding),
                (int) (y - padding),
                (int) (w + padding * 2),
                (int) (h + padding * 2),
                (int) arc,
                (int) arc
        );
    }
}
