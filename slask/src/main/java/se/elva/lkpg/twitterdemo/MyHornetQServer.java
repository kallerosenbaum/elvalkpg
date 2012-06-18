package se.elva.lkpg.twitterdemo;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;

public class MyHornetQServer {

	private static HornetQServer SERVER;

	public static void run() throws Exception {
		if (SERVER == null) {
			try {
				// Step 1. Create the Configuration, and set the properties
				// accordingly
				Configuration configuration = new ConfigurationImpl();
				configuration.setPersistenceEnabled(false);
				configuration.setSecurityEnabled(false);
				configuration.getAcceptorConfigurations().add(
						new TransportConfiguration(InVMAcceptorFactory.class
								.getName()));

				// Step 2. Create and start the server
				SERVER = HornetQServers
						.newHornetQServer(configuration);
				SERVER.start();

			} catch (HornetQException hqe) {

			}

		}
	}
	
	public static HornetQServer getServer() {
		return SERVER;
	}
	
	public static void stop() throws Exception {
		if (SERVER == null ) {
			SERVER.stop();
		}
	}

}
