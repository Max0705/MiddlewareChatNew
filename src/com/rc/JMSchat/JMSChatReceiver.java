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
public class JMSChatReceiver {

	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = "failover://tcp://localhost:61616";
	private String subject = "VSDBChat";

	private Session session = null;
	private Connection connection = null;
	private MessageConsumer consumer = null;
	private Destination destination = null;

	public JMSChatReceiver(String usr, String pw, String url, String topic) {
		user = usr;
		password = pw;
		this.url = "failover://tcp://" + url + ":61616";
		subject = topic;
	}

	public void anmelden() {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				user, password, url);
		try {
			connection = connectionFactory.createConnection();
			connection.start();

			// Create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic(subject);

			// Create the consumer
			consumer = session.createConsumer(destination);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void empfangen() {

		try {

			// Start receiving
			TextMessage message = (TextMessage) consumer.receive();
			if (message != null) {
				message.acknowledge();
				//GUIPanel.chat.setText(GUIPanel.chat.getText() + "\n "
						//+ message.getText());
			}

		} catch (Exception e) {
			System.out.println("[MessageConsumer] Caught: " + e);
			e.printStackTrace();
		}
	}

	public void abmelden() {
		try {
			consumer.close();
		} catch (Exception e) {
		}
		try {
			session.close();
		} catch (Exception e) {
		}
		try {
			connection.close();
		} catch (Exception e) {
		}
	}

	public String getuser(){
		return this.user;
	}
}
