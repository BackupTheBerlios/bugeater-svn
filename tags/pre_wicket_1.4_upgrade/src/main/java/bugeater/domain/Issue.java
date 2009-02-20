package bugeater.domain;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;

/**
 * Domain object to represent an issue.
 * 
 * @author pchapman
 */
@Entity
@Table (name = "be_issue")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Issue
{
	// CONSTRUCTORS

	/**
	 * Creates a new interval with the current date as start time and null as
	 * end time.
	 */
	public Issue()
	{
		super();
		openTime = Calendar.getInstance();
	}
	
	// MEMBERS

	@Column( name="assigned_user_id", nullable=true )
    private String assignedUserID;
	/** The user to which this issue has been assigned. **/
	public String getAssignedUserID()
	{
		return assignedUserID;
	}
	/** The user to which this issue has been assigned. **/
	public Issue setAssignedUserID(String userID)
	{
		this.assignedUserID = userID;
		return this;
	}
	
	@Column( name="category" )
	private String category;
	/** A categorization for the issue. **/
	public String getCategory()
	{
		return category;
	}
	/** A categorization for the issue. **/
	public Issue setCategory(String category)
	{
		this.category = category;
		return this;
	}
	
	@Column(name = "close_time", nullable=true)
	private Calendar closeTime;
	/** The date and time that the issue was created. **/
	public Calendar getCloseTime()
	{
		return closeTime;
	}
	/** The date and time that the issue was created. **/
	public Issue setCloseTime(Calendar closeTime)
	{
		this.closeTime = closeTime;
		return this;
	}
	
	@Column( name="current_status", nullable=false )
	private IssueStatus currentStatus;
	/** The current status of the issue **/
	public IssueStatus getCurrentStatus()
	{
		return currentStatus;
	}
	/**
	 * The current status of the issue.
	 * <b>NOTE</b>This member setter is not public API and should never be
	 * called outside of the Service layer.
	 * @see bugeater.service.IssueService#changeStatus(Issue, IUserBean, IssueStatus, String)
	 **/
	public Issue setCurrentStatus(IssueStatus status)
	{
		this.currentStatus = status;
		return this;
	}
	
	@Id @GeneratedValue
	@Column(name="issue_id")
	private Long id;
	/** The unique ID of the issue. **/
	public Long getId()
	{
		return id;
	}
	/** The unique ID of the issue. **/
	protected Issue setId(Long id)
	{
		this.id = id;
		return this;
	}

	@Column(name = "open_time", nullable=true)
	private Calendar openTime;
	/** The date and time that the issue is opened. **/
	public Calendar getOpenTime()
	{
		return openTime;
	}
	/** The date and time that the issue is opened. **/
	protected Issue setOpenTime(Calendar time)
	{
		this.openTime = time;
		return this;
	}

	@OneToMany( mappedBy="issue" ) @OrderBy( "createTime" )
	private List<Note>notes = new LinkedList<Note>();
	/** Notes related to an issue. **/
	public List<Note>getNotes()
	{
		return notes;
	}
	/** Notes related to an issue. **/
	protected Issue setNotes(List<Note> notes)
	{
		this.notes = notes;
		return this;
	}
	
	@Column( name="priority", nullable=false )
	private Priority priority;
	/** The priority of an issue. **/
	public Priority getPriority()
	{
		return priority;
	}
	/** The priority of an issue. **/
	public Issue setPriority(Priority priority)
	{
		this.priority = priority;
		return this;
	}
	
	@Column( name="project" )
	private String project;
	/** The project that the issue is related to. **/
	public String getProject()
	{
		return project;
	}
	/** The project that the issue is related to. **/
	public Issue setProject(String project)
	{
		this.project = project;
		return this;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="release_version_id")
    private ReleaseVersion releaseVersion;
	public ReleaseVersion getReleaseVersion()
	{
		return releaseVersion;
	}
	// returns this for chaining.
	public Issue setReleaseVersion(ReleaseVersion release)
	{
		this.releaseVersion = release;
		return this;
	}

	@OneToMany( mappedBy="issue", cascade=CascadeType.ALL ) @OrderBy( "order" )
	private List<IssueStatusChange>statusChanges =
		new LinkedList<IssueStatusChange>();
	/**
	 * A list of status changes for the issue.
	 * <b>NOTE</b>This member's list should never be modified outside of the
	 * Service layer.
	 * @see bugeater.service.IssueService#changeStatus(Issue, IUserBean, IssueStatus, String)
	 **/
	public List<IssueStatusChange>getStatusChanges()
	{
		return statusChanges;
	}
	/** A list of status changes for the issue. */
	protected Issue setStatusChanges(List<IssueStatusChange> changes)
	{
		this.statusChanges = changes;
		return this;
	}
	
	@Column(name = "summary", nullable=true)
	private String summary;
	/**
	 * A brief summary of the issue.  This should be as short, but descriptive
	 * and unique as possible.  Ideally, this is a one-liner.
	 */
	public String getSummary()
	{
		return summary;
	}
	/**
	 * A brief summary of the issue.  This should be as short, but descriptive
	 * and unique as possible.  Ideally, this is a one-liner.
	 */
	public Issue setSummary(String summary)
	{
		this.summary = summary;
		return this;
	}

	@Version
	@Column(name = "version")
	@AccessType("field")
	protected long version;

	@CollectionOfElements
	@JoinTable(
            name="be_issue_watcher",
            joinColumns = @JoinColumn(name="issue_id")
    )
    @Column(name="watcher_user_id", nullable=false)
	private Set<String>watchers = new HashSet<String>();
	/** A set of user ids of those users that are watching this issue. **/
	public Set<String>getWatchers()
	{
		return watchers;
	}
	/** A set of user ids of those users that are watching this issue. **/
	protected Issue setWatchers(Set<String> watchers)
	{
		this.watchers = watchers;
		return this;
	}
}
