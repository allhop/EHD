package com.nova.ehd.java;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import com.nova.ehd.error.EHDParseException;

public class GetFrom {

	public static void main(String[] args) throws EHDParseException {
		String outputFolder = "C:\\Users\\pmesyk\\Downloads\\test\\";
		String nextURL = " http://g.e-hentai.org/s/e8f01cde52/526301-42";
		// while (!nextURL.equals("")) {
		System.out.println(">> Trying from " + nextURL);
		HashMap<String, String> temp = EHentai2FileAndURL(nextURL, outputFolder);
		nextURL = temp.get("nextUrl");
		// }

		System.out
				.println("Reached end of stream, without a hitch (or many hitches). :)");

	}

	public static HashMap<String, String> EHentai2FileAndURL(String thisURL,
			String outputFolder) throws EHDParseException {

		Connection conn = null;
		Document doc = null;

		int thisPage = 0;							// current page
		int lastPage = 0;							// last page that will be loaded

		
		String nextUrl = "";						// whether there is a next URL, and what the URL is.
		String imageLocation = "";					// the image's location for immediate download!


		Map<String, String> cookies = null;			// Current URL's cookies


		try {

			System.out.println("Connecting to " + thisURL.toString());
			conn = Jsoup
					.connect(thisURL)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0") // thx
																									// http://whatsmyuseragent.com/
			;
			doc = conn.get();
			// System.out.println(doc.body().toString());
		} catch (Exception e) {
			throw new EHDParseException("Could not get connection/doc body!");
		}

		Response res = conn.response();
		cookies = res.cookies();

		Elements links = doc.select("a[href]");

		// System.out.println("\nLinks\n=====");

		for (Element link : links) {

			Elements linkChildren = link.children();
			for (Element linkChild : linkChildren) {
				if (linkChild.tagName().equals("img")) {
					
					
					// If the "next image" link, set the next image info
					// appropriately. This is probably a better place to put it then
					// down below, just because final pages tend to link to 
					// themselves... forever :(
					if (linkChild.attr("src").contains("n.png")) {	// TODO: Check switch to linkSrc?
						nextUrl = link.attr("abs:href");
					}// endif

					// If there's a "keystamp" in the image, then it's
					// the proper image to DL.
					String linkAbsSrc = linkChild.attr("abs:src");
					//String linkSrc = linkChild.attr("src");
					
					if (linkAbsSrc.contains("keystamp=")
							|| linkAbsSrc.contains("image.php?")) {
						imageLocation = linkAbsSrc;
					}// endif
					
					
					//System.out.println("Abs link: " + linkAbsSrc);
					//System.out.println("Rel link: " + linkSrc);
					if (GetFrom.Url2Name(linkAbsSrc).toLowerCase(Locale.ENGLISH).equals("509.gif")) {
						EHDParseException overloadException = new EHDParseException("Overloaded server; link error " + linkAbsSrc);
						overloadException.setDocumentBody(conn.response().body());
						throw overloadException;
						
					}
				}// endif
			}// next

		}// next

		// Find relative location of this/last pages
		Element thisPageElement;
		Element lastPageElement;
		try {
			thisPageElement = doc.select("div.sn div span").get(0);
			lastPageElement = doc.select("div.sn div span").get(1);
		} catch (Exception ex) {
			EHDParseException massiveException = new EHDParseException(
					"Couldn't load element...");
			massiveException.setStackTrace(Thread.currentThread()
					.getStackTrace());
			massiveException.setDocumentBody(conn.response().body());
			throw massiveException;
		}
		thisPage = Integer.valueOf(thisPageElement.text());
		lastPage = Integer.valueOf(lastPageElement.text());

		System.out.println("Final results:\n=============");
		System.out.println("Current URL:   " + thisURL);
		System.out.println("Next url:      " + nextUrl);
		System.out.println("img. location: " + imageLocation);
		System.out.println("This page:     " + thisPage);
		System.out.println("Last page:     " + lastPage);
		System.out.println("Percent done:  "
				+ new DecimalFormat("#.##").format(((double) thisPage)
						/ ((double) lastPage) * 100.0) + "%");

		if (imageLocation.equals("")) {
			
			
			EHDParseException massiveException = new EHDParseException(
					"No ImageLocation found.");
			massiveException.setStackTrace(Thread.currentThread()
					.getStackTrace());
			massiveException.setDocumentBody(conn.response().body());
			throw massiveException;
		}

		String name = GetFrom.Url2Name(imageLocation);

		// System.out
		// .println("Moving skipped for now (skipped " + name + ").");

		// This would allow downloading, something we temporarily
		// wish to avoid. :)
		// URL2File(imageLocation, cookies, thisURL, outputFolder + name);

		// A 1-second pause (thread.sleep) used to go here.

		if (nextUrl.equals(thisURL)) {
			return null;
		} else {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("nextUrl", nextUrl);
			temp.put("imageLocation", imageLocation);
			temp.put("thisPage", String.valueOf(thisPage));
			temp.put("lastPage", String.valueOf(lastPage));
			return temp;
		}// endif

	}

	public static String Url2Name(String url) {
		int slashIndex = url.lastIndexOf('/');
		int equalsIndex = url.lastIndexOf('=');
		String filenameWithExtension;

		if (equalsIndex > slashIndex) {
			filenameWithExtension = url.substring(equalsIndex + 1);
		} else {
			filenameWithExtension = url.substring(slashIndex + 1);
		}
		return filenameWithExtension;
	}

	/**
	 * Convert a URL to a saved file.
	 * 
	 * @param imageLocation
	 *            The location of the image (or URL) to save.
	 * @param cookies
	 *            A set of cookies to pass while loading the image (such as
	 *            login information, etc).
	 * @param referrer
	 *            The referring page that brought you to this image
	 * @param outputFile
	 *            The file to save the resultant image to
	 * @throws IOException
	 *             Returned when there is an error opening/saving the file.
	 */
	public static void URL2File(String imageLocation,
			Map<String, String> cookies, String referrer, String outputFile)
			throws IOException {
		
		// System.out.println("Moving from " + imageLocation);
		// System.out.println("Moving to   " + outputFile);
		// Open a URL Stream
		Response resultImageResponse = Jsoup
				.connect(imageLocation)
				.cookies(cookies)
				// push cookies that allow image load
				.ignoreContentType(true)
				// allows image loading (via execute, or any content type rly)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0") // thx
																								// http://whatsmyuseragent.com/
				.referrer(referrer) // generally, fake using page to load image
				.timeout(0) // just wait
				.execute();

		// output here
		FileOutputStream out = (new FileOutputStream(new java.io.File(
				outputFile)));
		out.write(resultImageResponse.bodyAsBytes()); // resultImageResponse.body()
														// is where the image's
														// contents are.
		out.close();

		// System.out.println("........done.");
	}

	/**
	 * Get the contents of a file to a string.
	 * 
	 * @param fileName
	 *            A string representation of the file's location
	 * @return The contents of the file
	 * @throws IOException
	 *             If the file cannot be opened or read, this will be sent.
	 */
	public static String File(String fileName) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		char[] buf = new char[1024];
		int numRead = 0;

		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();

		return fileData.toString();
	}
}
