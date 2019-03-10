import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Crawler
{
    public static void main(String[] args) throws Exception
    {
        String website_seed = "calpoly.edu";

        File file = new File("C:\\Users\\MuscleNerd\\Desktop\\Robots.txt");

        System.out.println(file);
        Robots robot = new Robots(file);
    }
}
