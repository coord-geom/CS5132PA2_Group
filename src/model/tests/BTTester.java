package model.tests;

import model.BTree;

public class BTTester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
        BTree<Character> tree = new BTree<>(4);

        for(int i=0;i<=25;++i){
            tree.add((char)('a'+i+26));
            tree.add((char)('a'+i));
        }

        System.out.println(tree);

        for(int i=13;i<=38;++i){
            tree.delete((char)('a'+i));
            System.out.println(tree);
        }


        return true;
    }
}
