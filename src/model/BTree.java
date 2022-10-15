package model;

import java.util.ArrayList;

/**
 * Implementation of a B Tree, where a key-item pair data is stored.
 * The key represents the item's "rank" and is used for comparing and sorting the data.
 * The keys should be unique
 * <br>
 * Utilises the model.BNode class
 * <br>
 * <em>Taken and modified from <a href="https://gist.github.com/adderllyer/3bfa2d04200386b5664c">here</a></em>
 * <br>
 * <em>Google is best</em>
 */
@Deprecated
public class BTree<T> {

    /**
     * The root node of the tree
     */
    private BNode<T> root = null;
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
    public BTree(int minChildren) {
        this.minChildren = minChildren;
        root = new BNode<>(2 * minChildren);
        root.isLeaf = true;
    }

    /**
     * Returns the root node
     * @return the root node
     */
    public BNode<T> getRootNode() {
        return this.root;
    }

    public void add(int key, T item) {
        BNode<T> root_ = root;
        if (!update(root, key, item)) {
            if (root_.numKeys == 2 * minChildren - 1) {
                root = new BNode<>(2 * minChildren);
                root.isLeaf = false;
                root.neighbours[0] = root_;
                splitNodes(root, 0, root_);
                insertNode(root, key, item);
            } else insertNode(root_, key, item);
        }
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
    private void splitNodes(BNode<T> parent, int i, BNode<T> child) {
        // Create new child node
        // -> [,a,b,c,d,e,] & []
        BNode<T> newNode = new BNode<>(2 * minChildren);
        newNode.isLeaf = child.isLeaf;
        newNode.numKeys = minChildren - 1;

        // Copy half of child node into new node
        // -> [,a,b,c,d,e,] & [.d.e.]
        for (int j = 0; j < minChildren - 1; ++j) {
            newNode.keys[j] = child.keys[j + minChildren];
            newNode.items[j] = child.items[j + minChildren];
        }

        // If the split nodes are inner nodes, copy over children as well.
        if (!newNode.isLeaf) {
            // -> [,a,b,c,d,e,] & [,d,e,]
            for (int j = 0; j < minChildren; ++j)
                newNode.neighbours[j] = child.neighbours[j + minChildren];
            // -> [,a,b,c.d.e.] & [,d,e,]
            for (int j = minChildren; j <= child.numKeys; ++j)
                child.neighbours[j] = null;
        }

        // Removes the duplicate data
        // -> [,a,b,c.] & [,d,e,]
        for (int j = minChildren; j < child.numKeys; ++j) {
            child.keys[j] = 0;
            child.items[j] = null;
        }
        child.numKeys = minChildren - 1;

        // Makes space in the parent node for median item from child node
        //        [,A,-.B,]
        // ->       /
        //    [,a,b,c.] [,d,e,]
        for (int j = parent.numKeys; j >= i + 1; --j) {
            parent.neighbours[j + 1] = parent.neighbours[j];
            parent.keys[j] = parent.keys[j - 1];
            parent.items[j] = parent.items[j - 1];
        }

        // Adds new node and new item
        //       [,A,c,B,]
        // ->      /   \
        //    [,a,b,] [,d,e,]
        parent.neighbours[i + 1] = newNode;
        parent.keys[i] = child.keys[minChildren - 1];
        parent.items[i] = child.items[minChildren - 1];
        child.keys[minChildren - 1] = 0;
        child.items[minChildren - 1] = null;
        ++parent.numKeys;
    }

    /**
     * Inserts a key-item pair into a subtree given the subtree's root node
     * @param node the subtree's root node
     * @param key
     * @param item
     */
    private void insertNode(BNode<T> node, int key, T item) {
        int i = node.numKeys - 1;
        //
        if (node.isLeaf) {
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                node.items[i + 1] = node.items[i];
                i--;
            }
            ++i;
            node.keys[i] = key;
            node.items[i] = item;
            node.numKeys++;
        } else {
            while (i >= 0 && key < node.keys[i]) i--;
            ++i;
            if (((BNode) node.neighbours[i]).numKeys == 2 * minChildren - 1) {
                splitNodes(node, i, (BNode) node.neighbours[i]);
                if (key > node.keys[i]) ++i;
            }
            insertNode((BNode) node.neighbours[i], key, item);
        }
    }

    public void delete(int key) {
        delete(root, key);
    }

