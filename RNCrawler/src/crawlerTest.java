import static org.junit.Assert.*;

import org.junit.Test;


public class crawlerTest {
	crawler RNCrawler = new crawler();
	String urlToCrawl = "https://www.digitalocean.com"; 
	//String urlToCrawl = "http://www.google.com";
	String crawlerAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	//static String crawlerAgent = "";
	
	@Test
	public void testGetRawLinks() {
		RNCrawler.getRawLinks(urlToCrawl,crawlerAgent);
		
		//make sure there are no HTTP errors
		assertTrue(RNCrawler.httpErrors, RNCrawler.httpErrors == "");
		
		//make sure we're getting a proper HTTP status code
		assertTrue(RNCrawler.httpStatusCode == 200);
		
		//make sure we're getting the right content
		assertTrue(RNCrawler.httpContentType.contains("text/html"));
		
		//make sure we're getting at least one link returned
		assertTrue(RNCrawler.rawLinksFound > 0);
	}

	@Test
	public void testFilterRawLinks_Text() {
		RNCrawler.getRawLinks(urlToCrawl,crawlerAgent);
		
		//call without escaping delimiter for | and .
		//make sure rawLinksFound is not size 0 since most filtering conditions will not eliminate every single link from rawLinks
		RNCrawler.filterRawLinks_Text("facebook|instagram|#", "|");
		if(RNCrawler.rawLinks.size() == 0) {
			fail("this is fairly unlikely");
		}
		
		RNCrawler.filterRawLinks_Text("facebook.instagram.#", ".");		
		if(RNCrawler.rawLinks.size() == 0) {
			fail("this is fairly unlikely");
		}
		//------------------
		//call with escaping delimiter for | and .
		//make sure rawLinksFound is not size 0 since most filtering conditions will not eliminate every single link from rawLinks
		RNCrawler.filterRawLinks_Text("facebook|instagram|#", "\\|");		
		if(RNCrawler.rawLinks.size() == 0) {
			fail("this is fairly unlikely");
		}
		
		RNCrawler.filterRawLinks_Text("facebook.instagram.#", "\\.");
		if(RNCrawler.rawLinks.size() == 0) {
			fail("this is fairly unlikely");
		}
		//------------------		
		//call with delimiters which don't need escaping
		//make sure rawLinksFound is not size 0 since most filtering conditions will not eliminate every single link from rawLinks
		RNCrawler.filterRawLinks_Text("facebook:instagram:#", ":");
		if(RNCrawler.rawLinks.size() == 0) {
			fail("this is fairly unlikely");
		}
		//------------------				
		//make sure rawLinksFound has filtered out the right links
		String f = "facebook";
		for(String s : RNCrawler.rawLinks) {
			if(s.contains(f)) {
				fail("list still contains text that was supposed to be filtered out");
			}
		}
		
		String i = "instagram";
		for(String s : RNCrawler.rawLinks) {
			if(s.contains(i)) {
				fail("list still contains text that was supposed to be filtered out");
			}
		}
		
		String h = "#";
		for(String s : RNCrawler.rawLinks) {
			if(s.contains(h)) {
				fail("list still contains text that was supposed to be filtered out");
			}
		}
	}

	@Test
	public void testFilterRawLinks_Subdomains() {
		RNCrawler.getRawLinks(urlToCrawl,crawlerAgent);
		RNCrawler.filterRawLinks_Text("facebook|instagram|#", "\\|");
		RNCrawler.filterRawLinks_Subdomains();
		
		//make sure rawLinksFound has filtered out subdomains
		for(String l : RNCrawler.rawLinks) {
			String[] linkParts = l.split("\\.");
			
			if(linkParts.length > 1) {
				if((linkParts[1].trim().equals("digitalocean")) && (!linkParts[0].contains("www"))) {
					fail("we haven't removed all the subdomains");
				}	
			}
		}
	}

	@Test
	public void testCreateSiteMap() {
		RNCrawler.getRawLinks(urlToCrawl,crawlerAgent);
		RNCrawler.filterRawLinks_Text("facebook|instagram|#", "\\|");
		RNCrawler.filterRawLinks_Subdomains();
		RNCrawler.createSiteMap();
		
		//unlikely that final site map will be empty
		if(RNCrawler.siteMapLinks.size() == 0) {
			fail("odd that the final sitemap would be empty for a site");
		} else {
			//re-check for bad content & presence of subdomains in the final site map
			String f = "facebook";
			for(String s : RNCrawler.rawLinks) {
				if(s.contains(f)) {
					fail("list still contains text that was supposed to be filtered out");
				}
			}
			
			String i = "instagram";
			for(String s : RNCrawler.rawLinks) {
				if(s.contains(i)) {
					fail("list still contains text that was supposed to be filtered out");
				}
			}
			
			String h = "#";
			for(String s : RNCrawler.rawLinks) {
				if(s.contains(h)) {
					fail("list still contains text that was supposed to be filtered out");
				}
			}
			
			for(String l : RNCrawler.rawLinks) {
				String[] linkParts = l.split("\\.");
				
				if(linkParts.length > 1) {
					if((linkParts[1].trim().equals("digitalocean")) && (!linkParts[0].contains("www"))) {
						fail("we haven't removed all the subdomains");
					}	
				}
			}
		}
	}

}
