public class BNode<T> extends Node<T>{

    /**
     * Implementation is taken from <a href="https://gist.github.com/adderllyer/3bfa2d04200386b5664c">...</a>
     * Google is the best
     */

    int numKeys = 0;
    int[] keys;
    T[] items;
    boolean isLeaf;
    // neighbours is childNodes;

    public BNode(int numNeighbours) {
        super(null, numNeighbours);
        keys = new int[numNeighbours-1];
        items = (T[]) new Object[numNeighbours-1];
    }

    public int binarySearch(int key){
        int left = 0, right = numKeys - 1;
        while(left < right){
            int mid = left + (right-left)/2;
            if(keys[mid] < key) left = mid+1;
            else if(keys[mid] > key) right = mid-1;
            else return mid;
        }
        return -1;
    }

    public boolean containsKey(int key){ return binarySearch(key) != 1; }

    public void remove(int id, int shift){ // left is 0, right is 1
        if(id >= 0){
            int i;
            for(i=id;i<numKeys;++i){
                keys[i] = keys[i+1];
                items[i] = items[i+1];
                if(!isLeaf){
                    if(i >= id+shift) neighbours[i] = neighbours[i+1];
                }
            }
            keys[i] = 0;
            if(!isLeaf){
                if(i>=id+shift) neighbours[i] = neighbours[i+1];
                neighbours[i+1] = null;
            }
            numKeys--;
        }
    }

    public void shiftRight(){
        if(!isLeaf) neighbours[numKeys+1] = neighbours[numKeys];
        for(int i=numKeys-1;i>=0;--i){
            keys[i+1]=keys[i];
            items[i+1]=items[i];
            if(!isLeaf) neighbours[i+1]=neighbours[i];
        }
    }

    public int subtreeRootNodeIndex(int key){
        for(int i=0;i<numKeys;++i){
            if(key < keys[i]) return i;
        }
        return numKeys;
    }

}