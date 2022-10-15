package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Implementation of a node in a B Tree
 * Taken from <a href="https://gist.github.com/adderllyer/3bfa2d04200386b5664c">...</a>
 * Google is best
 */
public class BNode_<T extends Comparable<? super T>> extends Node<T> {

    int numItems = 0;
    T[] items;
    boolean isLeaf;
    // neighbours is childNodes;

    public BNode_(int numNeighbours) {
        // The "item" attribute in the node superclass is not used as the implementation requires an array of T items
        super(null, numNeighbours);
        items = (T[]) new Comparable[numNeighbours-1];
    }

    /**
     * Returns the array of items contained in the node.
     * May contain null elements.
     *
     * @return an array of generic type items
     */
    public T[] getItems() {
        return items;
    }

    /**
     * Getter for boolean value stating if the node is a leaf node or not.
     *
     * @return whether the node is a leaf node.
     */
    public boolean isLeaf() {
        return isLeaf;
    }


    /**
     * Returns the child nodes of the B Node.
     * May contain null elements.
     *
     * @return a Node array of children.
     */
    public Node[] getNeighbours() {
        return this.neighbours;
    }

    public int binarySearch(T item) {
        int left = 0, right = numItems - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (items[mid].compareTo(item) < 0) left = mid + 1;
            else if (items[mid].compareTo(item) > 0) right = mid - 1;
            else return mid;
        }
        return -1;
    }

    /**
     * Returns whether a key exists in the node.
     * Uses binary search
     *
     * @param item the item to be searched
     * @return boolean of whether the key exists
     */

    public boolean containsItem(T item) {
        return binarySearch(item) != -1;
    }

    public void remove(int id, int shift) { // left is 0, right is 1
        if (id >= 0) {
            int i;
            for (i = id; i < numItems - 1; ++i) {
                items[i] = items[i + 1];
                if (!isLeaf()) {
                    if (i >= id + shift) neighbours[i] = neighbours[i + 1];
                }
            }
            items[i] = null;
            if (!isLeaf()) {
                if (i >= id + shift) neighbours[i] = neighbours[i + 1];
                neighbours[i + 1] = null;
            }
            numItems--;
        }
    }

    public void shiftRight() {
        if (!isLeaf()) neighbours[numItems + 1] = neighbours[numItems];
        for (int i = numItems - 1; i >= 0; --i) {
            items[i + 1] = items[i];
            if (!isLeaf()) neighbours[i + 1] = neighbours[i];
        }
    }

    public int subtreeRootNodeIndex(T item) {
        for (int i = 0; i < numItems; ++i) {
            if (item.compareTo(items[i]) < 0) return i;
        }
        return numItems;
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
                if (node != neighbours[numItems - 1]) {
                    ((BNode_<?>) node).print(buffer, childrenPrefix + "+--- ", childrenPrefix + "|   ");
                } else {
                    ((BNode_<?>) node).print(buffer, childrenPrefix + "L___ ", childrenPrefix + "    ");
                }
            }
        }
    }
}