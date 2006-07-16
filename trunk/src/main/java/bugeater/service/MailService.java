package bugeater.service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import bugeater.domain.Issue;
import bugeater.domain.Note;

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
	 * Generates an email concerning the posting of a new note to an issue and
	 * sends it to all the related issue's watchers.
	 * @param userService The service that can be used to get user (watcher)
	 *                    email addresses.
	 * @param note The note being reported on.
	 */
	public void emailNotePosted(
			UserService userService, Note note
		);

	/**
	 * Generates an email concering the status change of a note.  The email is
	 * sent to all the issue's watchers.
	 * @param userService The service that can be used to get user (watcher)
	 *                    email addresses.
	 * @param issue The issue being reported on.
	 */
	public void emailStatusChange(
			UserService userService, Issue issue
		);
	
	/**
	 * Sends an email message asynchronously.  This method does not block.
	 * @param message The email message to send.
	 */
	public void queueMessage(Message message);
}
