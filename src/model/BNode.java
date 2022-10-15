package model;

import java.util.Arrays;

/**
 * Implementation of a node in a B Tree
 * Taken from <a href="https://gist.github.com/adderllyer/3bfa2d04200386b5664c">...</a>
 * Google is best
 */
public class BNode<T> extends Node<T> {

    int numKeys = 0;
    int[] keys;
    T[] items;
    boolean isLeaf;
    // neighbours is childNodes;

    public BNode(int numNeighbours) {
        // The "item" attribute in the node superclass is not used as the implementation requires an array of T items
        super(null, numNeighbours);
        keys = new int[numNeighbours - 1];
        items = (T[]) new Object[numNeighbours - 1];
    }

    /**
     * Returns the array of items contained in the node.
     *
     * @return an array of generic type items
     */
    public T[] getItems() {
        return items;
    }

    /**
     * Returns the child nodes of the B Node.
     *
     * @return a Node array of children.
     */
    public Node<?>[] getNeighbours() {
        return neighbours;
    }

    public int binarySearch(int key) {
        int left = 0, right = numKeys - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (keys[mid] < key) left = mid + 1;
            else if (keys[mid] > key) right = mid - 1;
            else return mid;
        }
        return -1;
    }

    /**
     * Returns whether a key exists in the node.
     * Uses binary search
     *
     * @param key the key to be searched
     * @return boolean of whether the key exists
     */
    public boolean containsKey(int key) {
        return binarySearch(key) != 1;
    }

    public void remove(int id, int shift) { // left is 0, right is 1
        if (id >= 0) {
            int i;
            for (i = id; i < numKeys; ++i) {
                keys[i] = keys[i + 1];
                items[i] = items[i + 1];
                if (!isLeaf) {
                    if (i >= id + shift) neighbours[i] = neighbours[i + 1];
                }
            }
            keys[i] = 0;
            if (!isLeaf) {
                if (i >= id + shift) neighbours[i] = neighbours[i + 1];
                neighbours[i + 1] = null;
            }
            numKeys--;
        }
    }

    public void shiftRight() {
        if (!isLeaf) neighbours[numKeys + 1] = neighbours[numKeys];
        for (int i = numKeys - 1; i >= 0; --i) {
            keys[i + 1] = keys[i];
            items[i + 1] = items[i];
            if (!isLeaf) neighbours[i + 1] = neighbours[i];
        }
    }

    public int subtreeRootNodeIndex(int key) {
        for (int i = 0; i < numKeys; ++i) {
            if (key < keys[i]) return i;
        }
        return numKeys;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    /**
     * Auxiliary private method used in toString method to get the string representation of the subtree with the
     * node object as the root node
     *
     * @param buffer         the StringBuffer used
     * @param prefix         prefix for current node
     * @param childrenPrefix prefix for child nodes
     */
    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(Arrays.asList(items));
        buffer.append('\n');

        for (Node node : neighbours) {
            if (node != null) {
                if (node != neighbours[numKeys - 1]) {
                    ((BNode<?>) node).print(buffer, childrenPrefix + "+--- ", childrenPrefix + "|   ");
                } else {
                    ((BNode<?>) node).print(buffer, childrenPrefix + "L___ ", childrenPrefix + "    ");
                }
            }
        }
    }

}