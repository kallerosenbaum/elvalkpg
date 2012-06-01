package se.elva.lkpg.twitterdemo;

import twitter4j.HashtagEntity;
import twitter4j.Tweet;
import twitter4j.UserMentionEntity;

public class TweetPrinter implements TweetProcessor {

	public void processTweet(Tweet tweet) {
		System.out.println("Id: " + tweet.getId());
		System.out.println("Text:"  + tweet.getText());
		System.out.println("To User: " + tweet.getToUser());
		for (HashtagEntity hashTag : tweet.getHashtagEntities()) {
			System.out.println("Hashtag: " + hashTag.getText());
		}
		for (UserMentionEntity userMentioned : tweet.getUserMentionEntities()) {
			System.out.println("User mentioned: " + userMentioned.getScreenName());
		}
		
	}
}
