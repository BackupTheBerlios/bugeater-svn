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
import bugeater.web.model.AssignableUsersModel;
import bugeater.web.model.ReleaseVersionModel;
import bugeater.web.model.ReleaseVersionsListModel;
import bugeater.web.model.UserIssuesListModel;
import bugeater.web.page.IssuesListPage;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.spring.injection.SpringBean;

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
	public SearchPanel(MarkupContainer parent, String id)
	{
		super(parent, id);
		new SearchForm(this, "searchForm");
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

	public SearchForm(MarkupContainer parent, String id)
	{
		super(parent, id);

		// Search by current status
		new DropDownChoice<IssueStatus>(
				this, "status", new Model<IssueStatus>(),
				Arrays.asList(IssueStatus.values())
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
				if (newSelection instanceof IssueStatus) {
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
		};
		
		// All pending issues link
		new Link(this, "pendingIssuesLink")
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
		};
		
		// Search by release version
		new DropDownChoice<ReleaseVersion>(
				this, "release",
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
				if (newSelection instanceof ReleaseVersion) {
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
				new IChoiceRenderer<ReleaseVersion>()
				{
					private static final long serialVersionUID = 1L;
					/**
					 * @see wicket.markup.html.form.IChoiceRenderer#getDisplayValue(T)
					 */
					public Object getDisplayValue(ReleaseVersion object)
					{
						return
							object.getProject() + " - " +
							object.getVersionNumber();
					}

					/**
					 * @see wicket.markup.html.form.IChoiceRenderer#getIdValue(T, int)
					 */
					public String getIdValue(ReleaseVersion object, int index)
					{
						return object.getId().toString();
					}
				}
		);
		
		
		// Search by project
		new DropDownChoice<String>(
				this, "project",
				new Model<String>(null),
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
		};
		
		// Search by assignee
		new DropDownChoice<IUserBean>(
				this, "assignee",
				searchUserModel = new Model<IUserBean>(),
				new AssignableUsersModel()
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
				if (newSelection instanceof IUserBean) {
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
		};
			
		// Search by status change
		new DropDownChoice<IssueStatus>(
				this, "statusChanged",
				searchStatusChangeModel = new Model<IssueStatus>(),
				Arrays.<IssueStatus>asList(IssueStatus.values())
			);
		new DropDownChoice<IUserBean>(
				this, "changedBy",
				searchUserModel = new Model<IUserBean>(),
				new AssignableUsersModel()
			);
		new Button(this, "statusChangeSearch")
		{
			private static final long serialVersionUID = 1L;
			public void onSubmit()
			{
				setResponsePage(
						new IssuesListPage(
								new StatusChangeSearchModel(
										searchStatusChangeModel.getObject(),
										searchUserModel.getObject()
									),
								"Issues changed to status " +
									searchStatusChangeModel.getObject().toString() +
									" by " +
									searchUserModel.getObject().toString() +
									":"
							)
					);
			}
		};
	}
	
	private IModel<IssueStatus>searchStatusChangeModel;
	private IModel<IUserBean>searchUserModel;
}

/**
 * A model which will provide a list of pending issues
 * 
 * @author pchapman
 */
class PendingIssuesSearchModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	PendingIssuesSearchModel()
	{
		super();
	}
	
	private List<Issue>list;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			list = service.getPendingIssues();
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// not implemented
	}
}

/**
 * A model which will provide a list of issues that have the given current
 * status.
 * 
 * @author pchapman
 */
class CurrentStatusSearchModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	CurrentStatusSearchModel(IssueStatus issueStatus)
	{
		super();
		this.issueStatus = issueStatus;
	}
	
	private IssueStatus issueStatus;
	private List<Issue>list;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			IssueService service = (IssueService)
				((BugeaterApplication)Application.get()).getSpringBean("issueService");
			list = service.getIssuesByCurrentStatus(issueStatus);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// not implemented
	}
}

/**
 * A model which will provide a list of issues based on status and who changed
 * the issue to that status.
 * 
 * @author pchapman
 */
class StatusChangeSearchModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	StatusChangeSearchModel(IssueStatus issueStatus, IUserBean userBean)
	{
		super();
		this.issueStatus = issueStatus;
		this.userBean = userBean;
	}
	
	private IssueStatus issueStatus;
	private List<Issue>list;
	private IUserBean userBean;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			list = service.getIssuesByStatusChange(issueStatus, userBean);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// not implemented
	}
}

/**
 * A model which will provide a list of issues based on releaes version.
 * 
 * @author pchapman
 */
class ReleaseVersionSearchModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	ReleaseVersionSearchModel(IModel<ReleaseVersion> releaseVersion)
	{
		super();
		this.releaseVersion = releaseVersion;
	}
	
	private List<Issue>list;
	private IModel<ReleaseVersion>releaseVersion;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return releaseVersion;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			list = service.getIssuesByReleaseVersion(releaseVersion.getObject());
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// not implemented
	}
}

/**
 * A model which will provide a list of issues based on project.
 * 
 * @author pchapman
 */
class ProjectSearchModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	ProjectSearchModel(String project)
	{
		super();
		this.project = project;
	}
	
	private List<Issue>list;
	private String project;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			list = service.getIssuesByProject(project);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// not implemented
	}
}
