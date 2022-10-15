package model.tests;

import model.RawBTree;

public class RawBTreeTester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
        RawBTree tree = new RawBTree();

        for(int i=0;i<=25;++i){
            tree.add(i+26,(char)((int)'a'+i));
            tree.add(i,(char)((int)'A'+i));
        }

        System.out.println(tree);

        for(int i=13;i<=38;++i){
            tree.delete((i));
            System.out.println(tree);
        }


        return true;
    }
}
