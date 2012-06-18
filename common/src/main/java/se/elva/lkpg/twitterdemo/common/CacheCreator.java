package se.elva.lkpg.twitterdemo.common;

import javax.annotation.Resource;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import twitter4j.Tweet;

@Stateless
public class CacheCreator {
	@Resource(lookup = "java:/TwitterDemoCacheContainer")
	private EmbeddedCacheManager manager;
	
	private static final Logger logger = Logger.getLogger(CacheCreator.class);

	public static final String TWEET_CACHE = "tweet-cache";
	public static final String INDEX_CACHE = "index-cache";
	public static final String TWEET_MAXID_CACHE = "tweet-maxid-cache";
	/*
	 * This cache has to use eager cluster-wide locking!
	 */
	public static final String TIMER_CACHE = "timer-cache";
	
	public Cache<Long, Tweet> getTweetCache() {
		return manager.getCache(TWEET_CACHE);
	}

	public Cache<Object, Object> getIndexCache() {
		return manager.getCache(INDEX_CACHE);
	}
	
	public Cache<String, String> getTimestampCache() {
		return manager.getCache(TIMER_CACHE);
	}
	
	public Cache<String, String> getTweetMaxIdCache() {
		return manager.getCache(TWEET_MAXID_CACHE);
	}
}
