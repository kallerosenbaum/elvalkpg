package se.elva.lkpg.twitterdemo.feed;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import se.elva.lkpg.twitterdemo.common.LuceneStuff;
import twitter4j.Tweet;

@Stateless
public class Indexer {
	private static final Logger log = Logger.getLogger(Indexer.class);

	@EJB
	private LuceneStuff luceneStuff;

	public void index(List<Tweet> tweets) {
		log.info("Indexer.index() called");

		try {
			luceneStuff.addNewDocument(tweets);
		} catch (IOException e) {
			log.error("Failed to index tweet " + tweets, e);
		}
	}
}
