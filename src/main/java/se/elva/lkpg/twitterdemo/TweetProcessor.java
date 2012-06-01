package se.elva.lkpg.twitterdemo;

import twitter4j.Tweet;

public interface TweetProcessor {
	public void processTweet(Tweet tweet);
}
