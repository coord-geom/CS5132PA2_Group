package model;

import java.util.ArrayList;

/**
 * Implementation of a B Tree
 * Utilises the model.BNode class
 */
public class BTree<T> {

    private BNode<T> root = null;
    private static final int LEFT_NODE = 0;
    private static final int RIGHT_NODE = 1;
    private int order = 2;

    public BTree(int order){
        this.order = order;
        root = new BNode<>(2*order);
        root.isLeaf = true;
    }

    public BNode<T> getRootNode() {
        return this.root;
    }

    public void add(int key, T item){
        BNode<T> root_ = root;
        if(!update(root,key,item)){
            if(root_.numKeys==2*order-1){
                root=new BNode<>(2*order);
                root.isLeaf=false;
                root.neighbours[0]=root_;
                split(root,0,root_);
                insertNode(root,key,item);
            } else insertNode(root_,key,item);
        }
    }

    private void split(BNode<T> parent, int i, BNode<T> child){
        BNode<T> newNode = new BNode<>(2*order);
        newNode.isLeaf = child.isLeaf;
        newNode.numKeys = order-1;
        for(int j=0;j<order-1;++j){
            newNode.keys[j]=child.keys[j+order];
            newNode.items[j]=child.items[j+order];
        }
        if(!newNode.isLeaf){
            for(int j=0;j<order;++j) newNode.neighbours[j]=child.neighbours[j+order];
            for(int j=order;j<=child.numKeys;++j) child.neighbours[j]=null;
        }
        for(int j=order;j<child.numKeys;++j){
            child.keys[j]=0;
            child.items[j]=null;
        }
        child.numKeys=order-1;
        for(int j=parent.numKeys;j>=i+1;--j){
            parent.neighbours[j+1]=parent.neighbours[j];
            parent.keys[j]=parent.keys[j-1];
            parent.items[j]=parent.items[j-1];
        }
        parent.neighbours[i+1]=newNode;
        parent.keys[i]=child.keys[order-1];
        parent.items[i]=child.items[order-1];
        child.keys[order-1]=0;
        child.items[order-1]=null;
        ++parent.numKeys;
    }

    private void insertNode(BNode<T> node, int key, T item){
        int i=node.numKeys-1;
        if(node.isLeaf){
            while(i>=0 && key<node.keys[i]) {
                node.keys[i+1] = node.keys[i];
                node.items[i+1] = node.items[i];
                i--;
            }
            ++i;
            node.keys[i]=key;
            node.items[i]=item;
            node.numKeys++;
        } else {
            while(i>=0 && key<node.keys[i]) i--;
            ++i;
            if(((BNode) node.neighbours[i]).numKeys == 2*order-1){
                split(node,i,(BNode) node.neighbours[i]);
                if(key>node.keys[i]) ++i;
            }
            insertNode((BNode) node.neighbours[i],key,item);
        }
    }

    public void delete(int key){
        delete(root,key);
    }

    private void delete(BNode<T> node, int key){
        int i=node.binarySearch(key);
        if(node.isLeaf){
            if(i!=-1) node.remove(i,LEFT_NODE);
        } else {
            if(i!=-1){
                BNode<T> left = (BNode<T>) node.neighbours[i];
                BNode<T> right = (BNode<T>) node.neighbours[i+1];
                if(left.numKeys>=order){
                    BNode<T> preNode = left;
                    BNode<T> toDelete = preNode;
                    while(!preNode.isLeaf){
                        toDelete = preNode;
                        preNode = (BNode<T>) preNode.neighbours[node.numKeys-1];
                    }
                    node.keys[i]=preNode.keys[preNode.numKeys-1];
                    node.items[i]=preNode.items[preNode.numKeys-1];
                    delete(toDelete,node.keys[i]);
                } else if(right.numKeys >= order){
                    BNode<T> sucNode = right;
                    BNode<T> toDelete = sucNode;
                    while(!sucNode.isLeaf){
                        toDelete = sucNode;
                        sucNode = (BNode<T>) sucNode.neighbours[0];
                    }
                    node.keys[i]=sucNode.keys[0];
                    node.items[i]=sucNode.items[0];
                    delete(toDelete,node.keys[i]);
                } else {
                    int medianId = mergeNodes(left,right);
                    moveKey(node,i,RIGHT_NODE,left,medianId);
                    delete(left,key);
                }
            } else {
                i = node.subtreeRootNodeIndex(key);
                BNode<T> child = (BNode<T>) node.neighbours[i];
                if(child.numKeys==order-1){
                    BNode<T> childLeft = (i-1>=0)? (BNode<T>) node.neighbours[i-1]:null;
                    BNode<T> childRight = (i+1<=node.numKeys)? (BNode<T>) node.neighbours[i+1]:null;
                    if(childLeft!=null && childLeft.numKeys>=order){
                        child.shiftRight();
                        child.keys[0]=node.keys[i-1];
                        child.items[0]=node.items[i-1];
                        if(!child.isLeaf) child.neighbours[0]=node.neighbours[i-1];
                        ++child.numKeys;

                        node.keys[i-1]=childLeft.keys[childLeft.numKeys-1];
                        node.items[i-1]=childLeft.items[childLeft.numKeys-1];
                        childLeft.remove(childLeft.numKeys-1,RIGHT_NODE);
                    } else if(childRight!=null && childRight.numKeys>=order){
                        child.keys[childRight.numKeys]=node.keys[i];
                        child.items[childRight.numKeys]=node.items[i];
                        if(!child.isLeaf) child.neighbours[childRight.numKeys]=node.neighbours[i];
                        ++child.numKeys;

                        node.keys[i]=childRight.keys[0];
                        node.items[i]=childRight.items[0];
                        childRight.remove(0,LEFT_NODE);
                    } else {
                        if(childLeft!=null){
                            int medianId=mergeNodes(child,childLeft);
                            moveKey(node,i-1,LEFT_NODE,child,medianId);
                        } else if(childRight!=null){
                            int medianId=mergeNodes(child,childRight);
                            moveKey(node,i,RIGHT_NODE,child,medianId);
                        }
                    }
                }
                delete(child,key);
            }
        }
    }

