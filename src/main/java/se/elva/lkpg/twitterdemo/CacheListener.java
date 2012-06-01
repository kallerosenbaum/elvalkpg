package se.elva.lkpg.twitterdemo;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

import twitter4j.Tweet;

@Listener
public class CacheListener {

	@CacheEntryModified
	public void entryModified(CacheEntryModifiedEvent<Object, Object> event) {
		Tweet tweet = (Tweet)event.getValue();
		if (tweet != null) {
			System.out.println("Mod " + event.getKey() + " : " + tweet.getText());
		} else {
			System.out.println("Mod " + event.getKey());
		}
	}

	@CacheEntryCreated
	public void entryCreated(CacheEntryCreatedEvent<Object, Object> event) {
		
		Tweet tweet = (Tweet)event.getCache().get(event.getKey());
		if (tweet != null) {
			System.out.println("Cre " + event.getKey() + " : " + tweet.getText());
		} else {
			System.out.println("Cre " + event.getKey());
		}
	}
}
