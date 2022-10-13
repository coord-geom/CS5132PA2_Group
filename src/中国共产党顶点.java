public class 中国共产党顶点<T> extends PNode<T>{
    String 名字;
    public 中国共产党顶点(T 社会信用评分, String 名字) {
        super(社会信用评分);
        this.名字 = 名字;
    }

    public 中国共产党顶点(T 社会信用评分, String 名字, int 孩子数量) {
        super(社会信用评分, 孩子数量);
        this.名字 = 名字;
    }

    public 中国共产党顶点(T 社会信用评分, String 名字, Node<T>[] 孩子) {
        super(社会信用评分, 孩子);
        this.名字 = 名字;
    }

    public 中国共产党顶点(中国共产党顶点<T> n) {
        super(n);
        this.名字 = n.名字;
    }

    public T 拿社会信用评分(){
        return super.getItem();
    }
}
