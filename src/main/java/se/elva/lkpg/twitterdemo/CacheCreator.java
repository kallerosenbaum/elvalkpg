package se.elva.lkpg.twitterdemo;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class CacheCreator {

	public static Cache<Object, Object> getTweetCache() {
		EmbeddedCacheManager manager = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
		         .transport().addProperty("configurationFile", "jgroups-tcp.xml")
		         .build());
		
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(2).groups();
	    configBuilder.invocationBatching().enable();	
		manager.defineConfiguration("tweet-cache", configBuilder.build());
		return manager.getCache("tweet-cache");
	}

	public static Cache<Object, Object> getIndexCache() {
		EmbeddedCacheManager manager = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
		         .transport().addProperty("configurationFile", "jgroups-tcp.xml")
		         .build());
		
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(2).groups();
	    configBuilder.invocationBatching().enable();	
		manager.defineConfiguration("index-cache", configBuilder.build());
		return manager.getCache("index-cache");
	}
}
