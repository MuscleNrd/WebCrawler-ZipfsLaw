import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Zipfs
{
    private static HashMap<String, Integer> word_rank = new HashMap<>();

    public Zipfs(String directory) throws Exception
    {
        File[] files = new File(directory).listFiles();
        populate_hashmap(files);
        print_hashmap();
    }

    private static void populate_hashmap(File[] directory) throws Exception
    {
        for (File file : directory)
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null)
            {
                // any number of consecutive spaces to split into tokens
                String[] split = st.split("\\s+");
                for (String temp : split)
                {
                    try
                    {
                        if (!word_rank.containsKey(temp.toLowerCase()))
                            word_rank.put(temp,1);
                        else
                            word_rank.put(temp.toLowerCase(), (word_rank.get(temp) + 1));
                    }

                    catch (NullPointerException e)
                    {
                        System.out.println("Exception Occured!");
                        continue;
                    }

                }
            }
        }
    }

    private static void print_hashmap()
    {
        Map<String,Integer> map = new TreeMap<>(word_rank);
        System.out.println("------------------------------- Printing words and their counts: -------------------------------");
        for (Map.Entry<String,Integer> entry : map.entrySet())
        {
            System.out.println("Count: " + entry.getValue() + " - Word: " + entry.getKey());
        }
        System.out.println("------------------------------- End words and their counts: -------------------------------");
    }
}


