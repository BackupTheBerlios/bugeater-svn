package bugeater.service.impl;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.ResourceBundle;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import bugeater.domain.Issue;
import bugeater.domain.Note;
import bugeater.service.MailService;
import bugeater.service.UserService;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterConstants;
import bugeater.web.page.ViewIssuePage;

/**
 * An implementation of the MailService.  This particular implementation relies
 * on wicket classes and would not be appropriate for non-wicket applications.
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
	
	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	
	private InternetAddress fromAddress;
	public InternetAddress getFromAddress()
	{
		return fromAddress;
	}
	public void setFromAddressString(String s)
	{
		try {
			fromAddress = new InternetAddress(s);
		} catch (AddressException ae) {
			logger.error("Error parsing from address, {}", s, ae);
		}
	}
	
	/**
	 * A thread that will be used to deliver emails.
	 */
	private MailServiceThread mailServiceThread;
	MailServiceThread getMailServiceThread()
	{
		if (mailServiceThread == null) {
			mailServiceThread = new MailServiceThread();
			logger.debug("New mail service thread created.");
			mailServiceThread.start();
		}
		return mailServiceThread;
	}

    private String login;
    @Required
    public void setMailLogin(String login)
    {
        this.login = login;
    }

    private String pass;
    @Required
    public void setMailPassword(String pass)
    {
        this.pass = pass;
    }
    
    private Integer port;
    public void setPort(Integer port) {
    	this.port = port;
    }
	
	/* Spring injected */
	private String serverName;
	/**
	 * The name of the mail server.
	 */
    @Required
	public void setMailServer(String serverName)
	{
		this.serverName = serverName;
	}
    
    private boolean usessl = false;
    public void setSslUsed(boolean sslUsed) {
    	usessl = sslUsed;
    }
