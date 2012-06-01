package se.elva.lkpg.twitterdemo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import twitter4j.Tweet;

public class CacheCreator {
	private static EmbeddedCacheManager manager  = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
	         .transport().addProperty("configurationFile", "jgroups-tcp.xml")
	         .build());

	static {
		configureTweetCache();
		configureIndexCache();
	}
	
	private static void configureCache(String cacheName) {
		configureJgroupsBindAddress();
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(2).groups();
	    configBuilder.invocationBatching().enable();	
		manager.defineConfiguration(cacheName, configBuilder.build());
	}
	
	private static void configureJgroupsBindAddress() {
		String myIpAddress = "127.0.0.1";
		Socket socket;
		try {
			socket = new Socket(InetAddress.getByName("google.se"), 80);
			myIpAddress = socket.getLocalAddress().getHostAddress();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.setProperty("jgroups.bind_addr", myIpAddress);
	}

	private static void configureTweetCache() {
		configureCache("tweet-cache");
	}
	
	private static void configureIndexCache() {
		configureCache("index-cache");
	}

	public static Cache<Long, Tweet> getTweetCache() {
		return manager.getCache("tweet-cache");
	}

	public static Cache<Object, Object> getIndexCache() {
		return manager.getCache("index-cache");
	}
}
