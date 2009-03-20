package bugeater.domain;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A release version for software update.
 * 
 * @author pchapman
 */
@Entity
@Table (name = "be_release_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReleaseVersion
{
	/**
	 * Creates a new instance.
	 */
	public ReleaseVersion()
	{
		super();
		issues = new HashSet<Issue>();
	}
	
	@Column(name = "actual_release_date", nullable=true)
	private Date actualReleaseDate;
	/**
	 * The date the release was made.
	 */
	public Date getActualReleaseDate()
	{
		return actualReleaseDate;
	}
	public ReleaseVersion setActualReleaseDate(Date date)
	{
		this.actualReleaseDate = date;
		return this;
	}
	
	@Column( name="project" )
	private String project;
	/** The project of which this release is. **/
	public String getProject()
	{
		return project;
	}
	/** The project of which this release is. **/
	public ReleaseVersion setProject(String project)
	{
		this.project = project;
		return this;
	}
	
	@Id @GeneratedValue
	@Column(name="release_version_id")
	private Long id;
	/** The unique ID of the issue. **/
	public Long getId()
	{
		return id;
	}
	/** The unique ID of the issue. **/
	protected ReleaseVersion setId(Long id)
	{
		this.id = id;
		return this;
	}
	
	@OneToMany( mappedBy="releaseVersion" )
	private Set<Issue> issues;
	/**
	 * The issues assigned to be fixed in the release.
	 */
	public Set<Issue> getIssues()
	{
		return issues;
	}
	protected ReleaseVersion setIssues(Set<Issue> issues)
	{
		this.issues = issues;
		return this;
	}
	
	@Column(name = "scheduled_release_date", nullable=false)
	private Date scheduledReleaseDate;
	/**
	 * The date scheduled for the release.
	 */
	public Date getScheduleReleaseDate()
	{
		return scheduledReleaseDate;
	}
	public ReleaseVersion setScheduleReleaseDate(Date date)
	{
		this.scheduledReleaseDate = date;
		return this;
	}

	/**
	 * A field used to track changes in the database record.
	 */
	@Version
	@Column(name = "version")
	@AccessType("field")
	protected long version;
	
	@Column(name = "version_number", nullable=false)
	private String versionNumber;
	/**
	 * The version number of the release.
	 */
	public String getVersionNumber()
	{
		return versionNumber;
	}
	public ReleaseVersion setVersionNumber(String number)
	{
		this.versionNumber = number;
		return this;
	}
	
	public String toString()
	{
		return getVersionNumber();
	}
}
