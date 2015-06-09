import java.io.IOException;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class crawler {
	//***********************************************************************************
	//CORE FUNCTIONALITY CODE
	//***********************************************************************************	
	//variables used in multiple parts of class
	public Set<String> siteMapLinks;
	public List<String> rawLinks = new LinkedList<String>();
	private Document htmlpage;
	
	//stats related variables (will be used for testing: tests are considered passed/failed based on values returned for these variables)
	String httpErrors = "";
	int rawLinksFound = 0;
	int httpStatusCode = 0;
	String httpContentType = "";
	
	//step 1: method to simply retrieve all links in a domain without discrimination
	public void getRawLinks(String urlPassedIn, String agentInfo) {
		try {
			//Connection myConn = Jsoup.connect(urlPassedIn).userAgent(CRAWL_AGENT);
			Connection myConn = null;
			
			switch(agentInfo) {
			case "":
				myConn = Jsoup.connect(urlPassedIn);				
				break;
			default:
				myConn = Jsoup.connect(urlPassedIn).userAgent(agentInfo);				
				break;
			}
			
			htmlpage = myConn.get();
			
			httpStatusCode = myConn.response().statusCode();	//information captured for testing or debug
			httpContentType = myConn.response().contentType();	//information captured for testing or debug
			
			Elements pageLinks = htmlpage.select("a[href]");
			rawLinksFound = pageLinks.size();	//information captured for testing or debug
			
			//store raw links found
			for(Element indivLink : pageLinks) {
				rawLinks.add(indivLink.absUrl("href"));
			}
		} catch(IOException httpEx) {
			//information captured for testing or debug
			httpErrors = httpEx.toString();
		}
	}
	
	//step 2: method to go through all the raw links retrieved in step 1 above and remove links we don't want
	//as per the spec the following social media links should be omitted
	//1. links to facebook 
	//2. links to instagram 
	//method is defined in such a way to handle change in filtering criteria (example if it is decided that linkedin links should be removed instead of facebook that can be done)
	//in addition, following types of links should be removed
	//1. links that contain a # since many of them link to different locations within the same page and it doesn't make sense to output the same link several times
	public void filterRawLinks_Text(String filterOut, String delimiter) {
		String delimiterToUse = null;
		
		switch(delimiter) {
			case "|":
				if(delimiter.indexOf("\\") == -1) {
					delimiterToUse = "\\" + delimiter;
				}
				else {
					delimiterToUse = delimiter;
				}
				break;
			case ".":
				if(delimiter.indexOf("\\") == -1) {
					delimiterToUse = "\\" + delimiter;
				}
				else {
					delimiterToUse = delimiter;
				}
				break;
			default:
				delimiterToUse = delimiter;
				break;
		}
		
		String[] filter = filterOut.split(delimiterToUse);
		
		ListIterator<String> rawLinksIterator = rawLinks.listIterator();
		while(rawLinksIterator.hasNext()) {
			String curLink = rawLinksIterator.next();
			if(shouldRemove(curLink,filter) == true) {
				rawLinksIterator.remove();
			}
		}
	}
	
	private boolean shouldRemove(String link, String[] filter) {
		boolean remove = false;
		
		for(int i=0;i<filter.length;i++) {
			if(link.contains(filter[i])) {
				remove = true;
			}
		}
		
		return remove;
	}
	
	//step 3: method to go through the filtered version of raw links retrieved in step 2 and remove subdomain links
	//assumption here is that the pattern for digitalocean.com subdomains will always be in the format "something.digitalocean.com" in that specific order
	public void filterRawLinks_Subdomains() {
		ListIterator<String> rawLinksIterator = rawLinks.listIterator();
		while(rawLinksIterator.hasNext()) {
			String curLink = rawLinksIterator.next();
			if(isSubDomain(curLink) == true) {
				rawLinksIterator.remove();
			}
		}
	}
	
	private boolean isSubDomain(String link) {
		boolean remove = false;
		
		String[] linkParts = link.split("\\.");
		
		if(linkParts.length > 1) {
			if((linkParts[1].trim().equals("digitalocean")) && (!linkParts[0].contains("www"))) {
				remove = true;
			}	
		}
		
		return remove;
	}
	
	//step 4: copy rawLinks list to siteMapLinks set. If logic hasn't removed duplicate links, copying the list to a set will enforce uniqueness which is what we want in a sitemap
	public void createSiteMap() {
		siteMapLinks = new HashSet<String>(rawLinks);
	}
	
	//***********************************************************************************
	//test functions to output contents while debugging
	//***********************************************************************************
	//will help us to get a visual of contents to be sure that different forms of filtering performed is giving us the required results
	public void printRawLinks() {
		ListIterator<String> rawLinksIterator = rawLinks.listIterator();
		while(rawLinksIterator.hasNext()) {
			String curLink = rawLinksIterator.next();
			System.out.println(curLink);
		}
	}
	
	public void printSiteMap() {
		for(String link : siteMapLinks) {
			System.out.println(link);
		}
	}
}