//    
//    private boolean usetls = false;
//    public void setTlsUsed(boolean sslTLS) {
//    	usetls = sslTLS;
//    }
	
	private InternetAddress notifyAddress;
	public InternetAddress getNotificationEmailAddress()
	{
		return notifyAddress;
	}
	public void setNotificationEmailAddressString(String s)
	{
		if (s == null || s.length() == 0) {
			notifyAddress = null;
		} else {
			try {
				notifyAddress = new InternetAddress(s);
			} catch (AddressException ae) {
				logger.error("Error parsing notification email {}", s, ae);
			}
		}
	}

	// METHODS
	
	/**
	 * A helper method for the helper method createTextDescription that will
	 * add a descriptive row to the text description.
	 */
	private static void addRow(
			StringBuilder builder, ResourceBundle resource,
			String resourceKey, String value
		)
	{
		builder.append(resource.getString(resourceKey));
		builder.append(": ");
		builder.append(value);
	}

	/**
	 * Creates a new empty message to be filled in, then queued for sending.
	 */
	public MimeMessage createEmptyMessage()
	{
		return new MimeMessage(getMailServiceThread().getSession());
	}

	/**
	 * A helper method that will create a text summary of the issue.
	 */
	private String createTextDescription(Issue issue)
	{
		StringBuilder sb = new StringBuilder();
		
		ResourceBundle resource = ResourceBundle.getBundle(getClass().getName());
		
		PageParameters params = new PageParameters();
		params.add(
				BugeaterConstants.PARAM_NAME_ISSUE_ID,
				String.valueOf(issue.getId())
			);
				
		// ID is null when testing
		if (issue.getId() != null) {
			sb.append("To view the complete issue, browse to ");
			BugeaterApplication app = (BugeaterApplication)Application.get();
			sb.append(app.buildFullyQualifiedPath(ViewIssuePage.class, params));
			sb.append("\n\n");
			addRow(sb, resource, "issue.id", issue.getId().toString());
		}
		sb.append('\n');
		addRow(sb, resource, "issue.summary", issue.getSummary());
		sb.append('\n');
		addRow(sb, resource, "issue.project", issue.getProject());
		sb.append('\n');
		addRow(sb, resource, "issue.category", issue.getCategory());
		sb.append('\n');
		addRow(sb, resource, "issue.priority", issue.getPriority().toString());
		sb.append('\n');
		addRow(sb, resource, "issue.currentStatus", issue.getCurrentStatus().toString());
		
		return sb.toString();
	}
	
	public void emailStatusChange(
			UserService userService, Issue issue
		)
	{
		ResourceBundle resource = ResourceBundle.getBundle(getClass().getName());
		
		StringBuilder sb = new StringBuilder(
				resource.getString("issue.status_changed.body")
			);
		sb.append('\n');
		sb.append(createTextDescription(issue));

		emailToWatchers(
				userService,
				resource.getString("issue.status_changed.subject"),
				sb.toString(), issue
			);
	}
	
	public void emailNotePosted(UserService userService, Note note)
	{
		ResourceBundle resource = ResourceBundle.getBundle(getClass().getName());
		
		StringBuilder sb = new StringBuilder(resource.getString("note.posted.body"));
		sb.append("\n\nIssue: ");
		sb.append(createTextDescription(note.getIssue()));
		sb.append("\n\nNew Note: ");
		sb.append(note.getText());

		emailToWatchers(
				userService, resource.getString("note.posted.subject"),
				sb.toString(), note.getIssue()
			);
	}

	/**
	 * A helper method that will build and email from the subject and text.
	 * The email will then be sent to all the watchers of the issue.
	 * @param mailService The mail service that will deliver the email.
	 * @param userService A service that will allow user info to be retrieved.
	 * @param subject The subject of the email message.
	 * @param text The text of the email message.
	 * @param i The issue for which all watchers will be notified.
	 */
	private void emailToWatchers(
			UserService userService, String subject, String text, Issue i
		)
	{
		try {
			// From address is added before message is queued
			MimeMessage msg = createEmptyMessage();
			msg.setSubject(subject);
			msg.setText(text);
			for (String watcherid : i.getWatchers()) {
				msg.addRecipient(
						RecipientType.TO,
						new InternetAddress(
								userService.getUserById(watcherid).getEmail()
							)
					);
			}
			// The assigned user is always a watcher
			if (i.getAssignedUserID() != null && (! i.getWatchers().contains(i.getAssignedUserID()))) {
				msg.addRecipient(
						RecipientType.TO,
						new InternetAddress(
								userService.getUserById(i.getAssignedUserID()).getEmail()
							)
					);
			}
			InternetAddress notify = getNotificationEmailAddress();
			if (notify != null) {
				msg.addRecipient(RecipientType.TO, getNotificationEmailAddress());
			}
			if (msg.getRecipients(RecipientType.TO).length > 0) {
				queueMessage(msg);
			}
		} catch (MessagingException me) {
			logger.error("Error sending message", me);
		}
	}
	
	/**
	 * @see bugeater.service.MailService#queueMessage(javax.mail.Message)
	 */
	public void queueMessage(Message message)
	{
		try {
			message.setFrom(getFromAddress());
			message.saveChanges();
			getMailServiceThread().queueMessage(message);
		} catch (MessagingException me) {
			logger.error("Error queing message", me);
		}
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
		MailServiceThread()
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
	        props.setProperty("mail.transport.protocol", "smtp");
	        props.setProperty("mail.host", serverName);
	        props.put("mail.smtp.host", serverName);
	        props.put("mail.debug", Boolean.valueOf(logger.isDebugEnabled()).toString());
			if (port != null) {
				props.put("mail.smtp.port", port.toString());
				props.put("mail.smtp.socketFactory.port", port.toString());
			}
//			props.put("mail.smtp.starttls.enable", Boolean.valueOf(usetls).toString());
            if (login != null && login.length() > 0) {
                props.put("mail.smtp.auth", "true");
//                if (usessl || usetls) {
                if (usessl) {
                	// This configuration has been tested against gmail.  Other transports have not been tested.
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
                    props.put("mail.smtp.socketFactory.fallback", "false");
                    props.put("mail.smtp.quitwait", "false");
                	session = Session.getDefaultInstance(props, new Authenticator()
                	{
						@Override
						protected PasswordAuthentication getPasswordAuthentication()
						{
							return new PasswordAuthentication(login, pass);
						}
                	});
                }
            } else {
                props.put("mail.smtp.auth", Boolean.FALSE.toString());
    	        session = Session.getDefaultInstance(props, null);
            }
            session.setDebug(logger.isDebugEnabled());
            logger.debug("Properties: {}", props);
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
			// Run while the runFlag is true.  If runFlag is false, this thread
			// will continue to run until there are no more messages in the
			// queue.
			while (isRunning() || getQueueCount() > 0) {
				// Get the next message to send or null if there are none.
				message = popMessage();
				if (message != null) {
					// Send the email
					try {
						Transport.send(message);
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