    private void delete(BNode<T> node, int key) {
        int i = node.binarySearch(key);
        if (node.isLeaf) {
            if (i != -1) node.remove(i, LEFT_NODE);
        } else {
            if (i != -1) {
                BNode<T> left = (BNode<T>) node.neighbours[i];
                BNode<T> right = (BNode<T>) node.neighbours[i + 1];
                if (left.numKeys >= minChildren) {
                    BNode<T> preNode = left;
                    BNode<T> toDelete = preNode;
                    while (!preNode.isLeaf) {
                        toDelete = preNode;
                        preNode = (BNode<T>) preNode.neighbours[node.numKeys - 1];
                    }
                    node.keys[i] = preNode.keys[preNode.numKeys - 1];
                    node.items[i] = preNode.items[preNode.numKeys - 1];
                    delete(toDelete, node.keys[i]);
                } else if (right.numKeys >= minChildren) {
                    BNode<T> sucNode = right;
                    BNode<T> toDelete = sucNode;
                    while (!sucNode.isLeaf) {
                        toDelete = sucNode;
                        sucNode = (BNode<T>) sucNode.neighbours[0];
                    }
                    node.keys[i] = sucNode.keys[0];
                    node.items[i] = sucNode.items[0];
                    delete(toDelete, node.keys[i]);
                } else {
                    int medianId = mergeNodes(left, right);
                    moveKey(node, i, RIGHT_NODE, left, medianId);
                    delete(left, key);
                }
            } else {
                i = node.subtreeRootNodeIndex(key);
                BNode<T> child = (BNode<T>) node.neighbours[i];
                if (child.numKeys == minChildren - 1) {
                    BNode<T> childLeft = (i - 1 >= 0) ? (BNode<T>) node.neighbours[i - 1] : null;
                    BNode<T> childRight = (i + 1 <= node.numKeys) ? (BNode<T>) node.neighbours[i + 1] : null;
                    if (childLeft != null && childLeft.numKeys >= minChildren) {
                        child.shiftRight();
                        child.keys[0] = node.keys[i - 1];
                        child.items[0] = node.items[i - 1];
                        if (!child.isLeaf) child.neighbours[0] = node.neighbours[i - 1];
                        ++child.numKeys;

                        node.keys[i - 1] = childLeft.keys[childLeft.numKeys - 1];
                        node.items[i - 1] = childLeft.items[childLeft.numKeys - 1];
                        childLeft.remove(childLeft.numKeys - 1, RIGHT_NODE);
                    } else if (childRight != null && childRight.numKeys >= minChildren) {
                        child.keys[childRight.numKeys] = node.keys[i];
                        child.items[childRight.numKeys] = node.items[i];
                        if (!child.isLeaf) child.neighbours[childRight.numKeys] = node.neighbours[i];
                        ++child.numKeys;

                        node.keys[i] = childRight.keys[0];
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
                delete(child, key);
            }
        }
    }

    private int mergeNodes(BNode<T> target, BNode<T> source) {
        int medianId;
        if (source.keys[0] < target.keys[target.numKeys - 1]) {
            if (!target.isLeaf)
                target.neighbours[source.numKeys + target.numKeys + 1] = target.neighbours[target.numKeys];
            for (int i = target.numKeys; i > 0; --i) {
                target.keys[source.numKeys + i] = target.keys[i - 1];
                target.items[source.numKeys + i] = target.items[i - 1];
                if (!target.isLeaf)
                    target.neighbours[source.numKeys + i] = target.neighbours[i - 1];
            }
            medianId = source.numKeys;
            target.keys[medianId] = 0;
            target.items[medianId] = null;
            for (int i = 0; i < source.numKeys; ++i) {
                target.keys[i] = source.keys[i];
                target.items[i] = source.items[i];
                if (!source.isLeaf) target.neighbours[i] = source.neighbours[i];
            }
            if (!source.isLeaf) target.neighbours[source.numKeys] = source.neighbours[source.numKeys];
        } else {
            medianId = target.numKeys;
            target.keys[medianId] = 0;
            target.items[medianId] = null;
            int offset = medianId + 1;
            for (int i = 0; i < source.numKeys; ++i) {
                target.keys[offset + i] = source.keys[i];
                target.items[offset + i] = source.items[i];
                if (!source.isLeaf) target.neighbours[offset + i] = source.neighbours[offset + i];
            }
            if (!source.isLeaf) target.neighbours[offset + source.numKeys] = source.neighbours[source.numKeys];
        }
        target.numKeys += source.numKeys;
        return medianId;
    }

    private void moveKey(BNode<T> source, int sourceId, int childId, BNode<T> target, int medianId) {
        target.keys[medianId] = source.keys[sourceId];
        target.items[medianId] = source.items[sourceId];
        ++target.numKeys;

        source.remove(sourceId, childId);
        if (source == root && source.numKeys == 0) root = target;
    }

    /**
     * Searches for an item in the whole tree and given the associated key
     * @param key the associated key of the item.
     * @return an item
     */
    public T search(int key) {
        return search(root, key);
    }

    /**
     * Searches for an item in the subtree and given the associated key, starting from the given root node.
     * @param node the root node where the search starts.
     * @param key the associated key of the item.
     * @return an item
     */
    public T search(BNode<T> node, int key) {
        // finds the index of the key
        int i = 0;
        while (i < node.numKeys && key > node.keys[i])
            i++;

        // Check if key is equal, returns the item associated with the key
        if (i < node.numKeys && key == node.keys[i])
            return (T) node.neighbours[i].getItem();

        // Recurse down the tree, returns null if already at leaf node
        if (node.isLeaf)
            return null;
        return (T) search((BNode) node.neighbours[i], key);
    }

    private boolean update(BNode<T> node, int key, T item) {
        while (node != null) {
            int i = 0;
            while (i < node.numKeys && key > node.keys[i]) i++;
            if (i < node.numKeys && key == node.keys[i]) {
                node.items[i] = item;
                return true;
            }
            if (node.isLeaf)
                return false;
            node = (BNode<T>) node.neighbours[i];
        }
        return false;
    }

    public void clear() {
        root = null;
    }

    @Override
    public String toString() {
        // return printTree(root);
        return root.toString();
    }
}
