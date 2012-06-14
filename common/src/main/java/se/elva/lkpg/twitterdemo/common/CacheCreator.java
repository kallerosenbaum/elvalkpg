package se.elva.lkpg.twitterdemo.common;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.lookup.JBossTransactionManagerLookup;
import org.infinispan.transaction.lookup.TransactionManagerLookup;

import twitter4j.Tweet;

@Stateless
public class CacheCreator {
	@Resource(lookup = "java:/TwitterDemoCacheContainer")
	private EmbeddedCacheManager manager;
	
	private static final Logger logger = Logger.getLogger(CacheCreator.class);

	public static final String TWEET_CACHE = "tweet-cache";
	public static final String INDEX_CACHE = "index-cache";
	public static final String TIMER_CACHE = "timer-cache";
	public static final String TWEET_MAXID_CACHE = "tweet-maxid-cache";
	
	@SuppressWarnings(value="unused")
	@PostConstruct
	private void configureCaches() {
		logger.debug("Configuring caches");
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_SYNC).hash()
				.numOwners(2).groups();
		configBuilder.invocationBatching().enable();
		Configuration config = configBuilder.build();
		
		manager.defineConfiguration(TWEET_CACHE, config);
		manager.defineConfiguration(TWEET_MAXID_CACHE, config);

		configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_ASYNC).hash()
				.numOwners(2).groups();
		configBuilder.invocationBatching().enable();
        manager.defineConfiguration(INDEX_CACHE, configBuilder.build());
		

		configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_SYNC).hash()
				.numOwners(2).groups();
		configBuilder.invocationBatching().enable();
        configBuilder.transaction().lockingMode(LockingMode.PESSIMISTIC);
		TransactionManagerLookup lookup = new JBossTransactionManagerLookup();
		configBuilder.transaction().transactionManagerLookup(lookup);
		manager.defineConfiguration(TIMER_CACHE, configBuilder.build());
	}

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
