package model.tests;

import model.BTree;

public class BTTester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
        BTree<Integer> tree = new BTree<>(2);

        for(int i=0;i<=25;++i){
            tree.add(i+26);
            tree.add(i);
            tree.add(i);
        }

        System.out.println(tree);

        for(int i=13;i<=38;++i){
            tree.delete(i);
        }

        System.out.println(tree);
        return true;
    }
}
