package model.tests;

import model.BTree;
import model.BTree_;

public class BTreeTester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
        BTree<Character> tree = new BTree<>(3);

        for(int i=0;i<=25;++i){
            tree.add(i+26,(char)((int)'a'+i));
            tree.add(i,(char)((int)'A'+i));
        }

        for(int i=0;i<=0;++i){
            tree.delete((i+26));

        }

        System.out.println(tree);
        return true;
    }
}
