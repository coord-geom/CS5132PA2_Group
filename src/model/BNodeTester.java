package model;

//TODO make actual content
public class BNodeTester {
    public static void main(String[] args) {
        BTree<Character> btree = new BTree<>(4);
        for(int i=8;i>=0;--i){
            btree.add(i,(char)((int)'A'+i));
            System.out.println(btree);
        }
        //System.out.println(btree);
    }
}
