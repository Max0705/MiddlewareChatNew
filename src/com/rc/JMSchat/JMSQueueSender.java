package com.rc.JMSchat;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * 
 * @author Christian Maran
 * @date 2013-04-12
 *
 */

public class JMSQueueSender {
	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = "failover://tcp://localhost:61616";
	private String ip = "0.0.0.0";

	private Session session = null;
	private Connection connection = null;
	private Destination destination = null;
	
	private static ActiveMQConnectionFactory connectionFactory;
	private static boolean transacted = false;

	public JMSQueueSender(String usr, String pw, String url, String ip) {
		  user = usr;
		  password = pw;
		  this.url = "failover://tcp://" + url + ":61616";
		  this.ip = ip;
	}

	public void anmelden() throws JMSException {
		connectionFactory = new ActiveMQConnectionFactory(
				user,
				password,
				url);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection
				.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(ip);
	}
	
	public void senden(String nachricht) throws JMSException {
		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		TextMessage message = session.createTextMessage(nachricht);
		System.out.println("Sending message: " + message.getText());
		producer.send(message);
	}
	
	public void abmelden() {
		try {
			session.close();
		} catch (Exception e) {
		}
		try {
			connection.close();
		} catch (Exception e) {
		}
	}
}
