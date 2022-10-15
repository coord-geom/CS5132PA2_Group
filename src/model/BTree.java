package model;

import java.util.ArrayList;

/*
 * Unlike a binary search tree, each node of a B-tree may have a variable number of items and children.
 * The items are stored in non-decreasing order. Each node either is a leaf node or
 * it has some associated children that are the root nodes of subtrees.
 * The left child node of a node's element contains all nodes (elements) with items less than or equal to the node element's item
 * but greater than the preceding node element's item.
 * If a node becomes full, a split operation is performed during the insert operation.
 * The split operation transforms a full node with 2*T-1 elements into two nodes with T-1 elements each
 * and moves the median item of the two nodes into its parent node.
 * The elements left of the median (middle) element of the splitted node remain in the original node.
 * The new node becomes the child node immediately to the right of the median element that was moved to the parent node.
 *
 * Example (T = 4):
 * 1.  R = | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
 *
 * 2.  Add item 8
 *
 * 3.  R =         | 4 |
 *                 /   \
 *     | 1 | 2 | 3 |   | 5 | 6 | 7 | 8 |
 *
 */

/**
 * Implementation of a B Tree
 * <br>
 * Utilises the model.BNode_ class
 * <br>
 * <em>Taken and modified from <a href="https://gist.github.com/adderllyer/3bfa2d04200386b5664c">here</a></em>
 * <br>
 * <em>Google is best</em>
 */

public class BTree<T extends Comparable<? super T>> {

    /**
     * The minimum number of children a node in the tree should have
     */
    private int minChildren;

    /**
     * The root node of the tree
     */
    public BNode root;
    private static final int LEFT_CHILD_NODE = 0;
    private static final int RIGHT_CHILD_NODE = 1;

    /**
     * Constructor
     * @param minChildren the minimum number of children a node can have
     */
    public BTree(int minChildren) {
        this.minChildren = minChildren;
        root = new BNode(2*minChildren);
        root.isLeaf = true;
    }

    public int getHeight() { return getHeight(root); }

    public int getHeight(BNode<T> node) {
        int height = 0;
        // Iterates down the tree until a leaf node is reached.
        // Property of B Trees ensures the first child node always exists given a node is not a root.
        BNode<T> iteratingNode = node;
        while (!iteratingNode.isLeaf) {
            iteratingNode = (BNode<T>) iteratingNode.neighbours[0];
            height++;
        }
        return height;
    }

    public void add(T item) {
        BNode rootNode = root;
        if (!update(root, item)) {
            if (rootNode.numItems == (2 * minChildren - 1)) {
                BNode newRootNode = new BNode(2*minChildren);
                root = newRootNode;
                newRootNode.isLeaf = false;
                root.neighbours[0] = rootNode;
                splitChildNode(newRootNode, 0, rootNode); // Split rootNode and move its median (middle) item up into newRootNode.
                insertIntoNonFullNode(newRootNode, item); // Insert the item into the B-Tree with root newRootNode.
            } else {
                insertIntoNonFullNode(rootNode, item); // Insert the item into the B-Tree with root rootNode.
            }
        }
    }

    // Split the node, node, of a B-Tree into two nodes that both contain T-1 elements and move node's median item up to the parentNode.
    // This method will only be called if node is full; node is the i-th child of parentNode.
    /**
     * Splits a given child node and places the median child node (which moves up one level)
     * in the given parent node at a specified index i
     * @param parentNode the given parent node
     * @param i the specified index i
     * @param node the given child node
     */
    //    [,A,B,]          [,A,c,B,]
    //       |       ->      /   \
    // [,a,b,c,d,e,]    [,a,b,] [,d,e,]
    void splitChildNode(BNode parentNode, int i, BNode node) {
        // Create new child node
        // -> [,a,b,c,d,e,] & []
        BNode newNode = new BNode(2*minChildren);
        newNode.isLeaf = node.isLeaf;
        newNode.numItems = minChildren - 1;

        // Copy half of child node into new node
        // -> [,a,b,c,d,e,] & [.d.e.]
        for (int j = 0; j < minChildren - 1; j++) { // Copy the last minChildren-1 elements of node into newNode.
            newNode.items[j] = node.items[j + minChildren];
        }

        // If the split nodes are inner nodes, copy over children as well.
        if (!newNode.isLeaf) {
            // -> [,a,b,c,d,e,] & [,d,e,]
            for (int j = 0; j < minChildren; j++) { // Copy the last minChildren pointers of node into newNode.
                newNode.neighbours[j] = node.neighbours[j + minChildren];
            }
            // -> [,a,b,c.d.e.] & [,d,e,]
            for (int j = minChildren; j <= node.numItems; j++) {
                node.neighbours[j] = null;
            }
        }

        // Removes the duplicate data
        // -> [,a,b,c.] & [,d,e,]
        for (int j = minChildren; j < node.numItems; j++) {
            node.items[j] = null;
        }
        node.numItems = minChildren - 1;

        // Insert a (child) pointer to node newNode into the parentNode, moving other items and pointers as necessary.
        // Makes space in the parent node for median item from child node
        //        [,A,-.B,]
        // ->       /
        //    [,a,b,c.] [,d,e,]
        for (int j = parentNode.numItems; j >= i + 1; j--) {
            parentNode.neighbours[j + 1] = parentNode.neighbours[j];
        }
        parentNode.neighbours[i + 1] = newNode;
        for (int j = parentNode.numItems - 1; j >= i; j--) {
            parentNode.items[j + 1] = parentNode.items[j];
        }

        // Adds new node and new item
        //       [,A,c,B,]
        // ->      /   \
        //    [,a,b,] [,d,e,]
        parentNode.items[i] = node.items[minChildren - 1];
        node.items[minChildren - 1] = null;
        parentNode.numItems++;
    }

