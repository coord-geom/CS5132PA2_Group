package model.tests;

import model.BTree;
import model.BTree_;

public class BTree_Tester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        BTree_<Character> tree = testBasicInsertion();
        System.out.println(tree);
        System.out.println(tree.getHeight()); // should be 1
    }
    public static BTree_<Character> testBasicInsertion() {
        BTree_<Character> tree = new BTree_<>(3);

        for(int i=0;i<=25;++i){
            tree.add((char)((int)'a'+i));
            tree.add((char)((int)'A'+i));
        }

        System.out.println(tree);

        for(int i=0;i<=7;++i){
            tree.delete((char)((int)'a'+i));
            System.out.println(tree);
        }

        return tree;
    }
}
