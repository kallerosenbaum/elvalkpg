package se.elva.lkpg.twitterdemo;

import twitter4j.HashtagEntity;
import twitter4j.Tweet;
import twitter4j.UserMentionEntity;

public class TweetPrinter implements TweetProcessor {

	public void processTweet(Tweet tweet) {
		Printer.p("Id: " + tweet.getId());
		Printer.p("Text:"  + tweet.getText());
		Printer.p("To User: " + tweet.getToUser());
		for (HashtagEntity hashTag : tweet.getHashtagEntities()) {
			Printer.p("Hashtag: " + hashTag.getText());
		}
		for (UserMentionEntity userMentioned : tweet.getUserMentionEntities()) {
			Printer.p("User mentioned: " + userMentioned.getScreenName());
		}
		
	}
}
