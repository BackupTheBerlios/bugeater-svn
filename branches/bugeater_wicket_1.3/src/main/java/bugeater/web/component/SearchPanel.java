package bugeater.web.component;

import java.util.Arrays;
import java.util.List;

import bugeater.bean.IUserBean;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.ReleaseVersion;
import bugeater.service.IssueService;
import bugeater.service.SortOrder;
import bugeater.web.BugeaterApplication;
import bugeater.web.model.AbstractDetachableEntityListModel;
import bugeater.web.model.AssignableUsersModel;
import bugeater.web.model.ReleaseVersionModel;
import bugeater.web.model.ReleaseVersionsListModel;
import bugeater.web.model.UserIssuesListModel;
import bugeater.web.page.IssuesListPage;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * A panel which allows the user to provide search criteria for finding issues.
 * 
 * @author pchapman
 */
public class SearchPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param parent
	 * @param id
	 */
	public SearchPanel(String id)
	{
		super(id);
		add(new SearchForm("searchForm"));
	}
}

/**
 * The form which takes criteria and runs the search.
 */
class SearchForm extends Form
{
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private IssueService issueService;
	public void setIssueService(IssueService service)
	{
		this.issueService = service;
	}

	public SearchForm(String id)
	{
		super(id);

		// Search by current status
		add(
			new DropDownChoice(
					"status", new Model(), Arrays.asList(IssueStatus.values())
				)
			{
				private static final long serialVersionUID = 1L;
				
				/**
				 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
				 */
				@Override
				protected boolean wantOnSelectionChangedNotifications()
				{
					return true;
				}
	
				/**
				 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
				 */
				@Override
				protected void onSelectionChanged(Object newSelection)
				{
					if (newSelection != null) {
						setResponsePage(
								new IssuesListPage(
										new CurrentStatusSearchModel(
												(IssueStatus)newSelection
											),
										"Issues which currently have the status " +
											newSelection.toString() +
											":"
									)
							);
					}
				}
			}
		);
		
		// All pending issues link
		add(
			new Link("pendingIssuesLink")
			{
				private static final long serialVersionUID = 1L;
				public void onClick()
				{
					setResponsePage(
							new IssuesListPage(
									new PendingIssuesSearchModel(),
									"Pending issues (Issues not closed):"
								)
						);
				}
			}
		);
		
		// Search by release version
		add(
			new DropDownChoice(
					"release",
					new ReleaseVersionModel((Long)null),
					new ReleaseVersionsListModel(SortOrder.Descending)
				)
			{
				private static final long serialVersionUID = 1L;
				
				/**
				 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
				 */
				@Override
				protected boolean wantOnSelectionChangedNotifications()
				{
					return true;
				}
	
				/**
				 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
				 */
				@Override
				protected void onSelectionChanged(Object newSelection)
				{
					if (newSelection != null) {
						setResponsePage(
								new IssuesListPage(
										new ReleaseVersionSearchModel(
												new ReleaseVersionModel(
														(ReleaseVersion)newSelection
												)
											),
										"Issues assigned to be released in " +
											newSelection.toString() +
											":"
									)
							);
					}
				}
			}.setChoiceRenderer(
					new IChoiceRenderer()
					{
						private static final long serialVersionUID = 1L;
						/**
						 * @see wicket.markup.html.form.IChoiceRenderer#getDisplayValue(T)
						 */
						public Object getDisplayValue(Object object)
						{
							ReleaseVersion r = (ReleaseVersion)object;
							return
								r.getProject() + " - " +
								r.getVersionNumber();
						}
	
						/**
						 * @see wicket.markup.html.form.IChoiceRenderer#getIdValue(T, int)
						 */
						public String getIdValue(Object object, int index)
						{
							return ((ReleaseVersion)object).getId().toString();
						}
					}
			)
		);
		
		
		// Search by project
		add(
			new DropDownChoice(
					"project",
					new Model(null),
					issueService.getProjectsList()
				)
			{
				private static final long serialVersionUID = 1L;
				
				/**
				 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
				 */
				@Override
				protected boolean wantOnSelectionChangedNotifications()
				{
					return true;
				}
	
				/**
				 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
				 */
				@Override
				protected void onSelectionChanged(Object newSelection)
				{
					if (newSelection != null) {
						setResponsePage(
								new IssuesListPage(
										new ProjectSearchModel(
												newSelection.toString()
											),
										"Issues for the project " +
											newSelection.toString() +
											":"
									)
							);
					}
				}
			}
		);
		
		// Search by assignee
		add(
			new DropDownChoice(
					"assignee",
					searchUserModel = new Model(),
					new AssignableUsersModel(), new UserBeanChoiceRenderer()
				)
			{
				private static final long serialVersionUID = 1L;
				
				/**
				 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
				 */
				@Override
				protected boolean wantOnSelectionChangedNotifications()
				{
					return true;
				}
	
				/**
				 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
				 */
				@Override
				protected void onSelectionChanged(Object newSelection)
				{
					if (newSelection != null) {
						setResponsePage(
								new IssuesListPage(
										new UserIssuesListModel(
												UserIssuesListModel.AssociationType.Assigned,
												((IUserBean)newSelection).getId()
											),
										"Issues assigned to " +
											newSelection.toString() +
											":"
									)
							);
					}
				}
			}
		);
			
		// Search by status change
		add(
			new DropDownChoice(
					"statusChanged",
					searchStatusChangeModel = new Model(),
					Arrays.asList(IssueStatus.values())
				)
			);
		add(
			new DropDownChoice(
					"changedBy",
					searchUserModel = new Model(),
					new AssignableUsersModel()
				)
			);
		add(
			new Button("statusChangeSearch")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					setResponsePage(
							new IssuesListPage(
									new StatusChangeSearchModel(
											((IssueStatus)searchStatusChangeModel.getObject()),
											((IUserBean)searchUserModel.getObject())
										),
									"Issues changed to status " +
										searchStatusChangeModel.getObject().toString() +
										" by " +
										searchUserModel.getObject().toString() +
										":"
								)
						);
				}
			}
		);
	}
	
	private IModel searchStatusChangeModel;
	private IModel searchUserModel;
}

