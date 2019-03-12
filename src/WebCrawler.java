import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.util.*;

public class WebCrawler
{
	private static String CURRENT_URL_HEAD = "";
	private static final String PREFIX = "LINK - ";
	private static int COUNTER = 1;
	private static int LINKS_PER_SEED_LIMIT = 50;
	private static final String SUFFIX = ".txt";
	private static Vector<String> SITESTOVISIT = new Vector<>();
	private static HashMap<String, Integer> csv = new HashMap<>();
	private static final String[] START_SEEDS = {"http://dmoz-odp.org/", "https://www.google.com/","https://www.ebay.com/"};


	public static void main(String[] args) throws Exception
	{
		start();
		Zipfs zipfs = new Zipfs("Repository\\");
	}

	private static void start() throws Exception
	{
		for(String seed : START_SEEDS)
		{
			try
			{
				System.out.println("Starting website: " + seed);
				System.out.println(" ----------- Grabbing Robots.txt -----------");
				Robots robots = new Robots(download_robots_txt(get_robots_URL(seed), get_parsed_URL_Title(seed)));
				connect_to_Seed(seed, robots);
			}
			catch (Exception e)
			{
				System.out.println("Website: " + seed + " - Robots.txt failed to download for some reason... Skipping!");
				continue;
			}
		}
		create_csv();
	}

	private static void create_csv() throws Exception
	{
		File dir = new File("Repository\\");
		File csvFile = new File(dir,"Report-OutLinks.csv");
		csvFile.createNewFile();

		BufferedWriter out = new BufferedWriter(new FileWriter(csvFile));
		out.write("URL,LINK-COUNT\n");
		for (Map.Entry<String,Integer> entry : csv.entrySet())
		{
			out.write(entry.getKey() + "," + entry.getValue() + "\n");
		}

		out.close();
	}

	private static String get_robots_URL(String parsed_URL)
	{
		return parsed_URL + "/robots.txt";
	}

	private static File download_robots_txt(String url_to_Robotrs, String curr_dir)
	{
		try
		{
			URL url = new URL(url_to_Robotrs);
			File directory = new File("Repository\\" + curr_dir);
			directory.mkdirs();
			File tmp = new File(directory, "robots.txt");
			tmp.createNewFile();

			// Attempt download of file, and separate lines by \n
			Scanner s = new Scanner(url.openStream()).useDelimiter("\n");

			// Write the file contents to robots.txt
			BufferedWriter out = new BufferedWriter(new FileWriter(tmp));

			while (s.hasNext())
			{
				out.write(s.nextLine() + "\n");
			}
			out.close();

			return tmp;
		} catch (IOException e)
		{
			System.out.println(e);
			return null;
		}
	}

	private static void connect_to_Elements(Vector<String> elements, Robots robots)
	{
		Iterator it = elements.iterator();
		while (it.hasNext() && COUNTER <= LINKS_PER_SEED_LIMIT)
		{
			try
			{
				it = elements.iterator();
				String element = it.next().toString();
				// Connect to website, downloading the HTML into "document"
				if (robots.isAllowed(element))
				{
					Document document = Jsoup.connect(element).get();
					System.out.println("Connecting to: " + element);
					// Save HTML into the website's root
					save_html_text_to_file(document, CURRENT_URL_HEAD);
					// Save ALL links into queue and remove current element
					it.remove();
					add_links_to_queue(document.select("a[href]"), elements);
					// Save element count from current URL
					csv.putIfAbsent(element,document.select("a[href]").size());
				}
				else
				{
					it.remove();
					continue;
				}
			} catch (Exception e)
			{
				System.out.println(e);
				it.remove();
				continue;
			}
		}
		SITESTOVISIT.clear();
		// TODO CREATE LOG.TXT OR SOMETHING THAT RECORDS TOTAL LINKS SO WE KNOW HOW MANY WE FOUND WHILE TRAVERSING.
	}

	private static void connect_to_Seed(String seed, Robots robots) throws Exception
	{
		COUNTER = 1;

		CURRENT_URL_HEAD = get_parsed_URL_Title(seed);
		print("Current URL: %s...", seed);
		// Connect to website, downloading the HTML into "document"
		Document document = Jsoup.connect(seed).get();
		// Save HTML into the website's root
		save_html_text_to_file(document, CURRENT_URL_HEAD);
		// Grab ALL Links
		Elements elements = document.select("a[href]");
		// Save element count from current URL
		csv.putIfAbsent(seed,elements.size());
		// Save ALL links into queue
		add_links_to_queue(elements, SITESTOVISIT);
		// Connect to all elements
		connect_to_Elements(SITESTOVISIT, robots);
	}

	private static String get_parsed_URL_Title(String originalURL)
	{
		return originalURL.replace("/", "").replace(":", "").replace("https", "").replace("http", "");
	}

	private static void add_links_to_queue(Elements links, Vector queue)
	{
		for (Element link : links)
		{
			queue.add(link.attr("abs:href"));
			//print("%s (%s)", link.attr("abs:href"), trim(link.text(), 35));
		}
	}

	private static Elements connect_and_save(Document document, String start_url) throws Exception
	{
		// Connect to website, downloading the HTML into "document"
		document = Jsoup.connect(start_url).get();
		// Save HTML into the website's root
		save_html_text_to_file(document, start_url);
		// Grab all links within the root HTML
		Elements test = new Elements();

		return test;
	}

	private static void save_html_text_to_file(Document html, String headURL) throws Exception
	{
		make_directory("Repository\\" + headURL);
		File file = new File("Repository\\" + headURL);
		BufferedWriter bw = new BufferedWriter(new FileWriter("Repository//" + headURL + "./" + PREFIX + COUNTER + " " + SUFFIX));
		bw.write(html.location() + "\n");
		bw.write(html.text());
		bw.close();
		COUNTER++;
	}

	private static boolean make_directory(String dirName)
	{
		return new File(dirName).mkdirs();
	}

	public static void print(String message, Object... args)
	{
		System.out.println(String.format(message, args));
	}

	private static String trim(String msg, Object... args)
	{
		return String.format(msg, args);
	}


	public static void writeToFile(Elements links) throws IOException
	{
		File file = new File("Repository\\Report.csv");
		file.getParentFile().mkdir();
		file.createNewFile();
		try (PrintWriter writer = new PrintWriter(new File("Repository\\Report.csv")))
		{
			StringBuilder sb = new StringBuilder();
			sb.append("List of URLs: ");
			for (Element link : links)
			{
				sb.append(link.attr("abs:href") + "(" + trim(link.text(), 35) + ")" + ",");
			}
			sb.append('\n');
			sb.append("Number of URLs: ");
			sb.append(',');
			sb.append(links.size());
			sb.append('\n');

			writer.println(sb.toString());
			writer.close();

			System.out.println("\nRepository folder was created, and Report.csv was created inside the respository folder.");

		} catch (FileNotFoundException exception)
		{
			System.out.println(exception.getMessage());
		}
	}
}