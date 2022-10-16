package model;

import java.util.Arrays;

public class BNode<T extends Comparable<? super T>> extends Node<T>{
    public int numItems = 0;
    public T[] items;
    public BNode[] neighbours;
    public boolean isLeaf;

    public BNode(int maxChildren){
        super(null, maxChildren);
        items = (T[]) new Comparable[maxChildren-1];
        neighbours = new BNode[maxChildren];
    }

    int binarySearch(T item) {
        if(item == null) return -1;
        int leftIndex = 0;
        int rightIndex = numItems - 1;
        while (leftIndex <= rightIndex) {
            final int middleIndex = leftIndex + ((rightIndex - leftIndex) / 2);
            if(items[middleIndex] == null) return -1;
            if (items[middleIndex].compareTo(item) < 0) {
                leftIndex = middleIndex + 1;
            } else if (items[middleIndex].compareTo(item) > 0) {
                rightIndex = middleIndex - 1;
            } else {
                return middleIndex;
            }
        }

        return -1;
    }

    /**
     * Returns whether an item exists in the node.
     * Uses binary search
     *
     * @param item the item to be searched
     * @return boolean of whether the item exists
     */

    boolean contains(T item) {
        return binarySearch(item) != -1;
    }

    /**
     * Remove an element from a node and also the left (0) or right (+1) child.
     * @param index the index of the item to be removed
     * @param leftOrRightChild to determine whether to remove the left or right child
     */
    void remove(int index, int leftOrRightChild) {
        if (index >= 0) {
            int i;
            for (i = index; i < numItems - 1; i++) {
                items[i] = items[i + 1];
                if (!isLeaf) {
                    if (i >= index + leftOrRightChild) {
                        neighbours[i] = neighbours[i + 1];
                    }
                }
            }
            items[i] = null;
            if (!isLeaf) {
                if (i >= index + leftOrRightChild) {
                    neighbours[i] = neighbours[i + 1];
                }
                neighbours[i + 1] = null;
            }
            numItems--;
        }
    }

    /**
     * Shifts all the items to the right by one position,
     * leaving a space at index 0 to add another item
     */
    void shiftRightByOne() {
        if (!isLeaf) {
            neighbours[numItems + 1] = neighbours[numItems];
        }
        for (int i = numItems - 1; i >= 0; i--) {
            items[i + 1] = items[i];
            if (!isLeaf) {
                neighbours[i + 1] = neighbours[i];
            }
        }
    }

    /**
     *
     * @param item
     * @return the index of the smallest item greater than item
     */
    int subtreeRootNodeIndex(T item) {
        if(item == null) return 0;
        for (int i = 0; i < numItems; i++) {
            if(items[i] == null) return i;
            if (item.compareTo(items[i]) <= 0) {
                return i;
            }
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

        for (BNode node : neighbours) {
            if (node != null) {
                if (numItems != 0 && node != neighbours[numItems]) {
                    node.print(buffer, childrenPrefix + "+--- ", childrenPrefix + "|   ");
                } else {
                    node.print(buffer, childrenPrefix + "L___ ", childrenPrefix + "    ");
                }
            }
        }
    }
}