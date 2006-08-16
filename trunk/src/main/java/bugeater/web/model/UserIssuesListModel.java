package bugeater.web.model;

import java.util.List;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;

/**
 * Provides a list of all issues associated with a user.  How the issue is
 * associated with the user depends on the AssociationType pararameter in the
 * constructor.  The issues are ordered by priority.
 * 
 * @author pchapman
 */
public class UserIssuesListModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	public enum AssociationType {Assigned, Watched};
	
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 * @param assignedUserID The unique ID of the user to which the issues are
	 *                       assigned.
	 */
	public UserIssuesListModel(AssociationType type, String userID)
	{
		super();
		this.associationType = type;
		this.userID = userID;
	}
	
	// MEMBERS
	
	private AssociationType associationType;
	
	private String userID;
	
	private List<Issue> issues;
	
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
		if (issues == null && userID != null) {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			switch (associationType) {
			case Assigned:
				issues = service.getPendingIssuesByAssigned(userID); break;
			case Watched:
				issues = service.getPendingWatchedIssues(userID); break;
			}
		} 
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		issues = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return issues;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// Not implemented
	}
}
