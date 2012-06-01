package se.elva.lkpg.twitterdemo;

import org.infinispan.Cache;

import twitter4j.Tweet;

public class InfinispanTest {
	
	public static void main(String[] args) {
		Cache<Long, Tweet> c = CacheCreator.getTweetCache();

		while (true) {
			Printer.p("Cache contains " + c.size() + " elements");
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
			}
		}
	}
}
