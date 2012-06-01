package se.elva.lkpg.twitterdemo;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class CacheCreator {
	private static EmbeddedCacheManager manager  = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
	         .transport().addProperty("configurationFile", "jgroups-tcp.xml")
	         .build());

	static {
		configureTweetCache();
		configureIndexCache();
	}
	
	private static void configureCache(String cacheName) {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(2).groups();
	    configBuilder.invocationBatching().enable();	
		manager.defineConfiguration(cacheName, configBuilder.build());
	}
	
	private static void configureTweetCache() {
		configureCache("tweet-cache");
	}
	
	private static void configureIndexCache() {
		configureCache("index-cache");
	}

	public static Cache<Object, Object> getTweetCache() {
		return manager.getCache("tweet-cache");
	}

	public static Cache<Object, Object> getIndexCache() {
		return manager.getCache("index-cache");
	}
}
