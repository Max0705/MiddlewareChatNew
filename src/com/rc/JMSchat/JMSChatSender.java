package com.rc.JMSchat;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Calendar;

/**
 * 
 * @author Christian Maran
 * @date 2013-04-12
 *
 */
public class JMSChatSender {

	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = "failover://tcp://localhost:61616";
	private String subject = "VSDBChat";
	private String ip;
	
	private Session session = null;
	private Connection connection = null;
	private MessageProducer producer = null;
	private Destination destination = null;

	public JMSChatSender (String usr, String pw, String url, String ip, String topic){
		  user = usr;
		  password = pw;
		  this.url = "failover://tcp://" + url + ":61616";
		  this.ip = ip;
		  subject = topic;
	}
	
	
	public void anmelden(){
		try {
			//Create Connection
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
			connection.start();

			// Create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic(subject);

			// Create the producer.
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		}catch (Exception e) {
			System.out.println("[MessageProducer] Caught: " + e);
			e.printStackTrace();
		}
	}
	
	public void senden(String nachricht) {
		try {
			// Create the message
			TextMessage message = session.createTextMessage("" + user + "<" + ip + "> " + Calendar.getInstance().getTime() + ": " + nachricht);
			producer.send(message);
			System.out.println(message.getText());

		} catch (Exception e) {
			System.out.println("[MessageProducer] Caught: " + e);
			e.printStackTrace();
		}

	}
	
	public void abmelden(){
		try {
			producer.close();
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

}