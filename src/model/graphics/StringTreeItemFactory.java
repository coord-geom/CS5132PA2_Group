package model.graphics;

import model.BTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StringTreeItemFactory implements TreeItemFactory<String> {
    @Override
    public String createItemFromString(String str) {
        return str;
    }

    @Override
    public BTree<String> createEmptyTree(int minChildren) {
        return new BTree<>(3);
    }

    @Override
    public boolean isValidString(String str) {
        return true;
    }

    /**
     * Reads a file with strings separated by newline characters and returns a tree.
     * @param fileName the name of the file
     * @return tree
     */
    public BTree<String> createTreeFromFile(String fileName) {
        BTree<String> tree = createEmptyTree(3);

        try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                tree.add(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return tree;
    }
}
