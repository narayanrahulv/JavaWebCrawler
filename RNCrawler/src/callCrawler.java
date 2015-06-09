import java.util.HashMap;


public class callCrawler {
	static crawler RNCrawler = new crawler();	
	static String urlToCrawl = "https://www.digitalocean.com";
	//static String urlToCrawl = "http://www.google.com";
	static HashMap<Integer, String> commonHTTPStatusCodes = new HashMap<Integer, String>();
	static String crawlerAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"; 
	//static String crawlerAgent = "";
	
	public static void main(String[] args) {
		setupStatusCodeLookup();
		
		RNCrawler.getRawLinks(urlToCrawl, crawlerAgent);
		
		//print out any issues/stats resulting from calling getRawLinks
		System.out.println("HTTP errors encountered: ");		
		System.out.println(RNCrawler.httpErrors);
		printOutputSpacing();
		
		System.out.println("Number of raw links found: ");
		System.out.println(RNCrawler.rawLinksFound);
		printOutputSpacing();
		
		System.out.println("HTTP status code encountered by request: ");
		System.out.println(RNCrawler.httpStatusCode + " meaning: " + getHTTPStatus(RNCrawler.httpStatusCode));
		printOutputSpacing();
		
		System.out.println("Content type returned by request: ");
		System.out.println(RNCrawler.httpContentType);
		printOutputSpacing();
		
		//print out all the raw links
		System.out.println("All links encountered: ");
		RNCrawler.printRawLinks();
		printOutputSpacing();
		
		//print out all the filtered links
		//arguments specified for the criteria used to filter out links and the delimiting character used to separate each criteria
		RNCrawler.filterRawLinks_Text("facebook|instagram|#", "\\|");
		System.out.println(RNCrawler.rawLinks.size() + " Raw links filtered by text content: ");
		RNCrawler.printRawLinks();
		printOutputSpacing();
		
		//print out all the links without subdomains
		RNCrawler.filterRawLinks_Subdomains();
		System.out.println("Subdomains removed from list of raw links: ");
		RNCrawler.printRawLinks();
		printOutputSpacing();
		
		//generate final site map
		RNCrawler.createSiteMap();
		System.out.println("Final site map:");
		RNCrawler.printSiteMap();
	}
	
	public static void printOutputSpacing() {
		System.out.println("=====================================================");
	}
	
	public static void setupStatusCodeLookup() {
		commonHTTPStatusCodes.put(200,"OK");
		commonHTTPStatusCodes.put(301,"Moved Permanently");
		commonHTTPStatusCodes.put(307,"Temporary Redirect");
		commonHTTPStatusCodes.put(400,"Bad Request");
		commonHTTPStatusCodes.put(401,"Unauthorized");
		commonHTTPStatusCodes.put(403,"Forbidden");
		commonHTTPStatusCodes.put(404,"Not Found");
		commonHTTPStatusCodes.put(408,"Request Timeout");
		commonHTTPStatusCodes.put(409,"Conflict");
		commonHTTPStatusCodes.put(500,"Internal Server Error");
		commonHTTPStatusCodes.put(501,"Not Implemented");
		commonHTTPStatusCodes.put(502,"Bad Gateway");
		commonHTTPStatusCodes.put(503,"Service Unavailable");
		commonHTTPStatusCodes.put(504,"Gateway Timeout");
		commonHTTPStatusCodes.put(505,"HTTP Version Not Supported");
	}
	
	public static String getHTTPStatus(int statusCode) {
		String returnedStatus = "";
		
		returnedStatus = commonHTTPStatusCodes.get(statusCode);
		
		return returnedStatus;
	}
}
