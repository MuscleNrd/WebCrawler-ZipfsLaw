import java.io.*;
import java.util.*;
import java.util.Collections;

public class Zipfs
{
    private static HashMap<String, Integer> word_rank = new HashMap<>();
    private static TreeMap<String, Integer> sorted = new TreeMap<>();

    public Zipfs(String directory) throws Exception
    {
        File[] folders = new File(directory).listFiles();
        populate_hashmap(folders);
        add_to_report();
    }

    private static void populate_hashmap(File[] directory) throws Exception
    {
        for (File file : directory)
        {
            if (file.isDirectory())
            {
                System.out.println("Printing from directory: " + file.getName());
                populate_hashmap(file.listFiles());
                print_hashmap();
               // word_rank.clear();
                continue;
            }

            if (file.getName().contains("robot"))
                continue;

            System.out.println(file.getName());

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null)
            {
                // any number of consecutive spaces to split into tokens
                String[] split = st.split("\\W+");
                for (String temp : split)
                {
                    temp = temp.toLowerCase();
                    try
                    {
                        if (!word_rank.containsKey(temp))
                            word_rank.put(temp,1);
                        else
                            word_rank.put(temp, (word_rank.get(temp) + 1));
                    }

                    catch (NullPointerException e)
                    {
                        System.out.println(e);
                        continue;
                    }

                }
            }
        }
    }

    private static void add_to_report() throws Exception
    {
        File dir = new File("Repository\\");
        File csvFile = new File(dir,"Report.csv");

        BufferedWriter out = new BufferedWriter(new FileWriter(csvFile));
        out.write("\nWORD,WORD-COUNT\n");
        for (Map.Entry<String,Integer> entry : sorted.entrySet())
        {
            out.write(entry.getKey() + "," + entry.getValue() + "\n");
        }
        out.close();
    }

    private static void print_hashmap()
    {
        sorted = sortMapByValue(word_rank);
        word_rank.clear();
        System.out.println("------------------------------- Printing words and their counts: -------------------------------");
        for (Map.Entry<String,Integer> entry : sorted.entrySet())
        {
            System.out.println("Count: " + entry.getValue() + " - Word: " + entry.getKey());
        }
        System.out.println("------------------------------- End words and their counts: -------------------------------");
    }

    public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
        Comparator<String> comparator = new ValueComparator(map);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(map);
        return result;
    }
}


