package model;

import java.util.ArrayList;

/**
 * Implementation of a B Tree
 * <br>
 * Utilises the model.BNode_ class
 * <br>
 * <em>Taken and modified from <a href="https://gist.github.com/adderllyer/3bfa2d04200386b5664c">here</a></em>
 * <br>
 * <em>Google is best</em>
 */
public class BTree_<T extends Comparable<? super T>> {

    /**
     * The root node of the tree
     */
    private BNode_<T> root = null;
    private static final int LEFT_NODE = 0;
    private static final int RIGHT_NODE = 1;

    /**
     * The minimum number of children a node in the tree can have
     */
    private final int minChildren;

    /**
     * Constructor
     * @param minChildren the minimum number of children a node can have
     */
    public BTree_(int minChildren) {
        this.minChildren = minChildren;
        root = new BNode_<>(2 * minChildren);
        root.isLeaf = true;
    }

    /**
     * Returns the root node
     * @return the root node
     */
    public BNode_<T> getRootNode() {
        return this.root;
    }

    /**
     * Returns the height of the whole tree.
     * @return the height of the tree.
     */
    public int getHeight() {
        return getHeight(root);
    }

    /**
     * Returns the height of the subtree with the given root.
     * @param node the root of the subtree.
     * @return the height of the tree.
     */
    public int getHeight(BNode_<T> node) {
        int height = 0;
        // Iterates down the tree until a leaf node is reached.
        // Property of B Trees ensures the first child node always exists given a node is not a root.
        BNode_<T> iteratingNode = node;
        while (!iteratingNode.isLeaf()) {
            iteratingNode = (BNode_<T>) iteratingNode.neighbours[0];
            height++;
        }
        return height;
    }

    public void add(T item) {
        BNode_<T> root_ = root;
        if (root_.numItems == 2 * minChildren - 1) {
            root = new BNode_<>(2 * minChildren);
            root.isLeaf = false;
            root.neighbours[0] = root_;
            splitNodes(root, 0, root_);
            insertNode(root, item);
        } else insertNode(root_, item);
    }

    /**
     * Splits a given child node and places the median child node (which moves up one level)
     * in the given parent node at a specified index i
     * @param parent the given parent node
     * @param i the specified index i
     * @param child the given child node
     */
    //    [,A,B,]          [,A,c,B,]
    //       |       ->      /   \
    // [,a,b,c,d,e,]    [,a,b,] [,d,e,]
    private void splitNodes(BNode_<T> parent, int i, BNode_<T> child) {
        // Create new child node
        // -> [,a,b,c,d,e,] & []
        BNode_<T> newNode = new BNode_<>(2 * minChildren);
        newNode.isLeaf = child.isLeaf;
        newNode.numItems = minChildren - 1;

        // Copy half of child node into new node
        // -> [,a,b,c,d,e,] & [.d.e.]
        for (int j = 0; j < minChildren - 1; ++j)
            newNode.items[j] = child.items[j + minChildren];

        // If the split nodes are inner nodes, copy over children as well.
        if (!newNode.isLeaf()) {
            // -> [,a,b,c,d,e,] & [,d,e,]
            for (int j = 0; j < minChildren; ++j)
                newNode.neighbours[j] = child.neighbours[j + minChildren];
            // -> [,a,b,c.d.e.] & [,d,e,]
            for (int j = minChildren; j <= child.numItems; ++j)
                child.neighbours[j] = null;
        }

        // Removes the duplicate data
        // -> [,a,b,c.] & [,d,e,]
        for (int j = minChildren; j < child.numItems; ++j)
            child.items[j] = null;
        child.numItems = minChildren - 1;

        // Makes space in the parent node for median item from child node
        //        [,A,-.B,]
        // ->       /
        //    [,a,b,c.] [,d,e,]
        for (int j = parent.numItems; j >= i + 1; --j) {
            parent.neighbours[j + 1] = parent.neighbours[j];
            parent.items[j] = parent.items[j - 1];
        }

        // Adds new node and new item
        //       [,A,c,B,]
        // ->      /   \
        //    [,a,b,] [,d,e,]
        parent.neighbours[i + 1] = newNode;
        parent.items[i] = child.items[minChildren - 1];
        child.items[minChildren - 1] = null;
        ++parent.numItems;
    }

    /**
     * Inserts a key-item pair into a subtree given the subtree's root node
     * @param node the subtree's root node
     * @param item
     */
    private void insertNode(BNode_<T> node, T item) {
        int i = node.numItems - 1;
        //
        if (node.isLeaf()) {
            while (i >= 0 && item.compareTo(node.items[i]) < 0) {
                node.items[i + 1] = node.items[i];
                i--;
            }
            ++i;
            node.items[i] = item;
            node.numItems++;
        } else {
            while (i >= 0 && item.compareTo(node.items[i]) < 0) i--;
            ++i;
            if (((BNode_) node.neighbours[i]).numItems == 2 * minChildren - 1) {
                splitNodes(node, i, (BNode_) node.neighbours[i]);
                if (item.compareTo(node.items[i]) > 0) ++i;
            }
            insertNode((BNode_) node.neighbours[i], item);
        }
    }

    public void delete(T item) {
        delete(root, item);
    }

