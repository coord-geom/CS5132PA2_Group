package model.tests;

import model.RawBTree;

import java.util.Random;

public class RawBTreeTester {
    public static void main(String[] args) {
        System.out.println("Basic Insertion Test");
        testBasicInsertion();
    }
    public static boolean testBasicInsertion() {
        RawBTree tree = new RawBTree();

        for(int i=0;i<=100000;++i){
            tree.add(100000-i, 100000-i);
        }
        Random random = new Random();
        for(int i=0;i<50000;++i){
            tree.delete(random.nextInt(100000));
        }

        System.out.println(tree);


        return true;
    }
}
