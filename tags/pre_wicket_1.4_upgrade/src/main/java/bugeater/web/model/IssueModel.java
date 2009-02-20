package bugeater.web.model;

import bugeater.domain.Issue;

import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.IModel;

/**
 * A model used to provide an Issue object to the component.
 * 
 * @author pchapman
 */
public class IssueModel implements IModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 * @param issueID The unique ID of the issue.
	 */
	public IssueModel(Long issueID)
	{
		super();
		this.issueid = issueID;
	}
	
	/**
	 * Creates a new instance.
	 * @param issue The issue.
	 */
	public IssueModel(Issue issue)
	{
		this(issue == null ? null : issue.getId());
	}

	// MEMBERS
	
	private Long issueid;
	private transient Issue issue;

	public void detach()
	{
		issue = null;
	}

	public Issue getObject()
	{
		if (issue == null && issueid != null) {
			IssueService iService =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			return iService.load(issueid);
		}
		return issue;
	}
	
	public void setObject(Issue issue)
	{
		this.issue = issue;
		issueid = issue == null ? null : issue.getId();
	}
}
