package bugeater.domain;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Represents an attachment to an issue.
 * 
 * @author pchapman
 */
@Entity
@Table (name = "be_attachment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Attachment
{
	/**
	 * Creates a new instance.
	 */
	public Attachment()
	{
		super();
	}
	
	// MEMBERS
	
	private String contentType;
	/** The type of file */
	public String getContentType()
	{
		return contentType;
	}
	/** The type of file */
	public Attachment setContentType(String contentType)
	{
		this.contentType = contentType;
		return this;
	}
	
	@Column( name="created" )
	private Calendar created;
	/** The data and time that the attachment was created. **/
	public Calendar getCreated()
	{
		return created;
	}
	/** The data and time that the attachment was created. **/
	protected void setCreated(Calendar created)
	{
		this.created = created;
	}
	
	@Column( name="file_name" )
	private String fileName;
	/** The name of the file as uploaded. */
	public String getFileName()
	{
		return fileName;
	}
	/** The name of the file as uploaded. */
	public Attachment setFileName(String fileName)
	{
		this.fileName = fileName;
		return this;
	}
	
	@Id @GeneratedValue
	@Column(name="attachment_id")
	private Long id;
	/** The unique ID of the attachment. **/
	public Long getId()
	{
		return id;
	}
	/** The unique ID of the attachment. **/
	protected Attachment setId(Long id)
	{
		this.id = id;
		return this;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="issue_id")
    private Issue issue;
	/** The issue that this attachment is associated with. **/
	public Issue getIssue()
	{
		return issue;
	}
	/** The issue that this attachment is associated with. **/
	public Attachment setIssue(Issue issue)
	{
		this.issue = issue;
		return this;
	}
	
	@Column( name="storage_name" )
	private String storageName;
	/** The name of the file in the storage directory. */
	public String getStorageName()
	{
		return storageName;
	}
	/** The name of the file in the storage directory. */
	public void setStorageName(String storageName)
	{
		this.storageName = storageName;
	}

	// METHODS
}
