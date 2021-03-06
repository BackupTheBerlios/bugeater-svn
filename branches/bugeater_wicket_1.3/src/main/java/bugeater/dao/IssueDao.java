package bugeater.dao;

import java.util.List;

import bugeater.bean.IUserBean;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.ReleaseVersion;

/**
 * An interface that defines data access methods for CreateIssueBean objects.
 * 
 * @author pchapman
 */
public interface IssueDao
{
	/**
	 * Delets the indicated issue.
	 * 
	 * @param issue
	 */
	public void delete(Issue issue);
	
	/**
	 * Gets all issues that have the given current status.
	 * 
	 * @param status The status to search for.
	 */
	public List<Issue>getIssuesByCurrentStatus(IssueStatus status);

	/**
	 * A list of issues that have been changed to the given status by the
	 * given user.
	 */
	public List<Issue>getIssuesByStatusChange(
			IssueStatus status, IUserBean bean
		);
	
	/**
	 * Gets all issues related to a particular project.
	 * 
	 * @param status The status to search for.
	 */
	public List<Issue>getIssuesByProject(String project);
	
	/**
	 * Gets all issues that are scheduled for the given release.
	 * 
	 * @param status The status to search for.
	 */
	public List<Issue>getIssuesByReleaseVersion(ReleaseVersion version);
	
	/**
	 * Gets all issues that have not been closed.
	 */
	public List<Issue>getPendingIssues();
		
	/**
	 * Gets all issues assigned to the user that have not been closed.
	 * 
	 * @param assignedUserID The ID of the user.
	 */
	public List<Issue>getPendingIssuesByAssigned(String assignedUserID);

	/**
	 * Gets all issues being watched by the user that have not been closed.
	 * 
	 * @param watcherUserID The ID of the user.
	 */
	public List<Issue>getPendingWatchedIssues(String watcherUserID);

	/**
	 * Loads a specific CreateIssueBean by ID.
	 */
	public Issue load(Long id);

	/**
	 * Saves changes to the issue.
	 */
	public void save(Issue issue);
}
