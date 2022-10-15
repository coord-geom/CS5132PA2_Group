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
        tree.add('A');
        tree.add('R');
        tree.add('E');
        tree.add('Y');
        tree.add('O');
        tree.add('U');
        tree.add('G');
        tree.add('I');
        tree.add('M');
        return tree;
    }
}
