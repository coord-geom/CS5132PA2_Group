package model.graphics;

import model.BTree_;

/**
 * Factory interface used by the graphics classes to create items and B Trees from user input string values,
 * as well as line-by-line file read strings.
 * Takes in a generic type which is the type of the items in the B Tree
 * <br>
 * <b><em>Generic type for items implemented must have an implemented toString() for the displayed string representation,
 * as well as an equals() method for checking of values for deletion from the tree.
 * <br>
 * implementing the Comparable interface is also required</b></em>
 */
public interface TreeItemFactory<T extends Comparable<? super T>> {

    /**
     * Create a value from a string input
     * @param str the string
     */
    T createItemFromString(String str);

    /**
     * Create an empty B Tree object with given minimum number of children
     * @param minChildren the minimum number of children
     * @return a new empty B Tree
     */
    BTree_<T> createEmptyTree(int minChildren);

}
