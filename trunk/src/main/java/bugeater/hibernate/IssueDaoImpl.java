package bugeater.hibernate;

import java.util.Collections;
import java.util.List;

import bugeater.bean.IUserBean;
import bugeater.dao.IssueDao;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.ReleaseVersion;

/**
 * An implementation of the bugeater.dao.IssueDao interface.
 * 
 * @author pchapman
 */
public class IssueDaoImpl extends AbstractHibernateDao<Issue>
	implements IssueDao
{
	/**
	 * Creates a new implementation.
	 * 
	 * @param dataClass
	 */
	public IssueDaoImpl()
	{
		super(Issue.class);
	}
	
	/**
	 * @see bugeater.dao.IssueDao#getIssuesByReleaseVersion(ReleaseVersion)
	 */
	@SuppressWarnings("unchecked")
	public List<Issue>getIssuesByReleaseVersion(ReleaseVersion version)
	{
		if (version == null) {
			return Collections.<Issue>emptyList();
		}
		return (List<Issue>)getSession()
			.createQuery(
					"select i " +
					"from Issue i " +
					"where i.releaseVersion = :version " +
					"order by i.priority desc"
			).setParameter("version", version).list();
	}

	/**
	 * @see bugeater.dao.IssueDao#getIssuesByStatusChange(IssueStatus, IUserBean)
	 */
	@SuppressWarnings("unchecked")
	public List<Issue>getIssuesByStatusChange(
			IssueStatus status, IUserBean bean
		)
	{
		if (status == null) {
			return Collections.<Issue>emptyList();
		}
		return (List<Issue>)getSession()
			.createQuery(
					"select i " +
					"from IssueStatusChange c join c.issue as i " +
					"where c.status = :status and c.userId = :user " +
					"order by c.changeTime desc"
			).setParameter("status", status)
			.setParameter("user", bean.getId()).list();
	}
	
	/**
	 * @see bugeater.dao.IssueDao#getPendingIssues()
	 */
	@SuppressWarnings("unchecked")
	public List<Issue>getPendingIssues()
	{
		StringBuilder sb =
			new StringBuilder(
					"select i.* " +
					"from be_issue i " +
					"where i.current_status in ( "
				);
		boolean first = true;
		for (IssueStatus status : IssueStatus.PENDING_STATUSES) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(status.ordinal());
		}
		sb.append(" ) order by i.priority desc");
		
		return (List<Issue>)getSession()
			.createSQLQuery(sb.toString())
			.addEntity(Issue.class)
			.list();
	}

	/**
	 * @see bugeater.dao.IssueDao#getPendingIssuesByAssigned(String)
	 */
	@SuppressWarnings("unchecked")
	public List<Issue>getPendingIssuesByAssigned(String assignedUserID)
	{
		StringBuilder sb =
			new StringBuilder(
					"select i.* " +
					"from be_issue i " +
					"where i.assigned_user_id = :userid and " +
					" i.current_status in ( "
				);
		boolean first = true;
		for (IssueStatus status : IssueStatus.PENDING_STATUSES) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(status.ordinal());
		}
		sb.append(" ) order by i.priority desc");
		
		return (List<Issue>)getSession()
			.createSQLQuery(sb.toString())
			.addEntity(Issue.class)
			.setParameter("userid", assignedUserID)
			.list();
	}
	
	/**
	 * @see bugeater.dao.IssueDao#getPendingIssuesByCurrentStatus(IssueStatus)
	 */
	@SuppressWarnings("unchecked")
	public List<Issue>getIssuesByCurrentStatus(IssueStatus status)
	{
		return (List<Issue>)getSession().createQuery(
				"select i from Issue i where i.currentStatus = :status"
			).setParameter("status", status).list();
	}

	/**
	 * @see bugeater.dao.IssueDao#getPendingWatchedIssues(String)
	 */
	@SuppressWarnings("unchecked")
	public List<Issue>getPendingWatchedIssues(String watcherUserID)
	{
		StringBuilder sb =
			new StringBuilder(
					"select i.* " +
					"from be_issue_watcher w " +
					" join be_issue i on i.issue_id = w.issue_id " +
					"where w.watcher_user_id = :userid and " +
					" i.current_status in ("
				);
		boolean first = true;
		for (IssueStatus status : IssueStatus.PENDING_STATUSES) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(status.ordinal());
		}
		sb.append(" ) order by i.priority desc");
		
		return (List<Issue>)getSession()
			.createSQLQuery(sb.toString())
			.addEntity(Issue.class)
			.setParameter("userid", watcherUserID)
			.list();
	}
}
