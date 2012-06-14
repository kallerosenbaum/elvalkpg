package se.elva.lkpg.twitterdemo.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.infinispan.Cache;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.CacheKeys;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Stateless
public class TwitterFeed {

	Logger log = Logger.getLogger(TwitterFeed.class);

	private static final long INTERVAL = 30000;
	private static final long ACCEPTABLE_RUNTIME = 3 * INTERVAL;

	@EJB
	private CacheCreator cacheCreator;

	@EJB
	private Indexer indexer;
	
	@Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void testSchedule() {
		log.info("testSchedule() called");

		Cache<String, String> timestampCache = cacheCreator.getTimestampCache();
		// Check if global time has come!

		String started = timestampCache.put(CacheKeys.LOCK, "started");
		String startedTimeString = timestampCache.get(CacheKeys.STARTED_TIME);
		Long startedTime = startedTimeString == null ? null : Long
				.parseLong(startedTimeString);

		if (started != null) {
			log.debug("Someone else started");
			if (startedRecently(startedTime)) {
				return;
			}
			log.debug("Assume other node dead");
		}

		log.debug("Will see if time has come");

		if (!timeHasCome(startedTime)) {
			timestampCache.remove(CacheKeys.LOCK);
			log.debug("Time has not come yet");
			return;
		}

		timestampCache.put(CacheKeys.STARTED_TIME,
				Long.toString(System.currentTimeMillis()));

		try {
			log.debug("execute");
			execute();
		} finally {
			log.debug("Remove lock");
			timestampCache.remove(CacheKeys.LOCK);
		}
	}

	private boolean timeHasCome(Long startedTime) {
		if (startedTime == null) {
			return true;
		}
		if (startedTime + INTERVAL <= System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	private boolean startedRecently(Long startedTime) {
		if (startedTime == null) {
			return true; // Other worker has not had the time to set the
							// startedTime yet.
		}
		if (startedTime < System.currentTimeMillis() - ACCEPTABLE_RUNTIME) {
			return false;
		}
		return true;
	}

	private void execute() {
		log.debug("RUNNING");
		Cache<String, String> maxIdCache = cacheCreator.getTweetMaxIdCache();
		String searchStrings = maxIdCache.get(CacheKeys.TWITTER_SUBJECTS);
		if (searchStrings == null || searchStrings.trim().equals("")) {
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(searchStrings, ",");		
		List<Tweet> tweetsToIndex = new ArrayList<Tweet>();
		while (tokenizer.hasMoreElements()) {
			feed(tokenizer.nextToken(), tweetsToIndex);
		}
		indexer.index(tweetsToIndex);
	}

	public void feed(String hashTag, List<Tweet> tweetsToIndex) {
		Cache<Long, Tweet> tweetCache = cacheCreator.getTweetCache();
		Cache<String, String> maxIdCache = cacheCreator.getTweetMaxIdCache();

		Twitter twitter = new TwitterFactory().getInstance();

		Query query = new Query(hashTag);
		query.setRpp(100);
		String maxIdKey = CacheKeys.getMaxIdKey(hashTag);
		String maxIdString = maxIdCache.get(maxIdKey);
		long maxId = maxIdString == null ? 0 : Long.parseLong(maxIdString);
		query.setSinceId(maxId);
		List<Tweet> result = getSearchResult(twitter, query);
		int hitCount = result.size();
		log.debug("HitCount : " + hitCount);
		for (Tweet tweet : result) {
			log.debug(tweet.getId() + " : " + tweet.getText());
			if (tweetCache.put(tweet.getId(), tweet) == null) {
				tweetsToIndex.add(tweet);
			}
			maxId = Math.max(maxId, tweet.getId());
		}
		maxIdCache.put(maxIdKey, Long.toString(maxId));
	}

	private List<Tweet> getSearchResult(Twitter twitter, Query query) {
		try {
			return twitter.search(query).getTweets();
		} catch (TwitterException e) {
			log.error("Failed to search Tweets: ", e);
			return Collections.emptyList();
		}
	}
}