    // Insert an element into a B-Tree. (The element will ultimately be inserted into a leaf node).
    /**
     * Inserts an item into a subtree given the subtree's root node
     * @param node the subtree's root node
     * @param item
     */
    void insertIntoNonFullNode(BNode node, T item) {
        int i = node.numItems - 1;
        if (node.isLeaf) {
            // Since node is not a full node insert the new element into its proper place within node.
            while (i >= 0 && item.compareTo((T) node.items[i]) < 0) {
                node.items[i + 1] = node.items[i];
                i--;
            }
            i++;
            node.items[i] = item;
            node.numItems++;
        } else {
            // Move back from the last item of node until we find the child pointer to the node
            // that is the root node of the subtree where the new element should be placed.
            while (i >= 0 && item.compareTo((T) node.items[i]) < 0){
                i--;
            }
            i++;
            if (node.neighbours[i].numItems == (2 * minChildren - 1)) {
                splitChildNode(node, i, node.neighbours[i]);
                if (item.compareTo((T) node.items[i]) > 0) {
                    i++;
                }
            }
            insertIntoNonFullNode(node.neighbours[i], item);
        }
    }

    public void delete(T item) {
        delete(root, item);
    }

    /**
     * Finds the node to be deleted
     *
     * If the item is found in a leaf node, the item is simply removed
     * If the item is found in a non-leaf node, we replace it
     *      with the largest predecessor or smallest successor
     *      and delete that item which is at a leaf node depending
     *      on which child (left or right) has minChildren items or more.
     *      If both have minChildren - 1 nodes,
     *          we will merge them and extract the median to delete
     * If the item is not in the node,
     *      we find the smallest index of the item i larger than it
     *      If the left child has more than minChildren items,
     *
     *
     *
     * @param node
     * @param item
     */
    public void delete(BNode node, T item) {
        if (node.isLeaf) { // 1. If the item is in node and node is a leaf node, then delete the item from node.
            int i;
            if ((i = node.binarySearch(item)) != -1) { // item is i-th item of node if node contains item.
                node.remove(i, LEFT_CHILD_NODE);
            }
        } else {
            int i;
            if ((i = node.binarySearch(item)) != -1) { // 2. If node is an internal node and it contains the item... (item is i-th item of node if node contains item)
                BNode leftChildNode = node.neighbours[i];
                BNode rightChildNode = node.neighbours[i + 1];
                if (leftChildNode.numItems >= minChildren) { // 2a. If the predecessor child node has at least T items...
                    //  E.g. To remove 500, replace with predecessor 499
                    //       then find 499 to delete later
                    //    [1, 2, 500, ...]     -->   [1, 2, 499, ...]
                    //          /                          /
                    //  [3, 4, 5, 6, null]         [3, 4, 5, 6, null]
                    //             \                          \
                    //             ...                        ...
                    //               \                          \
                    //        [..., 499, null] -->       [..., null, null]
                    BNode predecessorNode = leftChildNode;
                    BNode erasureNode = predecessorNode; // Make sure not to delete a item from a node with only T - 1 elements.
                    while (!predecessorNode.isLeaf) { // Therefore only descend to the previous node (erasureNode) of the predecessor node and delete the item using 3.
                        erasureNode = predecessorNode;
                        predecessorNode = predecessorNode.neighbours[predecessorNode.numItems];
                    }
                    node.items[i] = predecessorNode.items[predecessorNode.numItems - 1];
                    delete(erasureNode, (T) node.items[i]);
                } else if (rightChildNode.numItems >= minChildren) { // 2b. If the successor child node has at least minChildren items...
                    //  E.g. To remove 500, replace with successor 501
                    //       then find 501 to delete later
                    //    [1, 2, 500, ...]     -->   [1, 2, 501, ...]
                    //              \                          \
                    //             [600, 601, 602, ...]       [600, 601, 602, ...]
                    //              /                          /
                    //            ...                        ...
                    //            /                          /
                    //          [501, 502, ...] -->       [502, ...]
                    BNode successorNode = rightChildNode;
                    BNode erasureNode = successorNode; // Make sure not to delete a item from a node with only minChildren - 1 elements.
                    while (!successorNode.isLeaf) { // Therefore only descend to the previous node (erasureNode) of the predecessor node and delete the item using 3.
                        erasureNode = successorNode;
                        successorNode = successorNode.neighbours[0];
                    }
                    node.items[i] = successorNode.items[0];
                    delete(erasureNode, (T) node.items[i]);
                } else { // 2c. If both the predecessor and the successor child node have only minChildren - 1 items...
                    // If both of the two child nodes to the left and right of the deleted element have the minimum number of elements,
                    // namely minChildren - 1, they can then be joined into a single node with 2 * minChildren - 2 elements.
                    int medianId = mergeNodes(leftChildNode, rightChildNode);
                    moveitem(node, i, RIGHT_CHILD_NODE, leftChildNode, medianId); // Delete i's right child pointer from node.
                    delete(leftChildNode, item);
                }
            } else { // 3. If the item is not resent in node, descent to the root of the appropriate subtree that must contain item...
                // The method is structured to guarantee that whenever delete is called recursively on node "node", the number of items
                // in node is at least the minimum degree minChildren. Note that this condition requires one more item than the minimum required
                // by usual B-tree conditions. This strengthened condition allows us to delete a item from the tree in one downward pass
                // without having to "back up".
                i = node.subtreeRootNodeIndex(item);
                BNode childNode = node.neighbours[i]; // childNode is i-th child of node.
                if (childNode.numItems == minChildren - 1) {
                    BNode leftChildSibling = (i - 1 >= 0) ? node.neighbours[i - 1] : null;
                    BNode rightChildSibling = (i  + 1 <= node.numItems) ? node.neighbours[i + 1] : null;
                    if (leftChildSibling != null && leftChildSibling.numItems >= minChildren) { // 3a. The left sibling has >= minChildren items...
                        // Move a item from the subtree's root node down into childNode along with the appropriate child pointer.
                        // Therefore, first shift all elements and children of childNode right by 1.
                        childNode.shiftRightByOne();
                        childNode.items[0] = node.items[i - 1]; // i - 1 is the item index in node that is smaller than childNode's smallest item.
                        if (!childNode.isLeaf) {
                            childNode.neighbours[0] = leftChildSibling.neighbours[leftChildSibling.numItems];
                        }
                        childNode.numItems++;

                        // Move a item from the left sibling into the subtree's root node.
                        node.items[i - 1] = leftChildSibling.items[leftChildSibling.numItems - 1];

                        // Remove the item from the left sibling along with its right child node.
                        leftChildSibling.remove(leftChildSibling.numItems - 1, RIGHT_CHILD_NODE);
                    } else if (rightChildSibling != null && rightChildSibling.numItems >= minChildren) { // 3a. The right sibling has >= minChildren items...
                        // Move a item from the subtree's root node down into childNode along with the appropriate child pointer.
                        childNode.items[childNode.numItems] = node.items[i]; // i is the item index in node that is bigger than childNode's biggest item.
                        if (!childNode.isLeaf) {
                            childNode.neighbours[childNode.numItems + 1] = rightChildSibling.neighbours[0];
                        }
                        childNode.numItems++;

                        // Move a item from the right sibling into the subtree's root node.
                        node.items[i] = rightChildSibling.items[0];

                        // Remove the item from the right sibling along with its left child node.
                        rightChildSibling.remove(0, LEFT_CHILD_NODE);
                    } else { // 3b. Both of childNode's siblings have only minChildren - 1 items each...
                        if (leftChildSibling != null) {
                            int medianId = mergeNodes(childNode, leftChildSibling);
                            moveitem(node, i - 1, LEFT_CHILD_NODE, childNode, medianId); // i - 1 is the median item index in node when merging with the left sibling.
                        } else if (rightChildSibling != null) {
                            int medianId = mergeNodes(childNode, rightChildSibling);
                            moveitem(node, i, RIGHT_CHILD_NODE, childNode, medianId); // i is the median item index in node when merging with the right sibling.
                        }
                    }
                }
                delete(childNode, item);
            }
        }
    }

