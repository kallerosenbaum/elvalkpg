package se.elva.lkpg.twitterdemo.common;

public class CacheKeys {
	// Comma-separated list of strings to search twitter for
	public static final String TWITTER_SUBJECTS = "twitterSubjects";
	public static final String LOCK = "twitterFeedLock";
	public static final String STARTED_TIME = "twitterFeedStartedTime";
	private static final String MAX_ID_PREFIX = "max_id_";
	
	public static String getMaxIdKey(String searchString) {
		return MAX_ID_PREFIX + searchString;
	}
}
