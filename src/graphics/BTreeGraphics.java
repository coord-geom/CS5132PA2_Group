package graphics;

import model.BNode_;
import model.BTree_;
import model.Node;

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
    private ArrayList<NodeGraphics> getNodeGraphics() {
        // Step 1:
        // Iterate level-order through the tree using breadth-first iteration
        // Tracks down nodes as well as their children.

        int height = tree().getHeight();

        // List to record down level order nodes
        ArrayList<ArrayList<BNode_<?>>> levelsNodes = new ArrayList<>(height);
        for (int i = 0; i < height; i++)
            levelsNodes.add(new ArrayList<>());

        // List to record down parent indices (index of the list on the level above the child node) of level order nodes
        ArrayList<ArrayList<Integer>> levelsParent = new ArrayList<>(height);
        for (int i = 0; i < height; i++)
            levelsParent.add(new ArrayList<>());

        // Stack to keep track of next nodes to iterate.
        ArrayList<BNode_<?>> nodesIterationStack = new ArrayList<>();
        // Stack to keep track of the levels of the nodes in the iteration stack.
        ArrayList<Integer> levelsIterationStack = new ArrayList<>();
        // Stack to keep track of the indices of the parents of the nodes in the iteration stack.
        ArrayList<Integer> parentsIterationStack = new ArrayList<>();
        // Start from root
        nodesIterationStack.add(tree().getRootNode());
        levelsIterationStack.add(0);
        parentsIterationStack.add(-1);

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
            currIndex = levelsNodes.get(currLevel).size() - 1;
            if (currLevel != 0)
                levelsParent.get(currLevel - 1).add(currParentIndex);
            levelsNodes.get(currLevel).add(currNode);

            // If the node is not a leaf, add more iterable child nodes and relevant data
            if (!currNode.isLeaf()) {
                for (BNode_<?> node: currNode.getNeighbours()) {
                    nodesIterationStack.add(node);
                    levelsIterationStack.add(currLevel);
                    parentsIterationStack.add(currIndex);
                }
            }
        }

        // Step 2:
        // Turning data into graphics classes

        // Records into a single list
        ArrayList<NodeGraphics> nodeGraphics = new ArrayList<>();

        // Records into level separated lists for easy retrieval of parent nodes.
        ArrayList<ArrayList<NodeGraphics>> levelsNodeGraphics = new ArrayList<>();
        for (int i = 0; i < height; i++)
            levelsNodeGraphics.add(new ArrayList<>());

        // Iterate through all nodes and create node graphics
        NodeGraphics parentNode;
        NodeGraphics newNodeGraphics;
        for (int level = 0; level < height; level++) {
            int x = 0;  //TODO
            for (int i = 0; i < levelsNodes.get(level).size(); i++) {
                // Adding parent NodeGraphic objects to current node classes is possible
                // as the recording lists maintain the property that the parent nodes are always before child nodes
                // due to the breadth-first iteration.
                if (level != 0)
                    parentNode = levelsNodeGraphics.get(level - 1).get(levelsParent.get(level).get(i));
                else
                    parentNode = null;
                newNodeGraphics = new NodeGraphics(x, level * 100, levelsNodes.get(level).get(i), parentNode);
                levelsNodeGraphics.get(level).add(newNodeGraphics);
                nodeGraphics.add(newNodeGraphics);
                x += 300;
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
         * Will not contain any null values
         */
        private String[] items;

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
        private NodeGraphics parent;

        /**
         * Constructor
         *
         * @param posX              the x position of the graphic
         * @param posY              the y position of the graphic
         * @param node              the node of the B Tree with relevant data
         * @param parentNodeGraphic the parent node, null if does not exist
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
