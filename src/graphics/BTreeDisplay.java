package graphics;

import model.BNode;
import model.BTree;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Graphics class used to display the B Tree.
 * Subclasses canvas
 */
public class BTreeDisplay extends Canvas {

    /**
     * The B Tree displayed
     */
    private BTree<?> tree;
    private FontMetrics fontMetrics;

    /**
     * Constructor
     * @param tree the B Tree to be displayed
     */
    public BTreeDisplay(BTree<?> tree) {
        super();
        this.tree = tree;
    }

    /**
     * Setter for the B Tree
     * @param tree the new B Tree
     */
    public void setTree(BTree<?> tree) {
        this.tree = tree;
    }

    /**
     * Getter for the B Tree
     * @return the B Tree
     */
    public BTree<?> getTree() {
        return tree;
    }

    /**
     * Returns the bounds of a string to be printed.
     * @param text the text whose bounds are to be found
     * @param graphics the graphics instance
     * @return the bounds noted by a Rectangle2D object
     */
    private Rectangle2D getStringBounds(String text, Graphics graphics) {
        return fontMetrics.getStringBounds(text, graphics);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setFont(new Font("Courier New", Font.BOLD, 20));
        fontMetrics = graphics.getFontMetrics();

        setBackground(Color.WHITE);
        drawNode(graphics, 100, 100, tree.getRootNode());
    }

    /**
     * Draws an item inside a node
     * @param graphics the graphics instance
     * @param posX the x position
     * @param posY the y position
     * @param item the item whose toString will be used for display
     */
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
    private void drawNode(Graphics graphics, int posX, int posY, BNode<?> node) {
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

    private void drawLevel(Graphics graphics, int posX, int posY) {

    }
}
