package bugeater.service;

import java.io.IOException;
import java.io.InputStream;

import java.util.Calendar;
import java.util.List;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;

import org.springframework.transaction.annotation.Transactional;

/**
 * An interface that defines the API of a service class that deals with
 * manipulating a CreateIssueBean must implement.
 * 
 * @author pchapman
 */
public interface AttachmentService
{
	/**
	 * Gets the data of the attachment.
	 */
    public byte[] getData(Attachment attachment) throws IOException;

    /**
     * Gets the last date and time that the attachment was modified.
     */
	public Calendar getLastModifyTime(Attachment attachment);
	
	/**
	 * Gets the attachment with the given id.
	 */
	public Attachment load(Long id);
	
	/**
	 * Saves the attachment and stores the data.
	 */
	@Transactional
	public void save(Attachment attachment, InputStream inputStream)
		throws IOException;
	
	/**
	 * Gets a list of attachments for the given issue.
	 */
	public List<Attachment>getAttachments(Issue i);
}
