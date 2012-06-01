package se.elva.lkpg.twitterdemo;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.infinispan.Cache;

import twitter4j.Tweet;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
			MyHornetQServer.run();
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			//ksession.setGlobal("tweetProcessor", new TweetQueueAdder());
			ksession.setGlobal("tweetProcessor", new TweetPrinter());
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");
			Cache<Long, Tweet> cache = CacheCreator.getTweetCache();
			TwitterListener twitterListener = new TwitterListener(ksession);
			cache.addListener(twitterListener);
			logger.close();

			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("Sample.drl"),
				ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error : errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		kbConf.setOption(EventProcessingOption.STREAM);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConf);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

}