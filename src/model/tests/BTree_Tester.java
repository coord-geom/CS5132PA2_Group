package model.tests;

import model.BTree;
import model.BTree_;

public class BTree_Tester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
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
        // tree.add(0, -78);
        // tree.add(10, 2);
        System.out.println(tree);
        return true;
    }
}
