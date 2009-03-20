package bugeater.service.impl;

import org.junit.Test;

import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.Note;
import bugeater.domain.Priority;

/**
 * A class to test whether the mail service impl works.
 * 
 * @author pchapman
 */
public class MailServiceImplTest
{
	@Test
	public void testSend()
	{
		Issue issue = new Issue();
		issue.setCategory("Bug");
		issue.setCurrentStatus(IssueStatus.Open);
		issue.setPriority(Priority.Medium);
		issue.setProject("Test");
		issue.setSummary("Summary");
		
		Note note = new Note();
		note.setIssue(issue);
		note.setText("Text");
		note.setUserID("pchapman");
		
		MailServiceImpl msvc = new MailServiceImpl();
		msvc.setFromAddressString("bugeater@pcsw.us");
		msvc.setMailLogin("bugeater@pcsw.us");
		msvc.setMailPassword("1bug34t3r!r0#");
		msvc.setMailServer("smtp.gmail.com");
		msvc.setNotificationEmailAddressString("pchapman@pcsw.us");
		msvc.setPort(465);
		msvc.setSslUsed(true);
		msvc.emailNotePosted(null, note);
		
		try {
			do {
				Thread.sleep(50000L);	
			} while (msvc.getMailServiceThread().getQueueCount() > 0);
		} catch (InterruptedException e) {}
	}
}
