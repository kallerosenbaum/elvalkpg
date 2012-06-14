package se.elva.lkpg.twitterdemo.web;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.infinispan.Cache;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.LuceneStuff;
import twitter4j.Tweet;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Search.class);
       
	@EJB
	private LuceneStuff luceneStuff;
	
	@EJB
	private CacheCreator cacheCreator;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String term = request.getParameter("term");
		String count = request.getParameter("count");
		
		List<Long> result;
		Writer writer = response.getWriter();
		if (term == null) {
			writer.write("term is null\n");
			result = luceneStuff.listAllDocuments();
		} else {
			writer.write("term is " + term + "\n");
			result = luceneStuff.listStoredValuesMatchingString(term, 100);
		}
		writer.write("Found " + result.size() + " tweets\n");
		if (!"true".equals(count)) {
			printTweets(result, writer);
		}
	}
	
	private void printTweets(List<Long> tweetIds, Writer writer) {
		Cache<Long, Tweet> tweetCache = cacheCreator.getTweetCache();
		for (Long id : tweetIds) {
			try {
				writer.write(Long.toString(id));
				writer.write(":");
				Tweet tweet = tweetCache.get(id);
				if (tweet == null) {
					writer.write("Not found in cache!");
				} else {
					writer.write(tweet.getFromUser());
					writer.write(":");
					writer.write(tweet.getText());
				}
				writer.write("\n");
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
