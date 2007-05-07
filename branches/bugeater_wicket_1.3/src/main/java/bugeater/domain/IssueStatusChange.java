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

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Records a change in an issue's status.
 * 
 * @author pchapman
 */
@Entity
@Table (name = "be_issue_status_change")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IssueStatusChange
{
	/**
	 * Creates a new IssueStatusChange.
	 */
	protected IssueStatusChange()
	{
		super();
		this.changeTime = Calendar.getInstance();
	}
	
	/**
	 * Creates a new IssueStatusChange instance.
	 * <b>NOTE</b>This constructor is not public API and should never be
	 * called outside of the Service layer.
	 * @see bugeater.service.IssueService#changeStatus(Issue, IUserBean, IssueStatus, String)
	 **/
	public IssueStatusChange(Issue issue, String userId, IssueStatus newStatus)
	{
		this();
		this.issue = issue;
		this.userId = userId;
		this.status = newStatus;
	}
	
	@Id @GeneratedValue
	@Column(name="issue_status_change_id")
	private Long id;
	/** Unique ID **/
	public Long getId()
	{
		return id;
	}
	/** Unique ID **/
	protected IssueStatusChange setId(Long id)
	{
		this.id = id;
		return this;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="issue_id")
    private Issue issue;
	/** The issue whos status change is recorded by this object. **/
	public Issue getIssue()
	{
		return issue;
	}
	/**
	 *  The issue whos status change is recorded by this object.
	 * <b>NOTE</b>This method is not public API and should never be
	 * called outside of the Service layer.
	 * @see bugeater.service.IssueService#changeStatus(Issue, IUserBean, IssueStatus, String)
	 **/
	public IssueStatusChange setIssue(Issue issue)
	{
		this.issue = issue;
		return this;
	}
	
	// An issue may have any number of notes which are not related to a
	// status change.  However, each status change should have a note.
	@ManyToOne( cascade = {CascadeType.ALL} )
    @JoinColumn(name="note_id", nullable=true)
	private Note note;
	/** The note associated with status change. **/
	public Note getNote()
	{
		return note;
	}
	/** The note associated with status change. **/
	public void setNote(Note note)
	{
		this.note = note;
	}

	@Column( name="user_id", nullable=false )
    private String userId;
	/** The user that changed the issue's status. **/
	public String getUserId()
	{
		return userId;
	}
	/** The user that changed the issue's status. **/
	public IssueStatusChange setUserID(String userID)
	{
		this.userId = userID;
		return this;
	}
	
	@Column(name = "change_time", nullable=false)
	private Calendar changeTime;
	/** The date and time that the date and time has changed. **/
	public Calendar getChangeTime()
	{
		return changeTime;
	}
	/** The date and time that the date and time has changed. **/
	public IssueStatusChange setIssueStatusChange(Calendar time)
	{
		this.changeTime = time;
		return this;
	}
	
	@Column(name = "sort_order")
	@AccessType("field")
	protected int order;
	
	@Column( name="issue_status", nullable=false )
	private IssueStatus status;
	/** The status to which the issue has been changed. **/
	public IssueStatus getIssueStatus()
	{
		return status;
	}
	/** The status to which the issue has been changed. **/
	public IssueStatusChange setIssueStatus(IssueStatus status)
	{
		this.status = status;
		return this;
	}
}
