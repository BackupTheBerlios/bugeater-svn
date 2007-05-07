package bugeater.web.model;

import bugeater.domain.Issue;

import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;

/**
 * A model used to provide an Issue object to the component.
 * 
 * @author pchapman
 */
public class IssueModel implements IModel
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
		super();
		setObject(issue);
	}

	// MEMBERS
	
	private Long issueid;
	private transient Issue issue;

	
	/**
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach()
	{
		issue = null;
	}

	/**
	 * @see org.apache.wicket.model.IModel#getObject()
	 */
	public Object getObject()
	{
		if (issue == null && issueid != null) {
			IssueService iService =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			issue = iService.load(issueid);
		}
		return issue;
	}

	/**
	 * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Object object)
	{
		if (object == null) {
			issue = null;
			issueid = null;
		} else if (object instanceof Issue) {
			this.issue = (Issue)object;
			this.issueid = issue.getId();
		}
	}
}
