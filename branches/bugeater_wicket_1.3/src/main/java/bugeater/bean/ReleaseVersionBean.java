package bugeater.bean;

import java.io.Serializable;
import java.util.Calendar;

import bugeater.domain.ReleaseVersion;

/**
 * A bean class that holds data that can be used by forms that create or modify
 * a ReleaseVersion object.
 * 
 * @author pchapman
 */
public class ReleaseVersionBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance, suitable for creating a new ReleaseVersion.
	 */
	public ReleaseVersionBean()
	{
		super();
	}
	
	/**
	 * Creates a new instance, suitable for editing an existing bean.
	 * @param release
	 */
	public ReleaseVersionBean(ReleaseVersion release)
	{
		super();
		setActualReleaseDate(release.getActualReleaseDate());
		setId(release.getId());
		setProject(release.getProject());
		setScheduledReleaseDate(release.getScheduleReleaseDate());
		setVersionNumber(release.getVersionNumber());
	}
	
	private Calendar actualReleaseDate;
	/**
	 * The date the release was made.
	 */
	public Calendar getActualReleaseDate()
	{
		return actualReleaseDate;
	}
	public ReleaseVersionBean setActualReleaseDate(Calendar date)
	{
		this.actualReleaseDate = date;
		return this;
	}
	
	private String project;
	/** The project of which this release is. **/
	public String getProject()
	{
		return project;
	}
	/** The project of which this release is. **/
	public ReleaseVersionBean setProject(String project)
	{
		this.project = project;
		return this;
	}
	
	private Long id;
	/** The unique ID of the issue. **/
	public Long getId()
	{
		return id;
	}
	/** The unique ID of the issue. **/
	protected ReleaseVersionBean setId(Long id)
	{
		this.id = id;
		return this;
	}
	
	private Calendar scheduledReleaseDate;
	/**
	 * The date scheduled for the release.
	 */
	public Calendar getScheduledReleaseDate()
	{
		return scheduledReleaseDate;
	}
	public ReleaseVersionBean setScheduledReleaseDate(Calendar date)
	{
		this.scheduledReleaseDate = date;
		return this;
	}
	
	private String versionNumber;
	/**
	 * The version number of the release.
	 */
	public String getVersionNumber()
	{
		return versionNumber;
	}
	public ReleaseVersionBean setVersionNumber(String number)
	{
		this.versionNumber = number;
		return this;
	}
}
