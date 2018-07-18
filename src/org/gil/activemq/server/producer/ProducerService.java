package org.gil.activemq.server.producer;

import javax.jms.Destination;

public interface ProducerService {

	void sendMessage(Destination destination,final Object msg);
	
	void sendMessage(final String msg);
}
