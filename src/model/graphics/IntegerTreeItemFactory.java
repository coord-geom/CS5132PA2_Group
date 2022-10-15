package model.graphics;

import model.BTree;

/**
 * Factory class that creates items and B Trees
 */
public class IntegerTreeItemFactory implements TreeItemFactory<Integer> {

    @Override
    public Integer createItemFromString(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public BTree<?> createEmptyTree(int minChildren) {
        return new BTree<>(minChildren);
    }

}
