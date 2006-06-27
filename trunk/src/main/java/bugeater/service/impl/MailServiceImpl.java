package bugeater.service.impl;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bugeater.service.MailService;

/**
 * An implementation of the MailService.
 * 
 * @author pchapman
 */
public class MailServiceImpl implements MailService
{
	// CONSTRUCTORS
	
	/**
	 * 
	 */
	public MailServiceImpl() {
		super();
		logger.debug("New instance created.");
	}
	
	// MEMBERS
	
	private static final Log logger = LogFactory.getLog(MailServiceImpl.class);
	
	/**
	 * A thread that will be used to deliver emails.
	 */
	private MailServiceThread mailServiceThread;
	private MailServiceThread getMailServiceThread()
	{
		if (mailServiceThread == null) {
			mailServiceThread = new MailServiceThread(serverName);
			logger.debug("New mail service thread created.");
			mailServiceThread.start();
		}
		return mailServiceThread;
	}
	
	/* Spring injected */
	private String serverName;
	/**
	 * The name of the mail server.
	 */
	public void setMailServer(String serverName)
	{
		this.serverName = serverName;
	}

	// METHODS

	/**
	 * Creates a new empty message to be filled in, then queued for sending.
	 */
	public MimeMessage createEmptyMessage()
	{
		return new MimeMessage(getMailServiceThread().getSession());
	}
	
	/**
	 * @see bugeater.service.MailService#queueMessage(javax.mail.Message)
	 */
	public void queueMessage(Message message)
	{
		getMailServiceThread().queueMessage(message);
	}

	/**
	 * A thread that will continually check a queue of messages and send any
	 * found there.
	 * 
	 * @author pchapman
	 */
	class MailServiceThread extends Thread
	{
		// CONSTRUCTORS

		/**
		 * Creates a new instance.
		 */
		MailServiceThread(String serverName)
		{
			super("SIMIS Mail Service");
			Runtime.getRuntime().addShutdownHook(
					new Thread()
					{
						public void run()
						{
							stopThread();
						}
					}
				);
			// Create some properties and get the default Session
	        Properties props = new Properties();
	        props.put("mail.smtp.host", serverName);
	        session = Session.getDefaultInstance(props, null);
		}
		
		// MEMBERS
		
		private Object lock = new Object();
		private Queue <Message>messageQueue = new LinkedList<Message>();
		private boolean runFlag = true;
		private Session session;
		/** The mail session */
		Session getSession()
		{
			return session;
		}

		/**
		 * Gets a count of messages in the queue.
		 */
		int getQueueCount()
		{
			synchronized(lock) {
				return messageQueue.size();
			}
		}
		
		/**
		 * Returns true if the thread should continue to run, false if it
		 * should finish sending messages and exit.
		 */
		private boolean isRunning()
		{
			synchronized(lock) {
				return runFlag;
			}
		}
		
		// METHODS

		/**
		 * Gets the next message off of the queue, or null if there are none.
		 */
		private Message popMessage()
		{
			synchronized (lock) {
				return messageQueue.poll();
			}
		}
		
		/**
		 * Queues a message to be sent.
		 * @param message The message to send.
		 * @throws IllegalArgumentException if the thread is set to exit and
		 *                                  cannot accept any more messages
		 *                                  for delivery.
		 */
		void queueMessage(Message message)
		{
			synchronized (lock) {
				if (runFlag) {
					messageQueue.add(message);
				} else {
					throw new IllegalArgumentException(
							"The thread is exiting.  Cannot queue any more email messages to be sent."
						);
				}
			}
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			Message message;
			Transport transport = null;
			// Run while the runFlag is true.  If runFlag is false, this thread
			// will continue to run until there are no more messages in the
			// queue.
			while (isRunning() || getQueueCount() > 0) {
				// Get the next message to send or null if there are none.
				message = popMessage();
				if (message == null) {
					// Disconnect from the server (if required) and sleep for
					// a while, waiting for new messages to come in.
					if (transport != null) {
						try {
							transport.close();
						} catch (MessagingException me) {
							logger.error(me);
						}
						transport = null;
					}
					try {
						sleep(1000);
					} catch (InterruptedException ie) {}
				} else {
					if (transport == null) {
						// Instantiate a transport and keep it around for as
						// long as we have emails to send.
						try {
							transport = session.getTransport("smtp");
							transport.connect();
						} catch (Exception e) {
							logger.error(
									"Unable to get a mail transport and/or connect to the server.", e
								);
							return;
						}
					}
					// Send the email
					try {
						transport.sendMessage(
								message, message.getAllRecipients()
							);
					} catch (MessagingException me) {
						logger.error(
								"Unable to send the message: " + message, me
							);
					}
				}
			}
			logger.debug("Mail service thread exiting.");
		}
		
		/**
		 * Tells the thread to send all queued messages and exit.
		 */
		void stopThread()
		{
			synchronized (lock) {
				runFlag = false;
				this.interrupt();
			}
			logger.debug("Mail service thread set to be stopped.");
		}
	}
}
