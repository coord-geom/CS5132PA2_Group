public class MLMNode extends Node{


    public MLMNode(Object item) {
        super(item);
    }

    public MLMNode(Object item, int numNeighbours) {
        super(item, numNeighbours);
    }

    public MLMNode(Object item, Node[] neighbours) {
        super(item, neighbours);
    }

    public MLMNode(Node n) {
        super(n);
    }
}
