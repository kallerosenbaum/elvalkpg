package se.elva.lkpg.twitterdemo.web.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.infinispan.Cache;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.LuceneStuff;
import twitter4j.Tweet;

@Named
@RequestScoped
public class SearchController {

	private LuceneStuff luceneStuff;

	private CacheCreator cacheCreator;

	private String searchFor;

	private String greeting;

	private final List<Tweet> tweets = new ArrayList<Tweet>();

	public SearchController() {
	}

	@Inject
	public SearchController(LuceneStuff luceneStuff, CacheCreator cacheCreator) {
		this.luceneStuff = luceneStuff;
		this.cacheCreator = cacheCreator;
	}

	public void greet() {
		if (searchIsEntered()) {
			greeting = "Search results for: \"" + searchFor + "\":";
			List<Long> matches = luceneStuff.listStoredValuesMatchingString(searchFor,
					100);
			populateTweets(matches);
		} else {
			greeting = "";
		}
	}

	private void populateTweets(List<Long> tweetIds) {
		tweets.clear();
		Cache<Long, Tweet> tweetCache = cacheCreator.getTweetCache();
		for (Long id : tweetIds) {
			Tweet tweet = tweetCache.get(id);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}
	}

	private boolean searchIsEntered() {
		return searchFor != null && !searchFor.isEmpty();
	}

	public String getSearchFor() {
		return searchFor;
	}

	public void setSearchFor(String searchFor) {
		this.searchFor = searchFor;
	}

	public String getGreeting() {
		return greeting;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

}