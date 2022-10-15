package model.graphics;

import model.BTree;

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
     *
     * @param str the string
     */
    T createItemFromString(String str);

    /**
     * Create an empty B Tree object with given minimum number of children
     *
     * @param minChildren the minimum number of children
     * @return a new empty B Tree
     */
    BTree<?> createEmptyTree(int minChildren);

    /**
     * Checker method that returns whether the string is a valid string representation of the item to be created.
     * @param str the String input
     * @return a boolean that tells whether the string is valid.
     */
    boolean isValidString(String str);

}
