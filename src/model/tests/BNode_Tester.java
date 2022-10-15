package model.tests;

import model.BTree_;

public class BNode_Tester {
    public static void main(String[] args) {
        BTree_<Integer> btree = new BTree_<>(4);
        for(int i=0;i<=20;++i){
            btree.add(i);
            btree.add(50 - i);
            System.out.println(btree);
            System.out.println(i);
        }
        System.out.println(btree);
    }
    public static boolean test() {
        return true;
    }
}
