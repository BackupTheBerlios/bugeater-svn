package bugeater.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import bugeater.bean.CreateIssueBean;
import bugeater.bean.IUserBean;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.IssueStatusChange;

/**
 * An interface that defines the API that a service class that deals with
 * manipulating a CreateIssueBean must implement.
 * 
 * @author pchapman
 */
public interface IssueService
{
	/**
	 * Changes the status of the issue.  This will create a new
	 * IssueStatusChange which is added to the statuses of this issue (no need
	 * for the user to add it again).
	 * @param userBean The user who is changing the status.
	 * @param newStatus The new status for the issue.
	 */
	@Transactional
	public IssueStatusChange changeStatus(
			Issue issue, IUserBean userBean,
			IssueStatus newStatus, String note
		);

	/**
	 * Creates and saves a new issue based on the information contained in the
	 * bean.
	 * @param iBean The bean which contains information to be used to create
	 *              the new issue.
	 */
	@Transactional
	public Issue createIssue(CreateIssueBean iBean);
	
	/**
	 * Loads the issue class instance by unique ID.
	 */
	public Issue load(Long id);
	
	/**
	 * Saves changes to the issue record.
	 */
	@Transactional
	public void save(Issue i);
	
	@Transactional
	public void addCategory(String category);
	
	/**
	 * A list of categories into which issues may be assigned.
	 */
	@Transactional
	public List<String>getCategoriesList();
	
	/**
	 * A list of issues that have been changed to the given status by the
	 * given user.
	 */
	public List<Issue>getIssuesByStatusChange(
			IssueStatus status, IUserBean bean
		);
	
	@Transactional
	public void addProject(String project);
	
	/**
	 * A list of projects to which issues may be assigned.
	 */
	public List<String>getProjectsList();
	
	/**
	 * Gets all issues that have not been closed.
	 */
	public List<Issue>getPendingIssues();
	
	/**
	 * A list of issues assigned to the user that are not closed.
	 * @param assignedUserID The unique ID of the user to which the issues are
	 *                       assigned.
	 */
	public List<Issue>getPendingIssuesByAssigned(String assignedUserID);
	
	/**
	 * Gets all issues that the given current status.
	 * 
	 * @param status The status to search for.
	 */
	public List<Issue>getIssuesByCurrentStatus(IssueStatus status);
	
	/**
	 * A list of issues being watched by the user with the given unique ID.
	 * @param watcherUserID The unique ID of the user doing the watching.
	 */
	public List<Issue>getPendingWatchedIssues(String watcherUserID);
}
