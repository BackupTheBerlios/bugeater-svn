package bugeater.bean;

import java.io.Serializable;

import bugeater.domain.Priority;

/**
 * A bean that is used in the creation of an issue.
 * 
 * @author pchapman
 */
public class CreateIssueBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// CONSTRUCTORS

	/**
	 * Creates a new issue bean.
	 */
	public CreateIssueBean()
	{
		super();
	}
	
	// MEMBERS
	
	/**
	 * The categorization of the new issue.
	 */
	private String category;
	public String getCategory()
	{
		return category;
	}
	public CreateIssueBean setCategory(String category)
	{
		this.category = category;
		return this;
	}
	
	/**
	 * The creator of the new issue.  This user is associated with the first
	 * status, an Open status.  This user is also assigned as a watcher of the
	 * new issue.
	 */
	private IUserBean creator;
	public IUserBean getCreator()
	{
		return creator;
	}
	public CreateIssueBean setCreator(IUserBean creator)
	{
		this.creator = creator;
		return this;
	}
	
	/**
	 * A description of the new issue.  This info is used for the open status'
	 * note.
	 */
	private String description;
	public String getDescription()
	{
		return description;
	}
	// returns this for chaining.
	public CreateIssueBean setDescription(String description)
	{
		this.description = description;
		return this;
	}
	
	/**
	 * The priority of the new issue.
	 */
	private Priority priority;
	public Priority getPriority()
	{
		return priority;
	}
	public CreateIssueBean setPriority(Priority priority)
	{
		this.priority = priority;
		return this;
	}

	/**
	 * The project that the issue is for.
	 */
	private String project;
	public String getProject()
	{
		return project;
	}
	public CreateIssueBean setProject(String project)
	{
		this.project = project;
		return this;
	}

	/**
	 * A brief summary of the issue.  This should be as short, but descriptive
	 * and unique as possible.  Ideally, this is a one-liner.
	 */
	private String summary;
	public String getSummary()
	{
		return summary;
	}
	// returns this for chaining.
	public CreateIssueBean setSummary(String summary)
	{
		this.summary = summary;
		return this;
	}
}