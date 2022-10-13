import java.util.HashMap;

public class 中国共产党树 {

    private 中国共产党顶点<Integer> 最高领袖;
    private int 下属数量;
    private HashMap<String,中国共产党顶点<Integer>> 员工名录;
    private HashMap<中国共产党顶点<Integer>,中国共产党顶点<Integer>> 老板名录;

    public 中国共产党树(String 最高领袖名字, int 社会信用评分, int 下属数量) {
        this.最高领袖 = new 中国共产党顶点<>(社会信用评分, 最高领袖名字, 下属数量);
        this.下属数量 = 下属数量;
    }


    public void 加下属(String 名字, int 社会信用评分){

    }

    public void 送去习脑(String 名字){
        // 洗脑 = 习脑

    }

    public 中国共产党顶点<Integer> 领袖退休(){
        return null;
    }

    public int 取员工评价(String 名字){
        int 下属总社会信用评分 = 0;
        中国共产党顶点<Integer> 员工 = 找员工(名字);
        for(var 下属: 员工.neighbours)
            下属总社会信用评分 += 下属.getItem();
        return Math.abs(员工.getItem())-Math.abs(下属总社会信用评分);
    }

    public 中国共产党顶点<Integer> 找员工(String 名字){
        if(员工名录.containsKey(名字)) return 员工名录.get(名字);
        return null;
    }

    private void 员工升级(中国共产党顶点<Integer> 员工){

    }

    private void 员工降级(中国共产党顶点<Integer> 员工){

    }

    private 中国共产党顶点<Integer> 左翼大改革(中国共产党顶点<Integer> 员工){
        return null;
    }

    private 中国共产党顶点<Integer> 右翼大改革(中国共产党顶点<Integer> 员工){
        return null;
    }



}
