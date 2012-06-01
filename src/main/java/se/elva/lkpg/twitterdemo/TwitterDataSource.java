package se.elva.lkpg.twitterdemo;

import java.io.IOException;

import org.infinispan.Cache;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterDataSource {
	public void hej(final Cache<Long, Tweet> tweetCache, Cache<Object, Object> indexCache, String hashTag) {
		
		LuceneStuff luceneIndexer = new LuceneStuff(indexCache);
		
	    Twitter twitter = new TwitterFactory().getInstance();
	    
	    Query query = new Query(hashTag);
	    query.setRpp(100);
		long maxId = 0;
		int tweetCount = 0;
		while (true) {
			QueryResult result = getSearchResult(twitter, query);
			int hitCount = result.getTweets().size();
			tweetCount += hitCount;
			Printer.p("HitCount : " + hitCount + ", Total : " + tweetCount) ;
			for (Tweet tweet : result.getTweets()) {
				Printer.p(tweet.getId() + " : " + tweet.getText());
				tweetCache.put(tweet.getId(), tweet);

				try {
					luceneIndexer.addNewDocument(tweet);
				} catch (IOException e) {
					Printer.p("Failed to index tweet " + tweet);
					e.printStackTrace();
				}
				maxId = Math.max(maxId, tweet.getId());				
			}
			query.setSinceId(maxId);
			sleep();
		}
	    
	}

	private QueryResult getSearchResult(Twitter twitter, Query query) {
		try {
			return twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
			sleep();
			return getSearchResult(twitter, query);
		}
	}

	public static void main( String[] args) {
		TwitterDataSource dataSource = new TwitterDataSource();
		dataSource.hej(CacheCreator.getTweetCache(), CacheCreator.getIndexCache(), args[0]);
	}
	
	private void sleep() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
		}
	}

}
