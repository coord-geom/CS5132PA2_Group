import java.util.Arrays;
import java.util.Iterator;

public class PNode<T> extends Node<T> {
    public PNode(T item) {
        super(item);
    }

    public PNode(T item, int numNeighbours) {
        super(item, numNeighbours);
    }

    public PNode(T item, Node<T>[] neighbours) {
        super(item, neighbours);
    }

    public PNode(Node<T> n) {
        super(n);
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
            PNode<T> node = (PNode<T>) it.next();
            if (node != null){
                if (it.hasNext()){
                    node.print(buffer, childrenPrefix + "+--- ", childrenPrefix + "|   ");
                } else {
                    node.print(buffer, childrenPrefix + "L___ ", childrenPrefix + "    ");
                }
            }
        }
    }
}
