package bugeater.web.model;

import bugeater.domain.Issue;

import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;

import wicket.Application;

/**
 * A model used to provide an Issue object to the component.
 * 
 * @author pchapman
 */
public class IssueModel extends MutableDetachableModel<Issue>
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
		super(issue);
		this.issueid = issue == null ? null : issue.getId();
	}

	// MEMBERS
	
	private Long issueid;

	/**
	 * @see bugeater.web.model.MutableDetachableModel#detach()
	 */
	@Override
	public void detach()
	{
		issueid = getObject().getId();
		super.detach();
	}

	/**
	 * @see bugeater.web.model.MutableDetachableModel#load()
	 */
	@Override
	protected Issue load()
	{
		if (issueid == null) {
			return null;
		} else {
			IssueService iService =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			return iService.load(issueid);
		}
	}
}
