package model.tests;

import model.BTree;

public class BNodeTester {
    public static void main(String[] args) {
        BTree<Integer> btree = new BTree<>(4);
        for(int i=0;i<=20;++i){
            btree.add(i,i);
            btree.add(50-i,50-i);
            //System.out.println(btree);
            //System.out.println(i);
        }
        System.out.println(btree);
    }
    public static boolean test() {
        return true;
    }
}
