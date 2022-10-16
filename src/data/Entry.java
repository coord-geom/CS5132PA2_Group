package data;

public class Entry implements Comparable<Entry> {
    public int year;
    public String country;
    public float gov_left1;
    public Entry(int year, String country, float gov_left1) {
        this.year = year;
        this.country = country;
        this.gov_left1 = gov_left1;
    }

    @Override
    public String toString(){
        return year + " " + country + "\n" + gov_left1;
    }

    @Override
    public int compareTo(Entry o) {
        return Float.compare(this.gov_left1, o.gov_left1);
    }
}