    // Merge two nodes and keep the median item (element) empty.
    int mergeNodes(BNode target, BNode source) {
        int medianId;
        if (source.items[0].compareTo(target.items[target.numItems - 1]) < 0) {
            int i;
            // Shift all elements of target right by source.numItems + 1 to make place for the source and the median item.
            if (!target.isLeaf) {
                target.neighbours[source.numItems + target.numItems + 1] = target.neighbours[target.numItems];
            }
            for (i = target.numItems; i > 0 ; i--) {
                target.items[source.numItems + i] = target.items[i - 1];
                if (!target.isLeaf) {
                    target.neighbours[source.numItems + i] = target.neighbours[i - 1];
                }
            }

            // Clear the median item (element).
            medianId = source.numItems;
            target.items[medianId] = null;

            // Copy the source's elements into target.
            for (i = 0; i < source.numItems; i++) {
                target.items[i] = source.items[i];
                if (!source.isLeaf) {
                    target.neighbours[i] = source.neighbours[i];
                }
            }
            if (!source.isLeaf) {
                target.neighbours[i] = source.neighbours[i];
            }
        } else {
            // Clear the median item (element).
            medianId = target.numItems;
            target.items[medianId] = null;

            // Copy the source's elements into target.
            int offset = medianId + 1;
            int i;
            for (i = 0; i < source.numItems; i++) {
                target.items[offset + i] = source.items[i];
                if (!source.isLeaf) {
                    target.neighbours[offset + i] = source.neighbours[i];
                }
            }
            if (!source.isLeaf) {
                target.neighbours[offset + i] = source.neighbours[i];
            }
        }
        target.numItems += source.numItems;
        return medianId;
    }

