package model.graphics;

import model.BTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Factory class that creates items and B Trees
 */
public class IntegerTreeItemFactory implements TreeItemFactory<Integer> {

    @Override
    public Integer createItemFromString(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public BTree<Integer> createEmptyTree(int minChildren) {
        return new BTree<>(minChildren);
    }

    @Override
    public boolean isValidString(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Reads a file with integers separated by newline characters and returns a tree.
     * @param fileName the name of the file
     * @return tree
     */
    public BTree<Integer> createTreeFromFile(String fileName) {
        BTree<Integer> tree = createEmptyTree(3);

        try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                tree.add(Integer.parseInt(line));
            }
        } catch (IOException|NumberFormatException e) {
            e.printStackTrace();
        }
        return tree;
    }


}