    private int mergeNodes(BNode<T> target, BNode<T> source){
        int medianId;
        if(source.keys[0] < target.keys[target.numKeys-1]){
            if(!target.isLeaf)
                target.neighbours[source.numKeys+ target.numKeys+1]= target.neighbours[target.numKeys];
            for(int i=target.numKeys;i>0;--i){
                target.keys[source.numKeys+i]=target.keys[i-1];
                target.items[source.numKeys+i]=target.items[i-1];
                if(!target.isLeaf)
                    target.neighbours[source.numKeys+i]=target.neighbours[i-1];
            }
            medianId=source.numKeys;
            target.keys[medianId] = 0;
            for(int i=0;i<source.numKeys;++i){
                target.keys[i]=source.keys[i];
                target.items[i]=source.items[i];
                if(!source.isLeaf) target.neighbours[i]=source.neighbours[i];
            }
            if(!source.isLeaf) target.neighbours[source.numKeys]=source.neighbours[source.numKeys];
        } else {
            medianId=target.numKeys;
            target.keys[medianId]=0;
            target.items[medianId]=null;
            int offset=medianId+1;
            for(int i=0;i<source.numKeys;++i){
                target.keys[offset+i]=source.keys[i];
                target.items[offset+i]=source.items[i];
                if(!source.isLeaf) target.neighbours[offset+i]=source.neighbours[offset+i];
            }
            if(!source.isLeaf) target.neighbours[offset+source.numKeys]=source.neighbours[source.numKeys];
        }
        target.numKeys += source.numKeys;
        return medianId;
    }

    private void moveKey(BNode<T> source, int sourceId, int childId, BNode<T> target, int medianId){
        target.keys[medianId]=source.keys[sourceId];
        target.items[medianId]=source.items[sourceId];
        ++target.numKeys;

        source.remove(sourceId,childId);
        if(source==root && source.numKeys==0) root=target;
    }

    public T search(int key){
        return search(root, key);
    }

    public T search(BNode<T> node, int key){
        int i=0;
        while(i<node.numKeys && key>node.keys[i]) i++;
        if(i<node.numKeys && key==node.keys[i]) return (T) node.neighbours[i].getItem();
        if(node.isLeaf) return null;
        return (T) search((BNode) node.neighbours[i],key);
    }

    private boolean update(BNode<T> node, int key, T item){
        while(node != null){
            int i=0;
            while(i<node.numKeys && key>node.keys[i]) i++;
            if(i<node.numKeys && key==node.keys[i]){
                node.items[i] = item;
                return true;
            }
            if(node.isLeaf) return false;
            node = (BNode<T>) node.neighbours[i];
        }
        return false;
    }

    public void clear(){
        root = null;
    }

    public ArrayList<Integer> getKeys(BNode<T> node){
        ArrayList<Integer> res = new ArrayList<>();
        if(node!=null){
            if(node.isLeaf){
                for(int i=0;i<node.numKeys;++i) res.add(node.numKeys);
            } else {
                int i;
                for(i=0;i<node.numKeys;++i){
                    res.addAll(getKeys((BNode<T>) node.neighbours[i]));
                    res.add(node.keys[i]);
                }
                res.addAll((getKeys((BNode<T>) node.neighbours[i])));
            }
        }
        return res;
    }

    private String printTree(BNode<T> node){
        StringBuilder string = new StringBuilder();
        if(node!=null){
            if(node.isLeaf){
                for(int i=0;i<node.numKeys;++i) string.append(node.items[i]).append(" ");
            } else {
                int i;
                for(i=0;i<node.numKeys;++i){
                    string.append(printTree((BNode<T>) node.neighbours[i]));
                    string.append(node.items[i]).append(" ");
                }
                string.append(printTree((BNode<T>) node.neighbours[i]));
            }
        }
        return string.toString();
    }
    public String toString(){
        // return printTree(root);
        return root.toString();
    }
}
