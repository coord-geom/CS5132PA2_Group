package graphics;

import model.BNode_;
import model.BTree_;
import model.graphics.TreeItemFactory;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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
    }

    /**
     * Sets the offset of the tree graphics
     * @param xAmount the x amount offset
     * @param yAmount the y amount affset
     */
    @Deprecated
    public void setOffset(double xAmount, double yAmount) {
        this.treeGraphics.setXOffset(xAmount);
        this.treeGraphics.setYOffset(yAmount);
    }

    /**
     * Changes the offset of the tree graphics by specified amounts
     * @param xAmount the x amount changed
     * @param yAmount the y amount changed
     */
    @Deprecated
    public void changeOffset(double xAmount, double yAmount) {
        this.treeGraphics.setXOffset(this.treeGraphics.getXOffset() + xAmount);
        this.treeGraphics.setYOffset(this.treeGraphics.getYOffset() + yAmount);
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
        graphics2D.translate(100, 100);
        graphics2D.scale(1, 1);
        setBackground(Color.WHITE);
        treeGraphics.draw(graphics);
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
    static void fillRectPadding(Graphics2D graphics, double x, double y, double w, double h, double padding) {
        graphics.fillRoundRect(
                (int) (x - padding),
                (int) (y - padding),
                (int) (w + padding * 2),
                (int) (h + padding * 2),
                (int) padding,
                (int) padding
        );
    }

    /**
     * Draws an item inside a node
     * @param graphics the graphics instance
     * @param posX the x position
     * @param posY the y position
     * @param item the item whose toString will be used for display
     */
    @Deprecated
    private void drawItem(Graphics graphics, int posX, int posY, Object item) {
        String text = item.toString();

        graphics.setColor(Color.YELLOW);
        Rectangle2D rect = getStringBounds(text, graphics);
        graphics.fillRect(posX, posY, (int)rect.getWidth(), (int)rect.getHeight());

        graphics.setColor(Color.BLACK);
        graphics.drawString(text, posX, posY + (int)(rect.getHeight() * (3./4.)));
    }

    /**
     * Draws a node of the B Tree.
     * @param graphics the graphics instance
     * @param posX the x position
     * @param posY the y position
     * @param node the node to be displayed
     */
    @Deprecated
    private void drawNode(Graphics graphics, int posX, int posY, BNode_<?> node) {
        Object[] items = node.getItems();
        int gapSize = 5;
        // Loop through once to get the width and height of the node.
        int width = gapSize;
        int maxHeight = 0;
        for (Object item: items) {
            if (item == null)
                break;
            Rectangle2D rect = getStringBounds(item.toString(), graphics);
            width += rect.getWidth() + gapSize; // gapSize is added for the gap between the items
            if (maxHeight < rect.getHeight())
                maxHeight = (int) rect.getHeight();
        }

        // Draw the node
        graphics.setColor(Color.ORANGE);
        graphics.fillRect(posX, posY - gapSize, width, maxHeight + gapSize + gapSize);

        // Loop through again to draw items
        posX += gapSize;
        for (Object item: items) {
            if (item == null)
                break;
            drawItem(graphics, posX, posY, item);
            Rectangle2D rect = getStringBounds(item.toString(), graphics);
            posX += rect.getWidth() + gapSize;
        }
    }
}
