package model.graphics;

import data.Entry;
import model.BTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Factory class that creates items and B Trees
 */
public class EntryTreeItemFactory implements TreeItemFactory<Entry> {

    @Override
    public Entry createItemFromString(String str) {
        // split string based on comma, stripping whitespace before and after.
        String[] elements = str.split("^\\s+|\\s*,\\s*|\\s+$");
        // str should be "year, country, gov_left1"
        return new Entry(Integer.parseInt(elements[0]), elements[1], Float.parseFloat(elements[2]));
    }

    @Override
    public BTree<Entry> createEmptyTree(int minChildren) {
        return new BTree<Entry>(minChildren);
    }

    @Override
    public boolean isValidString(String str) {
        try {
            String[] elements = str.split("^\\s+|\\s*,\\s*|\\s+$");
            if (elements.length != 3)
                return false;
            new Entry(Integer.parseInt(elements[0]), elements[1], Float.parseFloat(elements[2]));
        } catch (NumberFormatException | AssertionError e) {
            return false;
        }
        return true;
    }

    public BTree<Entry> createFromFileTree(int minChildren, String filename){
        // mainly intended to instantiate a tree based on CPDS dataset
        if (filename == null) filename = "src/data/CPDS_1960-2019_Update_2021.csv";

        BTree<Entry> tree = createEmptyTree(minChildren);

        String line = "";
        String splitBy = ",";
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            // read headers
            br.readLine();
            int i = 0;
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] entry = line.split(splitBy);    // use comma as separator
                String year = entry[0];
                String country = entry[1];
                String gov_left1 = entry[11];

                // parse only valid entries, it is fine if we drop a few records
                if (!year.isEmpty() && !country.isEmpty() && !gov_left1.isEmpty()) {
                    tree.add(new Entry(Integer.parseInt(year), country, Float.parseFloat(gov_left1)));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return tree;
    }

}
