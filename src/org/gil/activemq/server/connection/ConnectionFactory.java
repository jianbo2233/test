package org.gil.activemq.server.connection;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class ConnectionFactory{

	/*@Autowired
	@Qualifier("connectionFactory")*/
	public ActiveMQConnectionFactory connectionFactory;
	
	public Session session;
	
	public ConnectionFactory(ActiveMQConnectionFactory factory){
		connectionFactory=factory;
	}
	
	private boolean initSession(){
		try {
			if (null==session) {
				Connection connection = connectionFactory.createConnection();
				connection.start();
				session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public Topic getTopic(String name) throws Exception{
		if (!initSession()) return null;
		Topic topic = session.createTopic(name);
		return topic;
	}

	public MessageConsumer getConsumer(String name) throws Exception {
		if (!initSession()) return null;
		MessageConsumer consumer = session.createConsumer(getTopic(name));
		return consumer;
	}

	public MessageProducer getProducer(String name) throws Exception{
		if (!initSession()) return null;
		MessageProducer producer = session.createProducer(getTopic(name));
		return producer;
	}
}
