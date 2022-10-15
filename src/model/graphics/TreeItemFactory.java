package model.graphics;

import model.BTree;

/**
 * Factory interface used by the graphics classes to create items and B Trees from user input string values,
 * as well as line-by-line file read strings.
 * <br>
 * <b><em>Generic type for items implemented must have an implemented toString() for the displayed string representation,
 * as well as an equals() method for checking of values for deletion from the tree.</b></em>
 */
public interface TreeItemFactory<T> {

    /**
     * Create a value from a string input
     * @param str the string
     */
    T createItemFromString(String str);

    /**
     * Create an empty B Tree object with given order
     * @param order the order of the B Tree
     * @return a new empty B Tree
     */
    BTree<T> createEmptyTree(int order);

}
