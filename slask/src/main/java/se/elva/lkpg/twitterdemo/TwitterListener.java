package se.elva.lkpg.twitterdemo;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

import twitter4j.Tweet;

@Listener
public class TwitterListener {
	StatefulKnowledgeSession ksession;

	public TwitterListener(StatefulKnowledgeSession ksession) {
		this.ksession = ksession;
	}

	@CacheEntryModified
	public void entryModified(CacheEntryModifiedEvent<Object, Object> event) {
		if (!event.isPre()) {
			if (event.getValue() instanceof Tweet) {
				Tweet tweet = (Tweet) event.getValue();
				Printer.p(tweet.getId() + " : " + tweet.getToUser());
				WorkingMemoryEntryPoint wm = ksession.getWorkingMemoryEntryPoint("Twitter Stream");
				wm.insert(tweet);
				ksession.fireAllRules();
			}
		}
	}

	@CacheEntryCreated
	public void entryCreated(CacheEntryCreatedEvent<Object, Object> event) {
	}
}
