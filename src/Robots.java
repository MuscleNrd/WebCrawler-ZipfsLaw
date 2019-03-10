import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Robots extends Crawler
{
    private static HashMap<String,Boolean> map_robot_listing = new HashMap<>();
    private static final String[] IGNORED_STRINGS = {"#","Sitemap","User-agent"};


    protected Robots(File robots_txt) throws Exception
    {
        populate_hashmap(robots_txt);
    }

    private static void populate_hashmap(File robots_txt) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(robots_txt));

        String st;
        while ((st = br.readLine()) != null)
        {
            if (contains_ignored_string(st) || st.length() <= 0)
                continue;
            System.out.println(grab_content_after_before_colon(st , false));
        }
    }

    // Tested and working
    private static String grab_content_after_before_colon(String target, boolean after_colon)
    {
        return  remove_whitespace(after_colon ? target.substring((target.indexOf(":") + 2)) : target.substring(0,target.indexOf(":")));
    }

    private static String remove_whitespace(String target)
    {
        target = target.replace(" ","");
        target = target.replace("   ","");
        return target;
    }

    private static boolean contains_ignored_string(String target)
    {
        for (String element : IGNORED_STRINGS)
        {
            if (target.contains(element))
                return true;
        }
        return false;
    }

    protected static boolean isDenied(HashMap<String,Boolean> list)
    {

        return false;
    }

    protected static boolean isAllowed(HashMap<String,Boolean> list)
    {

        return false;
    }

    protected static void print_list(boolean Allowed)
    {
        for (Map.Entry<String,Boolean> entry : map_robot_listing.entrySet())
        {
            Boolean b_value = entry.getValue();
            if (b_value && Allowed)
            {
                String item = entry.getKey();
                System.out.println(item);
            }
        }
    }
}
