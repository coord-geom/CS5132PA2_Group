package model;

public class Printing {
    public static void main(String[] args) {
        StringBuilder string = new StringBuilder();
        for(int i=1;i<=12;++i){
            for(int j=1;j<=28;++j){
                for(int k=0;k<26;k++){
                    string.append("2021-");
                    if(i < 10) string.append("0");
                    string.append(i).append("-");
                    if(j < 10) string.append("0");
                    string.append(j).append(",").append((char) (k + 'A')).append(",random,nothing\n");
                }
            }
        }
        System.out.println(string);
    }
}
