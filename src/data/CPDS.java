package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class CPDS {
    public static void main(String args[]){
        // there are 1759 rows (including header)
        Entry[] entries = new Entry[1760];
        String line = "";
        String splitBy = ",";
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src/data/CPDS_1960-2019_Update_2021.csv"));
            // read headers
            br.readLine();
            int i = 0;
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] entry = line.split(splitBy);    // use comma as separator
                String year = entry[0];
                String country = entry[1];
                String gov_left1 = entry[11];

                // parse only valid entries, it is fine if we drop a few records
                if (!year.isEmpty() && !country.isEmpty() && !gov_left1.isEmpty()) {
                    entries[i++] = new Entry(Integer.parseInt(year), country, Float.parseFloat(gov_left1));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(Arrays.toString(entries));
    }
}
