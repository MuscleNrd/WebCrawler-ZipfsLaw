import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.stream.events.StartDocument;
import java.io.*;
import java.util.Iterator;
import java.util.Vector;

public class WebCrawler
{
	private static String CURRENT_URL_HEAD = "";
	private static final String PREFIX = "LINK - ";
	private static int COUNTER = 1;
	private static final String SUFFIX = ".txt";
	private static Vector<String> SITESTOVISIT = new Vector<>();
	private static Vector<String> ITEMSTOREMOVE = new Vector<>();
	private static Vector<String> NEWSITESTOADD = new Vector<>();
	private static int CURRENT_DEPTH = 1;
	private static final int DEPTHTOCRAWL = 2;
	private static boolean ITERATORS_EMPTY = false;
	private static final String[] START_SEEDS = {"http://google.com/"};


	public static void main(String[] args) throws Exception
	{
		Document document = Jsoup.connect(START_SEEDS[0] + "robots.txt").get();
		System.out.println(document.clone());
		// TODO
		//https://stackoverflow.com/questions/25731346/reading-robot-txt-with-jsoup-line-by-line
		// For this issue ^
		//save_html_text_to_file(document,"Test");
		//Robots robot = new Robots(new File("lol"),"lol");
		//connect_to_Seeds(START_SEEDS);
	}

	private static void connect_to_Elements(Vector<String> elements)
	{
		Iterator it = elements.iterator();
		while(it.hasNext() || CURRENT_DEPTH > DEPTHTOCRAWL)
		{
			try
			{
				it = elements.iterator();
				// Connect to website, downloading the HTML into "document"
				Document document = Jsoup.connect(it.next().toString()).get();
				it.remove();
				// Save HTML into the website's root
				save_html_text_to_file(document,CURRENT_URL_HEAD);
				// Save ALL links into queue
				add_links_to_queue(document.select("a[href]"), elements);
			}
			catch(Exception e)
			{
				System.out.println(e);
				it.remove();
				continue;
			}

		}
	}

	private static void connect_to_Seeds(String[] seeds) throws Exception
	{

		for(String URL : START_SEEDS)
		{
			COUNTER = 1;

			if (!SITESTOVISIT.isEmpty())
			{
				Iterator it = ITEMSTOREMOVE.iterator();
				while(it.hasNext())
					SITESTOVISIT.remove(it.next());
			}

			String start_url = URL;
			CURRENT_URL_HEAD = get_parsed_URL_Title(start_url);
			print("Current URL: %s...", start_url);
			// Connect to website, downloading the HTML into "document"
			Document document = Jsoup.connect(start_url).get();
			// Save HTML into the website's root
			save_html_text_to_file(document,CURRENT_URL_HEAD);
			// Grab ALL Links
			Elements elements = document.select("a[href]");
			// Save ALL links into queue
			add_links_to_queue(elements, SITESTOVISIT);
			// Connect to all elements
			connect_to_Elements(SITESTOVISIT);
		}
	}

	private static String get_parsed_URL_Title(String originalURL)
	{
		return originalURL.replace("/","").replace(":","").replace("https","").replace("http","");
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
		save_html_text_to_file(document,start_url);
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
		if (new File(dirName).mkdirs())
			return true;
		return false;
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