    private void delete(BNode_<T> node, T item) {
        int i = node.binarySearch(item);
        if (node.isLeaf()) {
            if (i != -1) node.remove(i, LEFT_NODE);
        } else {
            if (i != -1) {
                BNode_<T> left = (BNode_<T>) node.neighbours[i];
                BNode_<T> right = (BNode_<T>) node.neighbours[i + 1];
                if (left.numItems >= minChildren) {
                    BNode_<T> preNode = left;
                    BNode_<T> toDelete = preNode;
                    while (!preNode.isLeaf) {
                        toDelete = preNode;
                        preNode = (BNode_<T>) preNode.neighbours[node.numItems - 1];
                    }
                    node.items[i] = preNode.items[preNode.numItems - 1];
                    delete(toDelete, node.items[i]);
                } else if (right.numItems >= minChildren) {
                    BNode_<T> sucNode = right;
                    BNode_<T> toDelete = sucNode;
                    while (!sucNode.isLeaf) {
                        toDelete = sucNode;
                        sucNode = (BNode_<T>) sucNode.neighbours[0];
                    }
                    node.items[i] = sucNode.items[0];
                    delete(toDelete, node.items[i]);
                } else {
                    int medianId = mergeNodes(left, right);
                    moveKey(node, i, RIGHT_NODE, left, medianId);
                    delete(left, item);
                }
            } else {
                i = node.subtreeRootNodeIndex(item);
                BNode_<T> child = (BNode_<T>) node.neighbours[i];
                if (child.numItems == minChildren - 1) {
                    BNode_<T> childLeft = (i - 1 >= 0) ? (BNode_<T>) node.neighbours[i - 1] : null;
                    BNode_<T> childRight = (i + 1 <= node.numItems) ? (BNode_<T>) node.neighbours[i + 1] : null;
                    if (childLeft != null && childLeft.numItems >= minChildren) {
                        child.shiftRight();
                        child.items[0] = node.items[i - 1];
                        if (!child.isLeaf()) child.neighbours[0] = childLeft.neighbours[i - 1];
                        ++child.numItems;

                        node.items[i - 1] = childLeft.items[childLeft.numItems - 1];
                        childLeft.remove(childLeft.numItems - 1, RIGHT_NODE);
                    } else if (childRight != null && childRight.numItems >= minChildren) {
                        child.items[childRight.numItems] = node.items[i];
                        if (!child.isLeaf) child.neighbours[childRight.numItems+1] = childRight.neighbours[0];
                        ++child.numItems;
                        node.items[i] = childRight.items[0];
                        childRight.remove(0, LEFT_NODE);
                    } else {
                        if (childLeft != null) {
                            int medianId = mergeNodes(child, childLeft);
                            moveKey(node, i - 1, LEFT_NODE, child, medianId);
                        } else if (childRight != null) {
                            int medianId = mergeNodes(child, childRight);
                            moveKey(node, i, RIGHT_NODE, child, medianId);
                        }
                    }
                }
                delete(child, item);
            }
        }
    }

    private int mergeNodes(BNode_<T> target, BNode_<T> source) {
        int medianId;
        if (source.items[0].compareTo(target.items[target.numItems - 1]) < 0) {
            if (!target.isLeaf)
                target.neighbours[source.numItems + target.numItems + 1] = target.neighbours[target.numItems];
            for (int i = target.numItems; i > 0; --i) {
                target.items[source.numItems + i] = target.items[i - 1];
                if (!target.isLeaf)
                    target.neighbours[source.numItems + i] = target.neighbours[i - 1];
            }
            medianId = source.numItems;
            target.items[medianId] = null;
            for (int i = 0; i < source.numItems; ++i) {
                target.items[i] = source.items[i];
                if (!source.isLeaf) target.neighbours[i] = source.neighbours[i];
            }
            if (!source.isLeaf) target.neighbours[source.numItems] = source.neighbours[source.numItems];
        } else {
            medianId = target.numItems;
            target.items[medianId] = null;
            int offset = medianId + 1;
            for (int i = 0; i < source.numItems; ++i) {
                target.items[offset + i] = source.items[i];
                if (!source.isLeaf) target.neighbours[offset + i] = source.neighbours[offset + i];
            }
            if (!source.isLeaf) target.neighbours[offset + source.numItems] = source.neighbours[source.numItems];
        }
        target.numItems += source.numItems;
        return medianId;
    }

    private void moveKey(BNode_<T> source, int sourceId, int childId, BNode_<T> target, int medianId) {
        target.items[medianId] = source.items[sourceId];
        ++target.numItems;

        source.remove(sourceId, childId);
        if (source == root && source.numItems == 0) root = target;
    }

    /**
     * Searches for an item in the whole tree and given the associated key
     * @param item the associated key of the item.
     * @return an item
     */
    public T search(T item) {
        return search(root, item);
    }

    /**
     * Searches for an item in the suBTree_ and given the associated key, starting from the given root node.
     * @param node the root node where the search starts.
     * @param item the associated key of the item.
     * @return an item
     */
    public T search(BNode_<T> node, T item) {
        // finds the index of the key
        int i = 0;
        while (i < node.numItems && item.compareTo(node.items[i]) > 0)
            i++;

        // Check if key is equal, returns the item associated with the key
        if (i < node.numItems && item.equals(node.items[i]))
            return (T) node.neighbours[i].getItem();

        // Recurse down the tree, returns null if already at leaf node
        if (node.isLeaf())
            return null;
        return (T) search((BNode_) node.neighbours[i], item);
    }

    private boolean update(BNode_<T> node, T item) {
        while (node != null) {
            int i = 0;
            while (i < node.numItems && item.compareTo(node.items[i]) > 0) i++;
            if (i < node.numItems && item.equals(node.items[i])) {
                node.items[i] = item;
                return true;
            }
            if (node.isLeaf())
                return false;
            node = (BNode_<T>) node.neighbours[i];
        }
        return false;
    }

    public void clear() {
        root = null;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
