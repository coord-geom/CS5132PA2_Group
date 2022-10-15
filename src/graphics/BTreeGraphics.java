package graphics;

import model.BNode_;
import model.BTree_;
import model.Node;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Wrapper class for a B Tree to be used in the BTreeDisplay Canvas.
 */
public class BTreeGraphics {

    // Default constant values and settings
    private final static double SPACING_BETWEEN_LEVELS = 52;

    private final static double SPACING_WITHIN_LEVELS = 20;

    /**
     * The wrapped B Tree object
     */
    private final BTree_<?> tree;

    /**
     * The offset x amount for the display graphics
     */
    @Deprecated
    private double xOffset;
    /**
     * The offset y amount for the display graphics
     */
    @Deprecated
    private double yOffset;

    /**
     * Constructor with tree input.
     *
     * @param tree the B Tree.
     */
    public BTreeGraphics(BTree_<?> tree) {
        this.tree = tree;
    }

    /**
     * Getter for the tree
     *
     * @return the B Tree
     */
    public BTree_<?> getTree() {
        return tree;
    }

    /**
     * Getter for x offset
     * @return the x offset
     */
    @Deprecated
    public double getXOffset() {
        return xOffset;
    }
    /**
     * Getter for y offset
     * @return the y offset
     */
    @Deprecated
    public double getYOffset() {
        return yOffset;
    }
    /**
     * Setter for x offset
     * @param xOffset the new x offset
     */
    @Deprecated
    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
    }
    /**
     * Setter for y offset
     * @param yOffset the new y offset
     */
    @Deprecated
    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    /**
     * Returns an ArrayList of NodeGraphics objects,
     * each with a draw() method that allows the whole tree to be displayed
     *
     * @param graphics the graphics instance needed to calculate values
     * @return an ArrayList of NodeGraphics objects.
     */
    private ArrayList<NodeGraphics> getNodeGraphics(Graphics graphics) {
        // Step 1:
        // Iterate level-order through the tree using breadth-first iteration
        // Tracks down nodes as well as their children.

        int height = getTree().getHeight();

        // List to record down level order nodes
        ArrayList<ArrayList<BNode_<?>>> levelsNodes = new ArrayList<>(height + 1);
        for (int i = 0; i < height + 1; i++)
            levelsNodes.add(new ArrayList<>());

        // List to record down parent indices (index of the list on the level above the child node) of level order nodes
        ArrayList<ArrayList<Integer>> levelsParent = new ArrayList<>(height + 1);
        for (int i = 0; i < height + 1; i++)
            levelsParent.add(new ArrayList<>());

        // Stack to keep track of next nodes to iterate.
        ArrayList<BNode_<?>> nodesIterationStack = new ArrayList<>();
        // Stack to keep track of the levels of the nodes in the iteration stack.
        ArrayList<Integer> levelsIterationStack = new ArrayList<>();
        // Stack to keep track of the indices of the parents of the nodes in the iteration stack.
        ArrayList<Integer> parentsIterationStack = new ArrayList<>();
        // Start from root
        nodesIterationStack.add(getTree().getRootNode());
        levelsIterationStack.add(0);
        parentsIterationStack.add(-1);  // Root node does not have a parent

        BNode_<?> currNode;
        int currLevel;
        int currParentIndex;
        int currIndex;
        // Conduct iteration
        while (!nodesIterationStack.isEmpty()) {
            // Get data of current node
            currNode = nodesIterationStack.remove(0);
            currLevel = levelsIterationStack.remove(0);
            currParentIndex = parentsIterationStack.remove(0);

            // Add data to the recording lists
            currIndex = levelsNodes.get(currLevel).size();
            if (currLevel != 0)
                levelsParent.get(currLevel).add(currParentIndex);
            levelsNodes.get(currLevel).add(currNode);

            // If the node is not a leaf, add more iterable child nodes and relevant data
            if (!currNode.isLeaf()) {
                for (Node<?> node: currNode.getNeighbours()) {
                    if (node == null)
                        break;
                    nodesIterationStack.add((BNode_<?>) node);
                    levelsIterationStack.add(currLevel + 1);  // Child nodes are one level down
                    parentsIterationStack.add(currIndex);
                }
            }
        }

        // Step 2:
        // Turning data into graphics classes

        // Records into a single list
        ArrayList<NodeGraphics> nodeGraphics = new ArrayList<>();

        // Records into level separated lists for easy retrieval of parent nodes.
        ArrayList<ArrayList<NodeGraphics>> levelsNodeGraphics = new ArrayList<>(height + 1);
        for (int i = 0; i < height + 1; i++)
            levelsNodeGraphics.add(new ArrayList<>());

        // Iterate through all nodes and create node graphics
        NodeGraphics parentNode;
        NodeGraphics newNodeGraphics;
        for (int level = 0; level < height + 1; level++) {
            for (int i = 0; i < levelsNodes.get(level).size(); i++) {
                // Adding parent NodeGraphic objects to current node classes is possible
                // as the recording lists maintain the property that the parent nodes are always before child nodes
                // due to the breadth-first iteration.
                if (level != 0)
                    // gets the parent from one level up
                    parentNode = levelsNodeGraphics.get(level - 1).get(levelsParent.get(level).get(i));
                else
                    parentNode = null;

                newNodeGraphics = new NodeGraphics(0, 0,
                        levelsNodes.get(level).get(i), parentNode);
                levelsNodeGraphics.get(level).add(newNodeGraphics);
                nodeGraphics.add(newNodeGraphics);
            }
        }

        // Step 3:
        // Calculate positions

        // Update values in graphics objects required for following calculations
        for (NodeGraphics nodeGraphic : nodeGraphics)
            nodeGraphic.updateDimensionsAndBounds(graphics);

        // Separate vertically and calculate level length
        ArrayList<Double> levelLength = new ArrayList<>();  // Tracks the length of each level.
        double yOffset = 0;
        for (int level = 0; level < levelsNodeGraphics.size(); level++) {
            double maxHeight = 0;
            levelLength.add(0.);

            for (int i = 0; i < levelsNodeGraphics.get(level).size(); i++) {
               NodeGraphics nodeGraphicsAtLevel = levelsNodeGraphics.get(level).get(i);
                if (nodeGraphicsAtLevel.getHeight() > maxHeight)
                    maxHeight = nodeGraphicsAtLevel.getHeight();
                nodeGraphicsAtLevel.setPosY(yOffset);  // Sets new y position of the node graphic

                // increments the length of the level
                double lengthIncrement = nodeGraphicsAtLevel.getWidth() + NodeGraphics.PADDING * 2;
                if (i != 0)
                    lengthIncrement += SPACING_WITHIN_LEVELS;
                levelLength.set(level, levelLength.get(level) + lengthIncrement);
            }
            yOffset += maxHeight + SPACING_BETWEEN_LEVELS;
        }

        // Separate horizontally
        double maxLength = Collections.max(levelLength);
        for (int level = 0; level < levelsNodeGraphics.size(); level++) {
            double xOffset = (maxLength - levelLength.get(level)) / 2;  // starting offset

            for (int i = 0; i < levelsNodeGraphics.get(level).size(); i++) {
                NodeGraphics nodeGraphicsAtLevel = levelsNodeGraphics.get(level).get(i);
                nodeGraphicsAtLevel.setPosX(xOffset);
                // increment offset
                xOffset += nodeGraphicsAtLevel.getWidth() + NodeGraphics.PADDING * 2 + SPACING_WITHIN_LEVELS;
            }
        }

        return nodeGraphics;
    }

    /**
     * Draws the B Tree
     *
     * @param graphics the graphics instance
     */
    void draw(Graphics graphics) {
        ArrayList<NodeGraphics> nodeGraphics = getNodeGraphics(graphics);
        // Draw node body
        for (NodeGraphics nodeGraphic : nodeGraphics)
            nodeGraphic.drawBody(graphics);
        // drawChildConnections is called after drawBody is called
        // as it requires the positional coordinate data of the child nodes to be initialised first
        for (NodeGraphics nodeGraphic : nodeGraphics)
            nodeGraphic.drawChildConnections(graphics);

        //
    }
    /**
     * A class representing the nodes of the B Tree to be drawn.
     */
    static class NodeGraphics {

        // Default constant values and settings
        private final static double SPACING = 4;  // Spacing between items excluding padding
        private final static double PADDING = 8;  // Starts from the raw string bounding box
        private final static double ITEM_PADDING = 4;  // Starts from the raw string bounding box
        private final static Color BG_COLOR = Color.BLACK;
        private final static Color ITEM_BG_COLOR = Color.BLUE;
        private final static Color FONT_COLOR = Color.GREEN;
        private final static Font FONT = new Font("Arial", Font.BOLD, 20);
        private final static Stroke LINE_STROKE = new BasicStroke(3);
        private final static Color LINE_COLOR = Color.BLACK;

        /**
         * The item representation of the items inside the Node
         * Will not contain any null values
         */
        private String[] items;

        /**
         * Position of the graphic, located at the top left of the bounding box.
         * <br>
         * (x, y)
         * <br>
         * Top left corner located where the bounding box is,
         * which bounds the item padding, but does not bound the total node padding.
         */
        private final double[] coords;

        /**
         * Width and height of the graphic, initialised value is (0, 0).
         * values are only filled when drawBody() is called.
         * <br>
         * (w, h)
         * <br>
         * Dimensions include the item padding but not the total node padding.
         */
        private final double[] dimensions;

        /**
         * The NodeGraphics object that is the parent of the node.
         * May be null if parent does not exist.
         */
        private final NodeGraphics parent;

        /**
         * Array of Rectangle2D bounds of items in a node.
         * Is not instantiated at initialisation.
         * Used by draw methods to communicate bounds information with each other.
         */
        private Rectangle2D[] itemBounds;

        /**
         * Constructor
         *
         * @param posX              the x position of the graphic
         * @param posY              the y position of the graphic
         * @param node              the node of the B Tree with relevant data
         * @param parentNodeGraphic the parent node, null if it does not exist
         */
        NodeGraphics(double posX, double posY, BNode_<?> node, NodeGraphics parentNodeGraphic) {
            this.items = null;
            initialiseItems(node);

            this.coords = new double[2];
            setPosX(posX);
            setPosY(posY);

            this.dimensions = new double[]{0, 0};

            this.parent = parentNodeGraphic;
        }

        /**
         * Updates dimensions and calculates item bounds
         * Needs to be called after NodeGraphics object creation so that dimension values are instantiated.
         *
         * @param graphics the graphics instance
         */
        void updateDimensionsAndBounds(Graphics graphics) {
            graphics.setFont(FONT);
            itemBounds = BTreeDisplay.getStringBounds(items, graphics, SPACING + ITEM_PADDING * 2);

            // Update dimensions
            dimensions[0] = 0;
            dimensions[1] = 0;
            int numItemsCounted = 0;  // Used to add correct padding and spacing values
            for (Rectangle2D bounds : itemBounds) {
                dimensions[0] += bounds.getWidth();
                if (numItemsCounted != 0)
                    dimensions[0] += ITEM_PADDING * 2 + SPACING;
                numItemsCounted += 1;
                if (dimensions[1] < bounds.getHeight())
                    dimensions[1] = bounds.getHeight();
            }
        }

        /**
         * Draws the main body of the node
         * Needs to be called after updateDimensionsAndBounds().
         *
         * @param graphics the graphics instance
         */
        void drawBody(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics;  // More powerful graphics class
            graphics.setFont(FONT);

            // Update values
            updateDimensionsAndBounds(graphics);

            // Draw Node
            graphics.setColor(BG_COLOR);
            BTreeDisplay.fillRectPadding(
                    graphics2D,
                    getPosX(),
                    getPosY(),
                    getWidth(),
                    getHeight(),
                    PADDING);

            // Draw items
            for (int i = 0; i < items.length; i++) {
                graphics.setColor(ITEM_BG_COLOR);
                BTreeDisplay.fillRectPadding(
                        graphics2D,
                        itemBounds[i].getX() + getPosX(),
                        itemBounds[i].getY() + getPosY(),
                        itemBounds[i].getWidth(),
                        itemBounds[i].getHeight(),
                        ITEM_PADDING);

                graphics.setColor(FONT_COLOR);
                graphics.drawString(items[i],
                        (int) (itemBounds[i].getX() + getPosX()),
                        (int) (itemBounds[i].getY() + getPosY() + itemBounds[i].getHeight() * 4/5));
                // Height added as drawString draws using coords as the bottom left corner of the text bounding box
                // Multiplied by a constant 4/5 is as it looks more center
            }
        }

        /**
         * Draws the parent connection.
         * To be called after every node has called drawBody()
         *
         * @param graphics the graphics instance
         */
        void drawChildConnections(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            if (parent == null)  // parent does not exist
                return;

            double lineStartX = getPosX() + getWidth() / 2;
            double lineStartY = getPosY() - PADDING * 3/4;
            double lineEndX = parent.getPosX() + parent.getWidth() / 2;
            double lineEnxY = parent.getPosY() + parent.getHeight() + PADDING * 3/4;

            graphics2D.setStroke(LINE_STROKE);
            graphics2D.setColor(LINE_COLOR);
            graphics2D.drawLine((int) lineStartX, (int) lineStartY, (int) lineEndX, (int) lineEnxY);
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
            // Find the number of non-null values
            int numNonNull = 0;
            // Since null values are always towards the end of the array, we iterate until we find a null value
            for (Object item: items) {
                if (item == null)
                    break;
                numNonNull += 1;
            }

            // Initialise items with number of non-null values, and populate with string data
            this.items = new String[numNonNull];
            for (int i = 0; i < numNonNull; i++) {
                this.items[i] = items[i].toString();
            }
        }

        @Override
        public String toString() {
            return "<<< NODE >>>\nItems: " + Arrays.toString(items) +
                    "\nCoords: " + Arrays.toString(coords) +
                    "\nDimensions: " + Arrays.toString(dimensions) +
                    "\nParent: " + ((parent == null) ? "None" : Arrays.toString(parent.items)) +
                    "\n";
        }
    }

}