/**
 * A model which will provide a list of pending issues
 * 
 * @author pchapman
 */
class PendingIssuesSearchModel extends AbstractDetachableEntityListModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	PendingIssuesSearchModel()
	{
		super();
	}

	@Override
	protected List<Issue> load()
	{
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		return service.getPendingIssues();
	}
}

/**
 * A model which will provide a list of issues that have the given current
 * status.
 * 
 * @author pchapman
 */
class CurrentStatusSearchModel extends AbstractDetachableEntityListModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	CurrentStatusSearchModel(IssueStatus issueStatus)
	{
		super();
		this.issueStatus = issueStatus;
	}
	
	private IssueStatus issueStatus;

	@Override
	protected List<Issue> load()
	{
		IssueService service = (IssueService)
			((BugeaterApplication)Application.get()).getSpringBean("issueService");
		return service.getIssuesByCurrentStatus(issueStatus);
	}
}

/**
 * A model which will provide a list of issues based on status and who changed
 * the issue to that status.
 * 
 * @author pchapman
 */
class StatusChangeSearchModel extends AbstractDetachableEntityListModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	StatusChangeSearchModel(IssueStatus issueStatus, IUserBean userBean)
	{
		super();
		this.issueStatus = issueStatus;
		this.userBean = userBean;
	}
	
	private IssueStatus issueStatus;
	private IUserBean userBean;

	@Override
	protected List<Issue> load()
	{
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		return service.getIssuesByStatusChange(issueStatus, userBean);
	}
}

/**
 * A model which will provide a list of issues based on releaes version.
 * 
 * @author pchapman
 */
class ReleaseVersionSearchModel extends AbstractDetachableEntityListModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	ReleaseVersionSearchModel(IModel releaseVersion)
	{
		super();
		this.releaseVersion = releaseVersion;
	}
	
	private IModel releaseVersion;

	@Override
	protected List<Issue> load()
	{
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		return service.getIssuesByReleaseVersion((ReleaseVersion)releaseVersion.getObject());
	}
}

/**
 * A model which will provide a list of issues based on project.
 * 
 * @author pchapman
 */
class ProjectSearchModel extends AbstractDetachableEntityListModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	ProjectSearchModel(String project)
	{
		super();
		this.project = project;
	}
	
	private String project;

	@Override
	protected List<Issue> load()
	{
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		return service.getIssuesByProject(project);
	}
}
