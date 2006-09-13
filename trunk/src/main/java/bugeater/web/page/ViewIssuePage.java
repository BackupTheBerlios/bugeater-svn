package bugeater.web.page;

import java.security.Principal;
import java.text.DateFormat;
import java.util.Arrays;

import javax.servlet.ServletException;

import bugeater.bean.IUserBean;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.IssueStatusChange;
import bugeater.domain.Note;
import bugeater.domain.Priority;
import bugeater.domain.ReleaseVersion;
import bugeater.service.AuthenticationService;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.service.SortOrder;
import bugeater.service.UserService;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;
import bugeater.web.component.IssueAttachmentsPanel;
import bugeater.web.component.WatchIssueLink;
import bugeater.web.component.util.NullableChoiceRenderer;
import bugeater.web.model.AssignableUsersModel;
import bugeater.web.model.RadeoxModel;
import bugeater.web.model.ReleaseVersionsListModel;
import bugeater.web.model.IssueModel;
import bugeater.web.model.IssueNotesListModel;

import wicket.Application;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.Session;
import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.AbstractDetachableModel;
import wicket.model.CompoundPropertyModel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.model.PropertyModel;
import wicket.spring.injection.SpringBean;
import wicket.util.string.StringValueConversionException;

/**
 * A page that will allow the user to view an issue.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.USER})
public class ViewIssuePage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	public ViewIssuePage(Long issueID)
	{
		super();
		init(new IssueModel(issueID));
	}

	public ViewIssuePage(Issue arg0)
	{
		super();
		init(new IssueModel(arg0));
	}

	public ViewIssuePage(IModel<Issue> arg0)
	{
		super();
		init(arg0);
	}

	public ViewIssuePage(PageMap arg0, IModel<Issue> arg1)
	{
		super(arg0);
		init(arg1);
	}
	
	@SuppressWarnings("unchecked")
	public ViewIssuePage(PageParameters params)
	{
		super(params);
		if (params.containsKey(BugeaterConstants.PARAM_NAME_ISSUE_ID)) {
			try {
				init(new IssueModel(params.getLong(BugeaterConstants.PARAM_NAME_ISSUE_ID)));
			} catch (StringValueConversionException svce) {
				logger.error(svce);
				setResponsePage(Home.class);
			}
		} else {
			setResponsePage(Home.class);
		}
	}
	
	@SpringBean
	private AuthenticationService aService;
	public void setAuthenticationService(AuthenticationService service)
	{
		this.aService = service;
	}
	
	@SpringBean
	private IssueService issueService;
	public void setIssueService(IssueService service)
	{
		this.issueService = service;
	}
	
	@SpringBean
	private UserService userService;
	public void setUserService(UserService service)
	{
		this.userService = service;
	}

	@SuppressWarnings("unchecked")
	private void init(final IModel<Issue>model)
	{
		Principal p = ((BugeaterSession)Session.get()).getPrincipal();
		boolean canEditIssue = false;
		
		try {
			canEditIssue =
				aService.isUserInRole(p, SecurityRole.Administrator) ||
				aService.isUserInRole(p, SecurityRole.Developer) ||
				aService.isUserInRole(p, SecurityRole.Tester);
		} catch (ServletException se) {
			logger.error(se);
		}
		
		setModel(new CompoundPropertyModel(model));

		// Make the issue ID a bookmarkable link so that people can easily
		// link to this issue
		PageParameters params = new PageParameters();
		params.add(
				BugeaterConstants.PARAM_NAME_ISSUE_ID,
				model.getObject().getId().toString()
			);
		Link link = new BookmarkablePageLink(
				this, "idlink", ViewIssuePage.class, params
			);
		new Label(link, "id");
		final DateFormat dateFormat = DateFormat.getDateTimeInstance();
		
		new Label(
				this, "openTime",
				new Model<String>(
						dateFormat.format(
								model.getObject().getOpenTime().getTime()
							)
					)
			);
		
		new Label(this, "summary");
		new Label(this, "project");
		new Label(this, "category");
		
		new DropDownChoice<Priority>(this, "priority", Arrays.<Priority>asList(Priority.values()))
		{
			private static final long serialVersionUID = 1L;
			
			/**
			 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
			 */
			@Override
			protected void onSelectionChanged(Object newSelection)
			{
				issueService.save(model.getObject());
			}

			/**
			 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}
		}.setEnabled(canEditIssue);
		
		DropDownChoice choice = new DropDownChoice<IUserBean>(
				this, "assignedTo", new AssignedUserModel(model),
				new AssignableUsersModel()
			)
		{
			private static final long serialVersionUID = 1L;
			
			/**
			 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
			 */
			@Override
			protected void onSelectionChanged(Object newSelection)
			{
				if (newSelection != null) {
					issueService.save(model.getObject());
				}
			}

			/**
			 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}
		};
		choice.setChoiceRenderer(new NullableChoiceRenderer());
		choice.setEnabled(canEditIssue);
		choice.setNullValid(true);
		
		final IModel<IssueStatus>newStatusModel = new Model<IssueStatus>(model.getObject().getCurrentStatus());
		new DropDownChoice<IssueStatus>(this, "currentStatus", newStatusModel, Arrays.<IssueStatus>asList(IssueStatus.values()))
		{
			private static final long serialVersionUID = 1L;
			
			/**
			 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
			 */
			@Override
			protected void onSelectionChanged(Object newSelection)
			{
				IssueStatus status = getModelObject();
				if (status != null) {
					// Get the note, then change the status
					setResponsePage(new AddNotePage(model, status));
				}
			}

			/**
			 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}
		}.setEnabled(canEditIssue);

		choice = new DropDownChoice<ReleaseVersion>(
				this, "releaseVersion",
				new ReleaseVersionsListModel(
						new PropertyModel<String>(model, "project"), true,
						SortOrder.Descending
					)
			)
		{
			private static final long serialVersionUID = 1L;
			
			/**
			 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
			 */
			@Override
			protected void onSelectionChanged(Object newSelection)
			{
				issueService.save(model.getObject());
			}

			/**
			 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}
		};
		choice.setChoiceRenderer(new NullableChoiceRenderer());
		choice.setNullValid(true);
		choice.setEnabled(canEditIssue);
		
		new WatchIssueLink(this, "watchLink", model);
		
		new ListView<Note>(this, "notesList", new IssueNotesListModel(model))
		{
			private static final long serialVersionUID = 1L;
			public void populateItem(ListItem <Note>item)
			{
				Note note = item.getModelObject();
				new Label(
						item, "postedBy",
						new Model<String>(
								userService.getUserById(
										note.getUserID()
									).getFullname()
							)
					);
				new Label(
						item, "postDate",
						new Model<String>(
								dateFormat.format(
										note.getCreateTime().getTime()
									)
							)
					);
				Label label = null;
				for (IssueStatusChange isc : note.getIssue().getStatusChanges()) {
					if (isc.getNote() != null && isc.getNote().equals(note)) {
						label = new Label(item, "statusChange", isc.getIssueStatus().toString());
						break;
					}
				}
				if (label == null) {
					label = new Label(item, "statusChange", "");
				}
				new Label(
						item, "text", new RadeoxModel(note.getText())
					).setEscapeModelStrings(false);
			}
		}.setRenderBodyOnly(true);
		
		new Link(this, "addNoteLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				setResponsePage(new AddNotePage(model));
			}
		};
		
		new IssueAttachmentsPanel(this, "attachments", model);
	}
	
	class AssignedUserModel extends AbstractDetachableModel<IUserBean>
	{
		private static final long serialVersionUID = 1L;
		
		AssignedUserModel(IModel<Issue>issueModel)
		{
			super();
			this.issueModel = issueModel;
		}
		
		private IModel<Issue>issueModel;
		private UserService service;

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
			if (service == null) {
				service =
					(UserService)((BugeaterApplication)Application.get())
					.getSpringBean("userService");
			}
		}

		/**
		 * @see wicket.model.AbstractDetachableModel#onDetach()
		 */
		@Override
		protected void onDetach()
		{
			service = null;
		}

		/**
		 * @see wicket.model.AbstractDetachableModel#onGetObject()
		 */
		@Override
		protected IUserBean onGetObject()
		{
			return service.getUserById(issueModel.getObject().getAssignedUserID());
		}

		/**
		 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
		 */
		@Override
		protected void onSetObject(IUserBean bean)
		{
			issueModel.getObject().setAssignedUserID(bean.getId());
		}
	}	
}
