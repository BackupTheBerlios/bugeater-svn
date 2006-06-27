package bugeater.service.impl;

import java.util.ArrayList;
import java.util.List;

import bugeater.bean.CreateIssueBean;
import bugeater.bean.IUserBean;
import bugeater.dao.IssueDao;
import bugeater.dao.LookupValueDao;

import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.IssueStatusChange;
import bugeater.domain.LookupValue;
import bugeater.domain.Note;

import bugeater.service.IssueService;
import bugeater.service.MailService;
import bugeater.service.SearchService;
import bugeater.service.UserService;

/**
 * An implimentation of the IssueService.
 * 
 * @author pchapman
 */
public class IssueServiceImpl implements IssueService
{
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 */
	public IssueServiceImpl()
	{
		super();
	}
	
	// MEMBERS

	private static final String[] DEFAULT_CATEGORIES = {
		"Bug","Functionality Change Request","New Functionality Request"
	};
	/**
	 * A list of categories into which issues may be assigned.
	 */
	public List<String>getCategoriesList()
	{
		List<String> list = new ArrayList<String>();
		for (LookupValue value : lvDao.load(LookupValue.ValueType.Category)) {
			list.add(value.getValue());
		}
		if (list.size() == 0) {
			// Load in some default values
			LookupValue lv;
			for (String s : DEFAULT_CATEGORIES) {
				lv = new LookupValue(LookupValue.ValueType.Category, s);
				lvDao.save(lv);
				list.add(s);
			}
		}
		return list;
	}
	
	/**
	 * @see bugeater.service.IssueService#getIssuesByStatusChange(IssueStatus, IUserBean)
	 */
	public List<Issue>getIssuesByStatusChange(
			IssueStatus status, IUserBean bean
		)
	{
		return issueDao.getIssuesByStatusChange(status, bean);
	}
	
	// Injected by spring
	private IssueDao issueDao;
	public void setIssueDao(IssueDao dao)
	{
		this.issueDao = dao;
	}
	
	// Injected by spring
	private LookupValueDao lvDao;
	public void setLookupValueDao(LookupValueDao dao)
	{
		this.lvDao = dao;
	}
	
	// Injected by spring
	private MailService mailService;
	public void setMailService(MailService service)
	{
		this.mailService = service;
	}
	
	// Injected by spring
	private SearchService searchService;
	public void setSearchService(SearchService service)
	{
		this.searchService = service;
	}
	
	// Injected by spring
	private UserService userService;
	public void setUserService(UserService service)
	{
		this.userService = service;
	}

	/**
	 * @see bugeater.service.IssueService#getPendingIssues()
	 */
	public List<Issue> getPendingIssues()
	{
		return issueDao.getPendingIssues();
	}
	
	/**
	 * @see bugeater.service.IssueService#getPendingIssuesByAssigned(String)
	 */
	public List<Issue>getPendingIssuesByAssigned(String assignedUserID)
	{
		return issueDao.getPendingIssuesByAssigned(assignedUserID);
	}

	/**
	 * @see bugeater.service.IssueService#getPendingIssuesByCurrentStatus(bugeater.domain.IssueStatus)
	 */
	public List<Issue> getIssuesByCurrentStatus(IssueStatus status)
	{
		return issueDao.getIssuesByCurrentStatus(status);
	}
	
	/**
	 * @see bugeater.service.IssueService#getPendingWatchedIssues(String)
	 */
	public List<Issue>getPendingWatchedIssues(String watcherUserID)
	{
		return issueDao.getPendingWatchedIssues(watcherUserID);
	}
	
	/**
	 * A list of projects to which issues may be assigned.
	 */
	public List<String>getProjectsList()
	{
		List<String> list = new ArrayList<String>();
		for (LookupValue value : lvDao.load(LookupValue.ValueType.Project)) {
			list.add(value.getValue());
		}
		return list;
	}
	
	// METHODS
	
	/**
	 * @see bugeater.service.IssueService#addCategory(String)
	 */
	public void addCategory(String category)
	{
		LookupValue lv = lvDao.load(LookupValue.ValueType.Category, category);
		if (lv == null) {
			lv = new LookupValue(LookupValue.ValueType.Category, category);
			lvDao.save(lv);
		}
	}
	
	/**
	 * @see bugeater.service.IssueService#addProject(String)
	 */
	public void addProject(String project)
	{
		LookupValue lv = lvDao.load(LookupValue.ValueType.Project, project);
		if (lv == null) {
			lv = new LookupValue(LookupValue.ValueType.Project, project);
			lvDao.save(lv);
		}
	}
	
	/**
	 * @see bugeater.service.IssueService#changeStatus(bugeater.bean.IUserBean, bugeater.domain.IssueStatus, java.lang.String)
	 */
	public IssueStatusChange changeStatus(
			Issue issue,  IUserBean userBean,
			IssueStatus newStatus, String note
		)
	{
		IssueStatusChange isc =
			changeStatusImpl(issue, userBean, newStatus, note);
		issueDao.save(issue);
		EmailHelper.emailStatusChange(mailService, userService, issue);
		return isc;
	}

	/**
	 * Does the actual work of changing an issue's status but does not save
	 * the changes.
	 * @param issue The issue whos status is being changed.
	 * @param userBean The user who is changing the status.
	 * @param newStatus The status being changed to.
	 * @param note A note about the status change.
	 */
	private IssueStatusChange changeStatusImpl(
			Issue issue,  IUserBean userBean,
			IssueStatus newStatus, String note
		)
	{
		//TODO keep user from changing status to same as current?
		IssueStatusChange isc =
			new IssueStatusChange(issue, userBean.getId(), newStatus);
		issue.getStatusChanges().add(isc);
		issue.setCurrentStatus(newStatus);
		Note n =
			new Note()
			.setIssue(issue)
			.setText(note)
			.setUserID(userBean.getId());
		isc.setNote(n);
		
		return isc;
	}
	
	/**
	 * @see bugeater.service.IssueService#createIssue(bugeater.bean.AddIssueBean)
	 */
	public Issue createIssue(CreateIssueBean iBean)
	{
		Issue i = new Issue();
		
		i.setCategory(iBean.getCategory());
		i.setPriority(iBean.getPriority());
		i.setProject(iBean.getProject());
		i.setSummary(iBean.getSummary());
		changeStatusImpl(
				i, iBean.getCreator(), IssueStatus.Open, iBean.getDescription()
			);
		i.getWatchers().add(iBean.getCreator().getId());
		
		issueDao.save(i);

		searchService.indexIssue(i);
		searchService.indexNote(i.getStatusChanges().get(0).getNote());

		return i;
	}
	
	/**
	 * @see bugeater.service.IssueService#load(String)
	 */
	public Issue load(Long id)
	{
		return issueDao.load(id);
	}
	
	/**
	 * @see bugeater.service.IssueService#save(bugeater.domain.User)
	 */
	public void save(Issue i)
	{
		issueDao.save(i);
		searchService.indexIssue(i);
	}
}
