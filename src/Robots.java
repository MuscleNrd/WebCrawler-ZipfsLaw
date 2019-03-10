import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Robots extends Crawler
{
    private static String WEBSITE_ROOT = "";
    private static HashMap<String,Boolean> map_robot_listing = new HashMap<>();
    private static final String[] IGNORED_STRINGS = {"#","Sitemap","User-agent"};
    private static final String ROBOTS_NAME = "Mr Robot: " ;
    private static boolean DEBUG = true; // Allows Mr. Robot to be more verbose.
    private static final String SPEAK_CKECKING_DIR_AGAINST_ROBOTS = "Checking requested URL against Robots.txt, sir.";
    private static final String SPEAK_POPULATING_HASHMAP_FINISHED = "Finished populating hashmap, sir.";
    private static final String SPEAK_PRINTING_DISALLOWED_URLS = "Printing disallowed URLS, sir.";
    private static final String SPEAK_PRINTING_ALLOWED_URLS = "Printing allowed URLS, sir.";
    private static final String SPEAK_REQUEST_URL_NOT_REFERENCED = "The URL requested does not exist in Robots.txt, sir.";


    protected Robots(File robots_txt, String website_root) throws Exception
    {
        this.WEBSITE_ROOT = website_root;
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
            Boolean left = grab_content_after_before_colon(st , false).contains("Allow") ? true : false;
            String right = grab_content_after_before_colon(st , true);
            map_robot_listing.putIfAbsent(right,left);

            if (DEBUG)
            robot_speak( (left ? "Allowing" : "Disallowing") + " crawling of : " + right);
        }
        robot_speak(SPEAK_POPULATING_HASHMAP_FINISHED);
        print_list(false);
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

    public boolean isAllowed(String target) throws Exception
    {
        try
        {
            boolean result = map_robot_listing.get(target);
            if (DEBUG)
                robot_speak(SPEAK_CKECKING_DIR_AGAINST_ROBOTS, result == true ? ("\"" + target + "\"" + "... ALLOWED!") : (  "\"" + target + "\"" + "...DISALLOWED!" ));

            return result;
        }

        catch (NullPointerException e)
        {
            robot_speak(SPEAK_REQUEST_URL_NOT_REFERENCED, "Not Found: \"" + target + "\"");
        }
        return false;
    }

    protected static void print_list(boolean Allowed)
    {
        if (!Allowed)
            robot_speak(SPEAK_PRINTING_DISALLOWED_URLS);
        else
            robot_speak(SPEAK_PRINTING_ALLOWED_URLS);
        for (Map.Entry<String,Boolean> entry : map_robot_listing.entrySet())
        {
            Boolean b_value = entry.getValue();
            if (b_value == Allowed)
            {
                String item = entry.getKey();
                System.out.println(item);
            }
        }
    }

    private static void robot_speak(String text, String...etc)
    {
        System.out.println("------------------------------------------------------------");
        System.out.println(ROBOTS_NAME + text);
        for ( String element : etc)
        {
            System.out.println(element);
        }
        System.out.println("------------------------------------------------------------");

    }


}
