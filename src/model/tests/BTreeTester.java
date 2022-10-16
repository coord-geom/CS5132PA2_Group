package model.tests;

import model.BTree;

import java.util.Random;

public class BTreeTester {
    public static void main(String[] args) {
        //System.out.println("Basic Insertion Test");
        //testBasicInsertion();
        //testcase1();
        //testcase2();
        testcase3();
    }
    public static boolean testBasicInsertion() {
        BTree<Integer> tree = new BTree<>(3);

        for(int i=0;i<=25;++i){
            tree.add(i+26);
            tree.add(i);
        }

        System.out.println(tree);

        for(int i=13;i<=38;++i){
            tree.delete(i);
        }

        System.out.println(tree);
        return true;
    }

    public static void testcase1(){
        BTree<Integer> tree;

        for(int j=3;j<=15;++j) {
            tree = new BTree<>(j);

            for (int i = 0; i <= 1000000; ++i) {
                tree.add(i);
            }

            for (int i = 0; i <= 1000000; ++i) {
                tree.delete(i);
            }

            System.out.println(tree);
        }
    }

    public static void testcase2(){
        BTree<Integer> tree;

        for(int j=3;j<=15;++j) {
            tree = new BTree<>(j);

            for (int i = 0; i <= 100; ++i) {
                tree.add(100-i);
            }

            System.out.println(tree.getItems(tree.root));
        }
    }
    public static void testcase3(){
        BTree<Integer> tree = new BTree<>(7);
        Random random = new Random();
        for(int j = 0; j < 10; ++j) {
            for (int i = 0; i <= 300; ++i) {
                tree.add(random.nextInt(10000));
                tree.add(random.nextInt(10000) + 10000);
                tree.add(random.nextInt(10000) + 20000);
                tree.add(random.nextInt(10000) + 30000);
            }

            for (int i = 0; i <= 300; ++i) {
                tree.delete(random.nextInt(40000));
            }
        }

        try{tree.validate();} catch(Exception e){e.printStackTrace();}
        System.out.println(tree);
    }
}
