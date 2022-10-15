package model.graphics;

import model.BTree_;

/**
 * Factory class that creates items and B Trees
 */
public class IntegerTreeItemFactory implements TreeItemFactory<Integer> {

    @Override
    public Integer createItemFromString(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public BTree_<Integer> createEmptyTree(int minChildren) {
        return new BTree_<>(minChildren);
    }

}
