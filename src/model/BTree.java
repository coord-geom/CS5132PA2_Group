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
        // Copy the last minChildren-1 elements of node into newNode.
        if (minChildren - 1 >= 0) System.arraycopy(node.items, minChildren, newNode.items, 0, minChildren - 1);


        // If the split nodes are inner nodes, copy over children as well.
        if (!newNode.isLeaf) {
            // -> [,a,b,c,d,e,] & [,d,e,]
            // Copy the last minChildren pointers of node into newNode.
            if (minChildren >= 0) System.arraycopy(node.neighbours, minChildren, newNode.neighbours, 0, minChildren);
            // -> [,a,b,c.d.e.] & [,d,e,]
            for (int j = minChildren; j <= node.numItems; j++) node.neighbours[j] = null;

        }

        // Removes the duplicate data
        // -> [,a,b,c.] & [,d,e,]
        for (int j = minChildren; j < node.numItems; j++) node.items[j] = null;

        node.numItems = minChildren - 1;

        // Insert a (child) pointer to node newNode into the parentNode, moving other items and pointers as necessary.
        // Makes space in the parent node for median item from child node
        //        [,A,-.B,]
        // ->       /
        //    [,a,b,c.] [,d,e,]
        if (parentNode.numItems + 1 - (i + 1) >= 0) System.arraycopy(parentNode.neighbours, i + 1, parentNode.neighbours, i + 1 + 1, parentNode.numItems + 1 - (i + 1));
        parentNode.neighbours[i + 1] = newNode;
        if (parentNode.numItems - i >= 0) System.arraycopy(parentNode.items, i, parentNode.items, i + 1, parentNode.numItems - i);

        // Adds new node and new item
        //       [,A,c,B,]
        // ->      /   \
        //    [,a,b,] [,d,e,]
        parentNode.items[i] = node.items[minChildren - 1];
        node.items[minChildren - 1] = null;
        parentNode.numItems++;
    }

    /**
     * Inserts an item into a subtree given the subtree's root node
     * @param node the subtree's root node
     * @param item the item to be inserted
     */
    void insertIntoNonFullNode(BNode node, T item) {
        int i = node.numItems - 1;
        if (node.isLeaf) {
            // Since node is not a full node insert the new element into its proper place within node.
            // [a, b, c, d, null] --> [a, b, X, c, d]
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
     *      we find the smallest index of the item i larger than it,
     *      and we restructure the nodes so that we can delete the item in one downward pass
     *
     * @param node the current node we are checking
     * @param item the item to delete
     */
    public void delete(BNode node, T item) {
        if (node.isLeaf) {
            // [a, b, X, c, d] --> [a, b, c, d, null]
            int i;
            if ((i = node.binarySearch(item)) != -1) {
                node.remove(i, LEFT_CHILD_NODE);
            }
        } else {
            int i;
            if ((i = node.binarySearch(item)) != -1) {
                BNode leftChildNode = node.neighbours[i];
                BNode rightChildNode = node.neighbours[i + 1];
                if (leftChildNode.numItems >= minChildren) {
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
                    BNode erasureNode = predecessorNode;
                    while (!predecessorNode.isLeaf) {
                        erasureNode = predecessorNode;
                        predecessorNode = predecessorNode.neighbours[predecessorNode.numItems];
                    }
                    node.items[i] = predecessorNode.items[predecessorNode.numItems - 1];
                    delete(erasureNode, (T) node.items[i]);
                } else if (rightChildNode.numItems >= minChildren) {
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
                    BNode erasureNode = successorNode;
                    while (!successorNode.isLeaf) {
                        erasureNode = successorNode;
                        successorNode = successorNode.neighbours[0];
                    }
                    node.items[i] = successorNode.items[0];
                    delete(erasureNode, (T) node.items[i]);
                } else {
                    // E.g.   [12, 30, 42, 55, 78] and minChildren = 2, item = 42
                    //                /  \
                    //               /    \
                    //              /      \
                    //  [33, 37, null...]  [44, 48, null, null, null]
                    //
                    //              becomes
                    //
                    //        [12, 30, 55, 78, null]
                    //                   \
                    //                    \
                    //                     \
                    //           * [33, 37, 42, 44, 48]
                    //
                    // then we run delete on * node...

                    int medianId = mergeNodes(leftChildNode, rightChildNode);
                    moveItem(node, i, RIGHT_CHILD_NODE, leftChildNode, medianId); // Delete i's right child pointer from node.
                    delete(leftChildNode, item);
                }
            } else {
                i = node.subtreeRootNodeIndex(item);
                BNode childNode = node.neighbours[i]; // childNode is i-th child of node.
                if (childNode.numItems == minChildren - 1) {
                    BNode leftChildSibling = (i - 1 >= 0) ? node.neighbours[i - 1] : null;
                    BNode rightChildSibling = (i  + 1 <= node.numItems) ? node.neighbours[i + 1] : null;
                    if (leftChildSibling != null && leftChildSibling.numItems >= minChildren) {
                        // E.g. To delete 51, i=3
                        //
                        //                   [30, 40, 50, 60, null]
                        //                           /  \
                        //                          /    \
                        //    [45, 46, 47, 48, null]   [53, 56, null, null, null]
                        //
                        //          48 goes up, 50 goes down to neighbours[3]
                        //                          becomes
                        //
                        //                   [30, 40, 48, 60, null]
                        //                           /  \
                        //                          /    \
                        //    [45, 46, 47, null, null]   [50, 53, 56, null, null]
                        //
                        //                  then we search neighbours[3]
                        //
                        childNode.shiftRightByOne();
                        childNode.items[0] = node.items[i - 1];
                        if (!childNode.isLeaf)
                            childNode.neighbours[0] = leftChildSibling.neighbours[leftChildSibling.numItems];

                        childNode.numItems++;
                        node.items[i - 1] = leftChildSibling.items[leftChildSibling.numItems - 1];
                        leftChildSibling.remove(leftChildSibling.numItems - 1, RIGHT_CHILD_NODE);
                    } else if (rightChildSibling != null && rightChildSibling.numItems >= minChildren) {
                        // E.g. To delete 49, i=2
                        //
                        //                   [30, 40, 50, 60, null]
                        //                           /  \
                        //                          /    \
                        //    [45, 48, null, null, null]   [53, 55, 57, 59, null]
                        //
                        //           53 goes up, 50 goes down to neighbours[2]
                        //                          becomes
                        //
                        //                   [30, 40, 53, 60, null]
                        //                           /  \
                        //                          /    \
                        //    [45, 48, 50, null, null]   [55, 57, 59, null, null]
                        //
                        //                  then we search neighbours[2]
                        //
                        childNode.items[childNode.numItems] = node.items[i];
                        if (!childNode.isLeaf)
                            childNode.neighbours[childNode.numItems + 1] = rightChildSibling.neighbours[0];
                        childNode.numItems++;
                        node.items[i] = rightChildSibling.items[0];
                        rightChildSibling.remove(0, LEFT_CHILD_NODE);
                    } else {
                        if (leftChildSibling != null) {
                            // E.g. To delete 51, i=3
                            //
                            //                   [30, 40, 50, 60, null]
                            //                           /  \
                            //                          /    \
                            //  [45, 46, null, null, null]   [53, 56, null, null, null]
                            //
                            //          neighbours[2] merges into neighbours[3]
                            //                          becomes
                            //
                            //                   [30, 40, 60, null]
                            //                           \
                            //                            \
                            //                    * [45, 46, 50, 53, 56]
                            //
                            //          then we search * node (now neighbours[2])
                            //
                            int medianId = mergeNodes(childNode, leftChildSibling);
                            moveItem(node, i - 1, LEFT_CHILD_NODE, childNode, medianId);
                        } else if (rightChildSibling != null) {
                            // E.g. To delete 49, i=2
                            //
                            //                   [30, 40, 50, 60, null]
                            //                           /  \
                            //                          /    \
                            //    [45, 48, null, null, null]   [53, 55, null, null, null]
                            //
                            //           neighbours[3] merges into neighbours[2]
                            //                          becomes
                            //
                            //                   [30, 40, 60, null, null]
                            //                           /
                            //                          /
                            //                  [45, 48, 50, 53, 55]
                            //
                            //                  then we search neighbours[2]
                            //
                            int medianId = mergeNodes(childNode, rightChildSibling);
                            moveItem(node, i, RIGHT_CHILD_NODE, childNode, medianId);
                        }
                    }
                }
                // If merging is not an option, just keep looking downwards
                delete(childNode, item);
            }
        }
    }

    /**
     * This function merges two nodes to save space,
     * note that they have the same number of elements
     *
     * @param target the node to be merged into
     * @param source the node to transfer items from
     * @return the median index which is left empty for later
     */
    int mergeNodes(BNode target, BNode source) {
        int medianId;
        if (source.items[0].compareTo(target.items[target.numItems - 1]) < 0) {
            // E.g. source: [1, 2, null, null, null]
            //      target: [4, 5, null, null, null]
            int i;
            if (!target.isLeaf) {
                target.neighbours[source.numItems + target.numItems + 1] = target.neighbours[target.numItems];
            }
            // then target: [4, 5, null, 4, 5]
            for (i = target.numItems; i > 0 ; i--) {
                target.items[source.numItems + i] = target.items[i - 1];
                if (!target.isLeaf) {
                    target.neighbours[source.numItems + i] = target.neighbours[i - 1];
                }
            }

            medianId = source.numItems;
            target.items[medianId] = null;

            // then target: [1, 2, null, 4, 5]
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
            // E.g. source: [4, 5, null, null, null]
            //      target: [1, 2, null, null, null]

            medianId = target.numItems;
            target.items[medianId] = null;

            // then target: [1, 2, null, 4, 5]
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

    /**
     * Moves an item from source (parent node) to the median index of target (child node) which is empty
     *
     * @param source the parent node
     * @param srcitemIndex the index of the item to insert into target
     * @param childIndex the index of the child node
     * @param target the child node
     * @param medianId the median index for the item to be inserted to
     */
    void moveItem(BNode source, int srcitemIndex, int childIndex, BNode target, int medianId) {
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