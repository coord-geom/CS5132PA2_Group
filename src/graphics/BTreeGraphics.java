package graphics;

import model.BNode;
import model.BTree;
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
    private final static double SPACING_BETWEEN_LEVELS = 300;

    private final static double SPACING_WITHIN_LEVELS = 20;

    /**
     * The wrapped B Tree object
     */
    private final BTree tree;

    /**
     * Stores the graphics node objects
     */
    private ArrayList<NodeGraphics> nodeGraphics;

    /**
     * Denotes whether each level should have its nodes displayed vertically instead of horizontally.
     * Will display the graphics from left to right level-wise, with items going from top to down per level.
     * Default is false
     */
    private boolean isVertical;

    /**
     * Denotes whether each node should have its items displayed vertically instead of horizontally.
     * Default is false
     */
    private boolean isItemVertical;

    /**
     * Boolean to track whether an update has been made to the tree.
     */
    private boolean hasUpdate;

    /**
     * Constructor with tree input.
     *
     * @param tree the B Tree.
     */
    public BTreeGraphics(BTree tree) {
        this(tree, false, false);
    }

    /**
     * Constructor with additional isVertical specifier
     *
     * @param tree           the B Tree.
     * @param isVertical     boolean parameter
     * @param isItemVertical boolean parameter
     */
    public BTreeGraphics(BTree tree, boolean isVertical, boolean isItemVertical) {
        this.tree = tree;
        this.nodeGraphics = new ArrayList<>();
        setVertical(isVertical);
        setItemVertical(isItemVertical);
        update();
    }

    /**
     * Getter for the tree
     *
     * @return the B Tree
     */
    public BTree getTree() {
        return tree;
    }

    /**
     * Getter for whether nodes within a level should be displayed vertically
     *
     * @return boolean value
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * Setter for whether nodes within a level should be displayed vertically
     *
     * @param isVertical parameter
     */
    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    /**
     * Getter for whether items within a node should be displayed vertically
     *
     * @return boolean value
     */
    public boolean isItemVertical() {
        return isItemVertical;
    }

    /**
     * Setter for whether items within a node should be displayed vertically
     *
     * @param isItemVertical parameter
     */
    public void setItemVertical(boolean isItemVertical) {
        this.isItemVertical = isItemVertical;
    }

    /**
     * Must be called when an update is made to the tree to update the NodeGraphics ArrayList.
     */
    public void update() {
        hasUpdate = true;
    }

    /**
     * Updates the ArrayList of NodeGraphics objects,
     * each with a draw() method that allows the whole tree to be displayed
     *
     * @param graphics the graphics instance needed to calculate values
     */
    private void updateNodeGraphics(Graphics graphics) {
        // Step 1:
        // Iterate level-order through the tree using breadth-first iteration
        // Tracks down nodes as well as their children.

        int height = getTree().getHeight();

        // List to record down level order nodes
        ArrayList<ArrayList<BNode>> levelsNodes = new ArrayList<>(height + 1);
        for (int i = 0; i < height + 1; i++)
            levelsNodes.add(new ArrayList<>());

        // List to record down parent indices (index of the list on the level above the child node) of level order nodes
        ArrayList<ArrayList<Integer>> levelsParent = new ArrayList<>(height + 1);
        for (int i = 0; i < height + 1; i++)
            levelsParent.add(new ArrayList<>());

        // Stack to keep track of next nodes to iterate.
        ArrayList<BNode> nodesIterationStack = new ArrayList<>();
        // Stack to keep track of the levels of the nodes in the iteration stack.
        ArrayList<Integer> levelsIterationStack = new ArrayList<>();
        // Stack to keep track of the indices of the parents of the nodes in the iteration stack.
        ArrayList<Integer> parentsIterationStack = new ArrayList<>();
        // Start from root
        nodesIterationStack.add(getTree().root);
        levelsIterationStack.add(0);
        parentsIterationStack.add(-1);  // Root node does not have a parent

        BNode<?> currNode;
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
            if (!currNode.isLeaf) {
                for (Node<?> node : currNode.neighbours) {
                    if (node == null)
                        break;
                    nodesIterationStack.add((BNode) node);
                    levelsIterationStack.add(currLevel + 1);  // Child nodes are one level down
                    parentsIterationStack.add(currIndex);
                }
            }
        }

        // Step 2:
        // Turning data into graphics classes

        // Records into a single list
        nodeGraphics = new ArrayList<>();

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
                        levelsNodes.get(level).get(i), parentNode,
                        isVertical(), isItemVertical());
                levelsNodeGraphics.get(level).add(newNodeGraphics);
                nodeGraphics.add(newNodeGraphics);
            }
        }

        // Step 3:
        // Calculate positions

        // Update values in graphics objects required for following calculations
        for (NodeGraphics nodeGraphic : nodeGraphics)
            nodeGraphic.updateDimensionsAndBounds(graphics);

        // Separate level-wise and calculate level length
        ArrayList<Double> levelLength = new ArrayList<>();  // Tracks the length of each level.
        double dimOffset = 0;
        for (int level = 0; level < levelsNodeGraphics.size(); level++) {
            double maxDim = 0;
            levelLength.add(0.);

            for (int i = 0; i < levelsNodeGraphics.get(level).size(); i++) {
                NodeGraphics nodeGraphicsAtLevel = levelsNodeGraphics.get(level).get(i);

                // Check for possible new maximum dimension (depending on vertical/horizontal)
                if (isVertical()) {
                    if (nodeGraphicsAtLevel.getWidth() > maxDim)
                        maxDim = nodeGraphicsAtLevel.getWidth();
                } else {
                    if (nodeGraphicsAtLevel.getHeight() > maxDim)
                        maxDim = nodeGraphicsAtLevel.getHeight();
                }

                // Sets new level-wise position of the node graphic (depending on vertical/horizontal)
                if (isVertical())
                    nodeGraphicsAtLevel.setPosX(dimOffset);
                else
                    nodeGraphicsAtLevel.setPosY(dimOffset);

                // increments the length of the level (depending on vertical/horizontal)
                double lengthIncrement = 0;
                if (isVertical)
                    lengthIncrement = nodeGraphicsAtLevel.getHeight() + NodeGraphics.PADDING * 2;
                else
                    lengthIncrement = nodeGraphicsAtLevel.getWidth() + NodeGraphics.PADDING * 2;
                if (i != 0)
                    lengthIncrement += SPACING_WITHIN_LEVELS;
                levelLength.set(level, levelLength.get(level) + lengthIncrement);
            }
            dimOffset += maxDim + SPACING_BETWEEN_LEVELS;
        }

        // Separate item-wise within levels
        double maxLength = Collections.max(levelLength);
        for (int level = 0; level < levelsNodeGraphics.size(); level++) {
            double levelDimOffset = (maxLength - levelLength.get(level)) / 2;  // starting offset

            for (int i = 0; i < levelsNodeGraphics.get(level).size(); i++) {
                NodeGraphics nodeGraphicsAtLevel = levelsNodeGraphics.get(level).get(i);
                // set offset x/y (depending on vertical/horizontal)
                if (isVertical)
                    nodeGraphicsAtLevel.setPosY(levelDimOffset);
                else
                    nodeGraphicsAtLevel.setPosX(levelDimOffset);
                // increment offset
                levelDimOffset += NodeGraphics.PADDING * 2 + SPACING_WITHIN_LEVELS;
                if (isVertical)
                    levelDimOffset += nodeGraphicsAtLevel.getHeight();
                else
                    levelDimOffset += nodeGraphicsAtLevel.getWidth();
            }
        }
    }

    /**
     * Draws the B Tree
     *
     * @param graphics the graphics instance
     */
    void draw(Graphics graphics) {
        if (hasUpdate) {
            updateNodeGraphics(graphics);
            hasUpdate = false;
        }
        // Draw node body
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

        // Default constant values and settings
        private final static double PADDING = 16;  // Starts from the raw string bounding box
        private final static double ARC = 50;
        private final static double ITEM_PADDING = 12;  // Starts from the raw string bounding box

        private final static double ITEM_ARC = 40;
        private final static double SPACING = 16;  // Spacing between items excluding padding
        private final static Color BG_COLOR = new Color(120, 130, 150);
        private final static Color ITEM_BG_COLOR = new Color(20, 30, 60);
        private final static Color FONT_COLOR = Color.WHITE;
        private final static Font FONT = new Font("Verdana", Font.BOLD, 20);
        private final static Stroke LINE_STROKE = new BasicStroke(2);
        private final static Color LINE_COLOR = new Color(120, 130, 150);

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
         * whether the nodes are displayed vertically within a level
         */
        private boolean isNodeVertical;
        /**
         * whether the items are displayed vertically within a node
         */
        private boolean isItemVertical;

        /**
         * Constructor
         *
         * @param posX              the x position of the graphic
         * @param posY              the y position of the graphic
         * @param node              the node of the B Tree with relevant data
         * @param parentNodeGraphic the parent node, null if it does not exist
         * @param isNodeVertical    whether the nodes are displayed vertically within a level
         * @param isItemVertical    whether the items are displayed vertically within a node
         */
        NodeGraphics(double posX, double posY, BNode node, NodeGraphics parentNodeGraphic,
                     boolean isNodeVertical, boolean isItemVertical) {
            this.items = null;
            initialiseItems(node);

            this.coords = new double[2];
            setPosX(posX);
            setPosY(posY);

            this.dimensions = new double[]{0, 0};
            this.parent = parentNodeGraphic;
            this.isNodeVertical = isNodeVertical;
            this.isItemVertical = isItemVertical;
        }

        /**
         * Updates dimensions and calculates item bounds
         * Needs to be called after NodeGraphics object creation so that dimension values are instantiated.
         *
         * @param graphics the graphics instance
         */
        void updateDimensionsAndBounds(Graphics graphics) {
            graphics.setFont(FONT);
            itemBounds = BTreeDisplay.getStringBounds(items, graphics,
                    SPACING + ITEM_PADDING * 2, isItemVertical);

            // Update dimensions

            // Get proper index values (depending on horizontal/vertical)
            int dimIncrementingValueIndex;
            int dimMaximisingValueIndex;
            if (isItemVertical) {
                dimIncrementingValueIndex = 1;
                dimMaximisingValueIndex = 0;
            } else {
                dimIncrementingValueIndex = 0;
                dimMaximisingValueIndex = 1;
            }
            dimensions[dimIncrementingValueIndex] = 0;
            dimensions[dimMaximisingValueIndex] = 0;

            // iterate through items
            int numItemsCounted = 0;  // Used to add correct padding and spacing values
            for (Rectangle2D bounds : itemBounds) {
                // Increment incrementing dimension
                double increment;
                if (isItemVertical)
                    increment = bounds.getHeight();
                else
                    increment = bounds.getWidth();
                dimensions[dimIncrementingValueIndex] += increment;
                if (numItemsCounted != 0)
                    dimensions[dimIncrementingValueIndex] += ITEM_PADDING * 2 + SPACING;

                // Check for possible new maximising dimension
                double candidate;
                if (isItemVertical)
                    candidate = bounds.getWidth();
                else
                    candidate = bounds.getHeight();
                numItemsCounted += 1;
                if (dimensions[dimMaximisingValueIndex] < candidate)
                    dimensions[dimMaximisingValueIndex] = candidate;
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
            BTreeDisplay.fillRoundRectPadding(
                    graphics2D,
                    getPosX(),
                    getPosY(),
                    getWidth(),
                    getHeight(),
                    ARC,
                    PADDING);

            // Draw items
            for (int i = 0; i < items.length; i++) {
                graphics.setColor(ITEM_BG_COLOR);
                BTreeDisplay.fillRoundRectPadding(
                        graphics2D,
                        itemBounds[i].getX() + getPosX(),
                        itemBounds[i].getY() + getPosY(),
                        itemBounds[i].getWidth(),
                        itemBounds[i].getHeight(),
                        ITEM_ARC,
                        ITEM_PADDING);

                graphics.setColor(FONT_COLOR);
                graphics.drawString(items[i],
                        (int) (itemBounds[i].getX() + getPosX()),
                        (int) (itemBounds[i].getY() + getPosY() + itemBounds[i].getHeight() * 4 / 5));
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

            double lineStartX;
            double lineStartY;
            double lineEndX;
            double lineEndY;
            if (isNodeVertical) {
                lineStartX = getPosX() - PADDING * 3 / 4;
                lineStartY = getPosY() + getHeight() / 2;
                lineEndX = parent.getPosX() + parent.getWidth() + PADDING * 3 / 4;
                lineEndY = parent.getPosY() + parent.getHeight() / 2;
            } else {
                lineStartX = getPosX() + getWidth() / 2;
                lineStartY = getPosY() - PADDING * 3 / 4;
                lineEndX = parent.getPosX() + parent.getWidth() / 2;
                lineEndY = parent.getPosY() + parent.getHeight() + PADDING * 3 / 4;
            }

            graphics2D.setStroke(LINE_STROKE);
            graphics2D.setColor(LINE_COLOR);
            graphics2D.drawLine((int) lineStartX, (int) lineStartY, (int) lineEndX, (int) lineEndY);
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
        private void initialiseItems(BNode<?> node) {
            Object[] items = node.items;
            // Find the number of non-null values
            int numNonNull = 0;
            // Since null values are always towards the end of the array, we iterate until we find a null value
            for (Object item : items) {
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
