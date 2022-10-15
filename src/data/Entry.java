package data;

public class Entry {
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
        return "year: " + year + ", country: " + country + ", gov_left1: " + gov_left1;
    }
}
