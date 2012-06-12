package se.elva.lkpg.twitterdemo.feed;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.infinispan.Cache;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
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
	private static final String LOCK = "twitterFeedLock";
	private static final String STARTED_TIME = "twitterFeedStartedTime";
	private static final String SEARCH_STRING = "elvis";
	private static final String MAX_ID_PREFIX = "max_id_";

	@EJB
	private CacheCreator cacheCreator;

	// private Cache<Long, Tweet> tweetCache;

	// Contains keys "maxId", "subjectList", "nextTime"
	// @Resource(lookup="java:jboss/infinispan/cache/TwitterDemo/timestamp-cache")
	// private Cache<String, String> timestampCache;

	@Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void testSchedule() {
		log.info("testSchedule() called");
		
		Cache<String, String> timestampCache = cacheCreator.getTimestampCache();
		// Check if global time has come!
		
		String started = timestampCache.put(LOCK, "started");
		String startedTimeString = timestampCache.get(STARTED_TIME);
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
			timestampCache.remove(LOCK);
			log.debug("Time has not come yet");
			return;
		}

		timestampCache.put(STARTED_TIME,
				Long.toString(System.currentTimeMillis()));

		try {
			log.debug("execute");
			execute();
		} finally {
			log.debug("Remove lock");
			timestampCache.remove(LOCK);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
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
		hej(SEARCH_STRING);
	}

	public void hej(String hashTag) {
		// LuceneStuff luceneIndexer = new LuceneStuff(indexCache);
		Cache<Long, Tweet> tweetCache = cacheCreator.getTweetCache();
		Cache<String, String> maxIdCache = cacheCreator.getTweetMaxIdCache();

		Twitter twitter = new TwitterFactory().getInstance();

		Query query = new Query(hashTag);
		query.setRpp(100);
		String maxIdKey = MAX_ID_PREFIX + hashTag;
		String maxIdString = maxIdCache.get(maxIdKey);
		long maxId = maxIdString == null ? 0 : Long.parseLong(maxIdString);
		query.setSinceId(maxId);
		QueryResult result = getSearchResult(twitter, query);
		int hitCount = result.getTweets().size();

		log.debug("HitCount : " + hitCount);
		for (Tweet tweet : result.getTweets()) {
			log.debug(tweet.getId() + " : " + tweet.getText());
			tweetCache.put(tweet.getId(), tweet);
			/*
			 * try { luceneIndexer.addNewDocument(tweet); } catch (IOException
			 * e) { Printer.p("Failed to index tweet " + tweet);
			 * e.printStackTrace(); }
			 */
			maxId = Math.max(maxId, tweet.getId());
		}
		maxIdCache.put(maxIdKey, Long.toString(maxId));
	}

	private QueryResult getSearchResult(Twitter twitter, Query query) {
		try {
			return twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
			return getSearchResult(twitter, query);
		}
	}
}
