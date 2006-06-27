package bugeater.service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A service to send email messages.
 * 
 * @author pchapman
 */
public interface MailService
{
	/**
	 * Creates a new message suitable to be filled in, then queued for sending.
	 * @return A new message.
	 */
	public MimeMessage createEmptyMessage();
	
	/**
	 * Returns the email address which all emails should come from.
	 */
	public InternetAddress getFromAddress();
	
	/**
	 * Sends an email message asynchronously.  This method does not block.
	 * @param message The email message to send.
	 */
	public void queueMessage(Message message);
}
