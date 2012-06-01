package se.elva.lkpg.twitterdemo;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.hornetq.api.core.client.ClientMessage;

import twitter4j.HashtagEntity;
import twitter4j.Tweet;
import twitter4j.UserMentionEntity;

public class TweetToMessage {
	public static final String SCREEN_NAME = "screenName";
	public static final String TEXT = "text";
	public static final String HASH_TAG = "hashtag";
	public static final String USERS = "userMentions";
	
	
	public static ClientMessage convert(Tweet tweet, ClientMessage message) {
		message.putStringProperty(SCREEN_NAME, tweet.getFromUser());
		message.putStringProperty(TEXT, tweet.getText());
		Set<String> hashTags = new HashSet<String>();
		for (HashtagEntity hashTag : tweet.getHashtagEntities()) {
			hashTags.add(hashTag.getText());
		}
		message.putStringProperty(HASH_TAG, StringUtils.collectionToDelimitedString(hashTags, ","));
		Set<String> userMentions = new HashSet<String>();
		for (UserMentionEntity userMentioned : tweet.getUserMentionEntities()) {
			userMentions.add(userMentioned.getScreenName());
		}
		message.putStringProperty(USERS, StringUtils.collectionToDelimitedString(userMentions, ","));
		return message;
	}
}
