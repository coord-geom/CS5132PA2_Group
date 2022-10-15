package graphics;

import model.BTree;
import model.graphics.IntegerTreeItemFactory;
import model.graphics.TreeItemFactory;

/**
 * Wrapper class for a B Tree to be used in the BTreeDisplay Canvas.
 */
public class BTreeGraphics {

    /**
     * The B Tree
     */
    private BTree<?> tree;

    /**
     * The factory class used to create new items in the tree.
     */
    private TreeItemFactory<?> treeItemFactory;

    /**
     * Empty constructor, creates an Integer B Tree by default
     */
    public BTreeGraphics() {
        this(new IntegerTreeItemFactory());
    }

    /**
     * Constructor with factory input.
     * @param treeItemFactory the factory used to create the empty tree and items
     */
    public BTreeGraphics(TreeItemFactory<?> treeItemFactory) {
        this(treeItemFactory, treeItemFactory.createEmptyTree(5));  // 5 is the default order of a B Tree
    }

    /**
     * Constructor with factory input as well as tree input.
     * @param treeItemFactory the factory used to create items.
     * @param tree the B Tree.
     */
    public BTreeGraphics(TreeItemFactory<?> treeItemFactory, BTree<?> tree) {
        this.treeItemFactory = treeItemFactory;
        this.tree = tree;
    }

    /**
     * Adds an item into the B Tree using a string representation of the item.
     * @param itemStrRep the string representation of the item
     */
    //TODO
    public void addItem(String itemStrRep) {
    }

    /**
     * Removes an item from the B Tree using a string representation of the item.
     * @param itemStrRep the string representation of the item
     */
    //TODO
    public void removeItem(String itemStrRep) {
    }

}
