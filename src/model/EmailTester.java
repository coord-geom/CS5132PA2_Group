package model;

import data.Entry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EmailTester {
    public static void main(String[] args) {
        BTree<Email> tree = new BTree<>(10);
        String line = "";
        String splitBy = ",";
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src/model/emails.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] entry = line.split(splitBy);    // use comma as separator
                tree.add(new Email(entry[0],entry[1],entry[2],entry[3]));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // The sample email to be searched for
        System.out.println(tree.search(tree.root, new Email("2021-08-09","Seen Ga Poh")));
    }
}
