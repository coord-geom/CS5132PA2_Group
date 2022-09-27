public class MLMTree<T extends Comparable<? super T>> {

    private Node<T> boss;

    public MLMTree(T[] people, int[] underlings, int[] levels, int[] weights) {
        if(people.length == 0) throw new IllegalArgumentException("Empty item array!");
        boss = new MLMNode(people[0], underlings[0]);
        for(int i=1;i<people.length;++i){
            addEmployee(people[i], underlings[i], levels[i], weights[i]);
        }
    }

    public void addEmployee(T employee){

    }

    public void addEmployee(T employee, int underlings, int level, int weight){

    }

    public void fireEmployee(T employee){

    }

    public void getBalance(Node employee){

    }

    private void nyeom(Node employee){

    }

    private void whoop(Node employee){

    }



}
