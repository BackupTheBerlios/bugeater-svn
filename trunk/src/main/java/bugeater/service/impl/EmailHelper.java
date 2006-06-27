package bugeater.service.impl;

import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import bugeater.domain.Issue;
import bugeater.domain.Note;
import bugeater.service.MailService;
import bugeater.service.UserService;

/**
 * A helper class with static methods to produce email text about objects.
 * 
 * @author pchapman
 */
class EmailHelper
{
	private static final Log logger = LogFactory.getLog(EmailHelper.class);

	/**
	 * No instantiation allowed.
	 */
	private EmailHelper()
	{
		super();
	}

	/**
	 * A helper method that will create a text summary of the issue.
	 */
	static String createTextDescription(Issue issue)
	{
		StringBuilder sb = new StringBuilder();
		
		ResourceBundle resource = ResourceBundle.getBundle(EmailHelper.class.getName());
		
		addRow(sb, resource, "issue.id", issue.getId().toString());
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
	 * A helper method that will email a notice about the new note.
	 * 
	 * @param mailService The mail service that will deliver the email.
	 * @param userService A service that will allow user info to be retrieved.
	 * @param note The note that is being reported on.
	 */
	static void emailNotePosted(
			MailService mailService, UserService userService, Note note
		)
	{
		ResourceBundle resource = ResourceBundle.getBundle(EmailHelper.class.getName());
		
		StringBuilder sb = new StringBuilder(resource.getString("note.posted.body"));
		sb.append("\n\nIssue: ");
		sb.append(createTextDescription(note.getIssue()));
		sb.append("\n\nNew Note: ");
		sb.append(note.getText());

		emailToWatchers(
				mailService, userService,
				resource.getString("note.posted.subject"),
				sb.toString(), note.getIssue()
			);
	}
	
	/**
	 * A helper method that will email a notice about the status change.
	 * @param mailService The mail service that will deliver the email.
	 * @param userService A service that will allow user info to be retrieved.
	 * @param issue The issue that is being reported on.
	 */
	static void emailStatusChange(
			MailService mailService, UserService userService, Issue issue
		)
	{
		ResourceBundle resource = ResourceBundle.getBundle(EmailHelper.class.getName());
		
		StringBuilder sb = new StringBuilder(resource.getString("issue.status_changed.body"));
		sb.append('\n');
		sb.append(createTextDescription(issue));

		emailToWatchers(
				mailService, userService,
				resource.getString("issue.status_changed.subject"),
				sb.toString(), issue
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
	private static void emailToWatchers(
			MailService mailService, UserService userService,
			String subject, String text, Issue i
		)
	{
		try {
			MimeMessage msg = mailService.createEmptyMessage();
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
			mailService.queueMessage(msg);
		} catch (MessagingException me) {
			logger.error(me);
		}
	}
}
