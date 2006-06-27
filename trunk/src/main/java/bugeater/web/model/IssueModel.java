package bugeater.web.model;

import bugeater.domain.Issue;

import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;

/**
 * A model used to provide an Issue object to the component.
 * 
 * @author pchapman
 */
public class IssueModel extends AbstractDetachableModel<Issue>
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
		this.issue = null;
		this.issueid = issueID;
		onAttach();
	}
	
	/**
	 * Creates a new instance.
	 * @param issue The issue.
	 */
	public IssueModel(Issue issue)
	{
		super();
		this.issueid = issue == null ? null : issue.getId();
		this.issue = issue;
	}

	// MEMBERS
	
	private Issue issue;
	private Long issueid;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	// METHODS
	
	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (issue == null && issueid != null) {
			IssueService iService =
				(IssueService)((BugeaterApplication)Application.get())
				.getSpringContextLocator().getSpringContext()
				.getBean("issueService");
			issue = iService.load(issueid);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		issue = null;		
	}
	
	

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected Issue onGetObject()
	{
		return issue;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(<T>)
	 */
	@Override
	protected void onSetObject(Issue issue) 
	{
		// Not implemented
	}	
}
