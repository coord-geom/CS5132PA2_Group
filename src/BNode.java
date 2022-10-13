import java.util.Arrays;
import java.util.Iterator;

public class BNode<T extends Comparable<? super T>> extends Node<T> implements Comparable<BNode<T>> {
    // minimum degree
    private int t;
    // current number of nodes
    private int n;
    // boolean keeping track of whether this is a leaf node
    private boolean leaf = true;

    // constructors
    public BNode(T item, int t) {
        super(item, 2 * t - 1);
        this.n = 0;
    }

    public BNode(BNode<T> n) {
        this(n.getItem(), n.t);
        this.t = n.t;
    }

    // function to search for child in subtree rooted at this node
    public BNode<T> search(T item){
        int i;
        for (i = 0; i < neighbours.length; i++){
            int comp = item.compareTo(neighbours[i].getItem());
            if (comp < 0){
                // find the first child greater than item
                break;
            } else if (comp == 0){
                // found the matching node
                return (BNode<T>) neighbours[i];
            }
        }
        if (this.leaf){
            // leaf nodes have no children, itself is not the item -> item does not exist in tree
            return null;
        } else {
            // recurse into subtree with root being first child that was greater than item
            return ((BNode<T>) neighbours[i]).search(item);
        }
    }

    // print method to visualise nodes and all their children
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }
    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(super.getItem());
        buffer.append('\n');

        for (Iterator<Node<T>> it = Arrays.stream(neighbours).iterator(); it.hasNext();){
            BNode<T> node = (BNode<T>) it.next();
            if (node != null){
                if (it.hasNext()){
                    node.print(buffer, childrenPrefix + "+--- ", childrenPrefix + "|   ");
                } else {
                    node.print(buffer, childrenPrefix + "L___ ", childrenPrefix + "    ");
                }
            }
        }
    }

    @Override
    public int compareTo(BNode<T> o) {
        return this.getItem().compareTo(o.getItem());
    }
}
