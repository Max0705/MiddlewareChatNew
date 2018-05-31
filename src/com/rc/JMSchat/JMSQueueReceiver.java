package com.rc.JMSchat;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * 
 * @author Christian Maran
 * @date 2013-04-12
 *
 */
public class JMSQueueReceiver {
	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = "failover://tcp://localhost:61616";
	private String ip = "0.0.0.0";
	private JTextArea chat;
	private static final String CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	private static final String QUEUE_CONNECTION_FACTORY_NAME = "QueueConnectionFactory";
	private static ConnectionListener connectionListener;

	private Session session = null;
	private Connection connection = null;
	private Destination destination = null;
	MessageConsumer consumer = null;

	private static ActiveMQConnectionFactory connectionFactory;
	private static boolean transacted = false;

	public JMSQueueReceiver(String usr, String pw, String url, String ip, JTextArea chat) {
		user = usr;
		password = pw;
		this.url = "failover://tcp://" + url + ":61616";
		this.ip = ip;
		connectionListener = new ConnectionListener();
		this.chat = chat;
		
	}

	public void anmelden() throws JMSException {
		connectionFactory = new ActiveMQConnectionFactory(user, password, url);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection
				.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(ip);
	}

	private static Context createContext(String brokerUrl, String queueName) {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
		props.put(Context.PROVIDER_URL, brokerUrl);
		props.put("queue." + queueName, queueName); 
		try {
			return new InitialContext(props);
		} catch (NamingException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
    public void loadMails(String ip) {
        try {
    
            Context context = createContext(url, ip);
        	//Context context = createContext(this.subject, queueName);
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(QUEUE_CONNECTION_FACTORY_NAME);
            ActiveMQConnection connection = (ActiveMQConnection) factory.createQueueConnection(); // activemq-specific
            QueueSession session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE); // the client acks the reception of the messages!
            Queue queue = (Queue) context.lookup(ip);
            QueueReceiver queueReceiver = session.createReceiver(queue);
 
            connection.addTransportListener(connectionListener);// activemq-specific
            connection.start();
            QueueBrowser queueBrowser = session.createBrowser(queueReceiver.getQueue());
            Enumeration e = queueBrowser.getEnumeration();
            int numMsgs = 1;
            chat.append("MAILS:\n");
            while (e.hasMoreElements()) {
            	Message message = (Message) e.nextElement();
            	TextMessage textMessage = (TextMessage) message;
            	chat.append("\nMail " + numMsgs + "\n\t" + textMessage.getText());
            	numMsgs++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

	private static class ConnectionListener implements TransportListener {

		private static final Logger logger = Logger
				.getLogger(ConnectionListener.class);

		public void onCommand(Object command){
			logger.info("command:" + command.toString());
		}

		@Override
		public void onException(IOException arg0) {
			// TODO Auto-generated method stub
			logger.info("exception:" + arg0.toString());
		}

		@Override
		public void transportInterupted() {
			// TODO Auto-generated method stub
			logger.info("interrupted:");
		}

		@Override
		public void transportResumed() {
			// TODO Auto-generated method stub
			logger.info("resumed:");
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
}
