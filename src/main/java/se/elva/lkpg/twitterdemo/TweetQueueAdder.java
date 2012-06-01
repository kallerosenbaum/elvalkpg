package se.elva.lkpg.twitterdemo;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;

import twitter4j.Tweet;

public class TweetQueueAdder implements TweetProcessor {
	final String queueName = "queue.exampleQueue";
	private ClientSessionFactory sf;
	
	public TweetQueueAdder () {
		ServerLocator serverLocator = HornetQClient.createServerLocatorWithoutHA(new TransportConfiguration(InVMConnectorFactory.class.getName()));
		try {
			sf = serverLocator.createSessionFactory();

			ClientSession coreSession = sf.createSession(false, false, false);

			coreSession.createQueue(queueName, queueName, true);

			coreSession.close();

		} catch (Exception hqe) {
			
		} finally {
			serverLocator.close();
		}

	}

	public void processTweet(Tweet tweet) {
		ServerLocator serverLocator = HornetQClient.createServerLocatorWithoutHA(new TransportConfiguration(InVMConnectorFactory.class.getName()));
		

		try
		{
			sf = serverLocator.createSessionFactory();
			
			System.out.println("Processing tweet");
			System.out.println(tweet.toString());
			// Step 5. Create the session, and producer
			ClientSession session = sf.createSession();
			
			ClientProducer producer = session.createProducer(queueName);
			
			// Step 6. Create and send a message
			ClientMessage message = session.createMessage(false);
			
			
			
			producer.send(TweetToMessage.convert(tweet, message));
			
			
		}
		catch (Exception hqe ) {
			
		}
		finally
		{
			serverLocator.close();
			// Step 9. Be sure to close our resources!
			if (sf != null)
			{
				sf.close();
			}
		}
		

	}

}
