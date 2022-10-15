package model.tests;

import model.BTree;

public class BTreeTester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
        BTree<Integer> tree = new BTree<>(3);
        tree.add(2, 2);
        tree.add(6, 6);
        tree.add(1, 1);
        tree.add(9, 9);
        tree.add(14, 14);
        tree.add(5, 5);
        tree.add(0, 0);
        tree.add(50, 50);
        tree.add(-3, -3);
        // tree.add(0, -78);
        // tree.add(10, 2);
        System.out.println(tree);
        return true;
    }
}
