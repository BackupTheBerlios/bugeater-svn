package bugeater.dao;

import java.util.List;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;

/**
 * An interface that defines data access methods for Attachment objects.
 * 
 * @author pchapman
 */
public interface AttachmentDao
{
	/**
	 * Gets a list of attachments related to an issue.
	 */
	public List<Attachment>getAttachments(Issue i);
	
	/**
	 * Loads a specific CreateIssueBean by ID.
	 */
	public Attachment load(Long id);

	/**
	 * Saves changes to the issue.
	 */
	public void save(Attachment attachment);
}
