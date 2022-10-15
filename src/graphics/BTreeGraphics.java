package graphics;

import model.BNode_;
import model.BTree_;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Wrapper class for a B Tree to be used in the BTreeDisplay Canvas.
 *
 * @param tree The B Tree
 */
public record BTreeGraphics(BTree_<?> tree) {

    /**
     * Constructor with tree input.
     *
     * @param tree the B Tree.
     */
    public BTreeGraphics {}

    /**
     * Getter for the tree
     *
     * @return the B Tree
     */
    @Override
    public BTree_<?> tree() {
        return tree;
    }

    /**
     * Returns an ArrayList of NodeGraphics objects,
     * each with a draw() method that allows the whole tree to be displayed
     *
     * @return an ArrayList of NodeGraphics objects.
     */
    //TODO
    private ArrayList<NodeGraphics> getNodeGraphics() {
        ArrayList<ArrayList<NodeGraphics>> nodeGraphicsLevels = new ArrayList<>();

        return new ArrayList<>();
    }

    /**
     * Iterate level-order through the tree
     * @param nodeGraphicsLevels the given list of levels and nodes.
     */
    //TODO
    private void getLevelOrderNodes(ArrayList<ArrayList<NodeGraphics>> nodeGraphicsLevels) {

    }

    /**
     * Draws the B Tree
     *
     * @param graphics the graphics instance
     */
    void draw(Graphics graphics) {
        ArrayList<NodeGraphics> nodeGraphics = getNodeGraphics();
        for (NodeGraphics nodeGraphic : nodeGraphics)
            nodeGraphic.drawBody(graphics);
        // drawChildConnections is called after drawBody is called
        // as it requires the positional coordinate data of the child nodes to be initialised first
        for (NodeGraphics nodeGraphic : nodeGraphics)
            nodeGraphic.drawChildConnections(graphics);
    }

    /**
     * A class representing the nodes of the B Tree to be drawn.
     */
    static class NodeGraphics {

        // Default values and settings
        private final static double SPACING = 5;
        private final static double PADDING = 10;
        private final static double ITEM_PADDING = 2;
        private final static Color BG_COLOR = Color.BLACK;
        private final static Color ITEM_BG_COLOR = Color.BLUE;
        private final static Color FONT_COLOR = Color.WHITE;
        private final static Font FONT = new Font("Courier New", Font.BOLD, 20);

        /**
         * The item representation of the items inside the Node
         */
        private final String[] items;

        /**
         * Position of the graphic, located at the top left of the bounding box.
         * (x, y)
         */
        private final double[] coords;

        /**
         * Width and height of the graphic, initialised value is (0, 0).
         * values are only filled when drawBody() is called.
         * (w, h)
         */
        private final double[] dimensions;

        /**
         * The children of the node.
         */
        private NodeGraphics[] children;

        /**
         * Constructor
         *
         * @param posX              the x position of the graphic
         * @param posY              the y position of the graphic
         * @param node              the node of the B Tree with relevant data
         * @param childNodeGraphics the child nodes
         */
        NodeGraphics(double posX, double posY, BNode_<?> node, NodeGraphics[] childNodeGraphics) {
            this.items = new String[node.getItems().length];
            initialiseItems(node);

            this.coords = new double[2];
            setPosX(posX);
            setPosY(posY);

            this.dimensions = new double[]{0, 0};

            this.children = childNodeGraphics;
        }

        /**
         * Draws the main body of the node
         *
         * @param graphics the graphics instance
         */
        void drawBody(Graphics graphics) {
            graphics.setFont(FONT);

            // Default spacing is 5
            Rectangle2D[] itemBounds = BTreeDisplay.getStringBounds(
                    items, graphics, SPACING + ITEM_PADDING * 2);

            // Update dimensions
            dimensions[0] = 0;
            dimensions[1] = 0;
            for (Rectangle2D bounds : itemBounds) {
                dimensions[0] += bounds.getWidth();
                if (dimensions[1] < bounds.getHeight())
                    dimensions[1] = bounds.getHeight();
            }

            // Draw Node
            graphics.setColor(BG_COLOR);
            BTreeDisplay.fillRectPadding(
                    graphics,
                    getPosX(),
                    getPosY(),
                    getWidth(),
                    getHeight(),
                    PADDING);

            // Draw items
            for (int i = 0; i < items.length; i++) {
                graphics.setColor(ITEM_BG_COLOR);
                BTreeDisplay.fillRectPadding(
                        graphics,
                        itemBounds[i].getX(),
                        itemBounds[i].getY(),
                        itemBounds[i].getWidth(),
                        itemBounds[i].getHeight(),
                        ITEM_PADDING);

                graphics.setColor(FONT_COLOR);
                graphics.drawString(
                        items[i],
                        (int) itemBounds[i].getX(),
                        (int) itemBounds[i].getY());
            }
        }

        /**
         * Draws the child connections.
         * To be called after every node has called drawBody()
         *
         * @param graphics the graphics instance
         */
        //TODO child draw connections
        void drawChildConnections(Graphics graphics) {

        }

        /**
         * Getter
         *
         * @return the x position of the graphic
         */
        private double getPosX() {
            return coords[0];
        }

        /**
         * Setter
         *
         * @param posX the new x position of the graphic
         */
        private void setPosX(double posX) {
            coords[0] = posX;
        }

        /**
         * Getter
         *
         * @return the y position of the graphic
         */
        private double getPosY() {
            return coords[1];
        }

        /**
         * Setter
         *
         * @param posY the new y position of the graphic
         */
        private void setPosY(double posY) {
            coords[1] = posY;
        }

        /**
         * Getter
         *
         * @return the width of the graphic
         */
        private double getWidth() {
            return dimensions[0];
        }

        /**
         * Getter
         *
         * @return the height of the graphic
         */
        private double getHeight() {
            return dimensions[1];
        }

        /**
         * Initialises the String array of item string representations stored in the BNodeGraphics object.
         *
         * @param node the node input with relevant data.
         */
        private void initialiseItems(BNode_<?> node) {
            Object[] items = node.getItems();
            for (int i = 0; i < items.length; i++) {
                this.items[i] = items[i].toString();
            }
        }

        /**
         * Method that shifts the position of the graphic
         *
         * @param x the x amount shifted
         * @param y the y amount shifted
         */
        private void move(double x, double y) {
            setPosX(getPosX() + x);
            setPosY(getPosY() + y);
        }
    }

}
