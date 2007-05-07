package bugeater.web.model;

import java.util.Collections;
import java.util.List;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;

import wicket.Application;

/**
 * Provides a list of all issues associated with a user.  How the issue is
 * associated with the user depends on the AssociationType pararameter in the
 * constructor.  The issues are ordered by priority.
 * 
 * @author pchapman
 */
public class UserIssuesListModel extends AbstractDetachableEntityListModel<Issue>
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

	// METHODS
	
	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected List<Issue> load()
	{
		List<Issue> issues = null;
		if (userID == null) {
			return Collections.emptyList();
		} else {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			switch (associationType) {
			case Assigned:
				issues = service.getPendingIssuesByAssigned(userID); break;
			case Watched:
				issues = service.getPendingWatchedIssues(userID); break;
			}
		} 
		return issues;
	}
}