    // Move the item from source at index into target at medianId. Note that the element at index is already empty.
    void moveitem(BNode source, int srcitemIndex, int childIndex, BNode target, int medianId) {
        target.items[medianId] = source.items[srcitemIndex];
        target.numItems++;

        source.remove(srcitemIndex, childIndex);

        if (source == root && source.numItems == 0) {
            root = target;
        }
    }

    /**
     * Searches for an item in the whole tree and given the associated item
     * @param item the associated item of the item.
     * @return an item
     */
    public T search(T item) {
        return search(root, item);
    }

    // Recursive search method.
    
    /**
     * Searches for an item in the suBTree_ and given the associated item, starting from the given root node.
     * @param node the root node where the search starts.
     * @param item the associated item of the item.
     * @return an item
     */
    public T search(BNode node, T item) {
        int i = 0;
        while (i < node.numItems && item.compareTo((T) node.items[i]) > 0) {
            i++;
        }
        if (i < node.numItems && item == node.items[i]) {
            return (T) node.items[i];
        }
        if (node.isLeaf) {
            return null;
        } else {
            return search(node.neighbours[i], item);
        }
    }

    public T search2(T item) {
        return search2(root, item);
    }

    // Iterative search method.
    public T search2(BNode node, T item) {
        while (node != null) {
            int i = 0;
            while (i < node.numItems && item.compareTo((T) node.items[i]) > 0) {
                i++;
            }
            if (i < node.numItems && item == node.items[i]) {
                return (T) node.items[i];
            }
            if (node.isLeaf) {
                return null;
            } else {
                node = node.neighbours[i];
            }
        }
        return null;
    }

    private boolean update(BNode node, T item) {
        while (node != null) {
            int i = 0;
            while (i < node.numItems && item.compareTo((T) node.items[i]) > 0) {
                i++;
            }
            if (i < node.numItems && item.equals(node.items[i])) {
                //node.mObjects[i] = object;
                return true;
            }
            if (node.isLeaf) {
                return false;
            } else {
                node = node.neighbours[i];
            }
        }
        return false;
    }

    // Inorder walk over the tree.
    String printBTree(BNode node) {
        String string = "";
        if (node != null) {
            if (node.isLeaf) {
                for (int i = 0; i < node.numItems; i++) {
                    string += node.items[i] + ", ";
                }
            } else {
                int i;
                for (i = 0; i < node.numItems; i++) {
                    string += printBTree(node.neighbours[i]);
                    string += node.items[i] + ", ";
                }
                string += printBTree(node.neighbours[i]);
            }
        }
        return string;
    }

    public String toString() {
        return root.toString();
    }

    void validate() throws Exception {
        ArrayList<T> array = getItems(root);
        for (int i = 0; i < array.size() - 1; i++) {
            if (array.get(i).compareTo(array.get(i + 1)) >= 0) {
                throw new Exception("B-Tree invalid: " + array.get(i)  + " greater than " + array.get(i + 1));
            }
        }
    }

    // Inorder walk over the tree.
    ArrayList<T> getItems(BNode node) {
        ArrayList<T> array = new ArrayList<>();
        if (node != null) {
            if (node.isLeaf) {
                for (int i = 0; i < node.numItems; i++) {
                    array.add((T) node.items[i]);
                }
            } else {
                int i;
                for (i = 0; i < node.numItems; i++) {
                    array.addAll(getItems(node.neighbours[i]));
                    array.add((T) node.items[i]);
                }
                array.addAll(getItems(node.neighbours[i]));
            }
        }
        return array;
    }
}