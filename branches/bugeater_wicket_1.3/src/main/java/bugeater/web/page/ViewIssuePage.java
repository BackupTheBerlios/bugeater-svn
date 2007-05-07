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
import bugeater.service.AuthenticationService;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.service.SortOrder;
import bugeater.service.UserService;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;
import bugeater.web.component.ConfirmLink;
import bugeater.web.component.IssueAttachmentsPanel;
import bugeater.web.component.UserBeanChoiceRenderer;
import bugeater.web.component.WatchIssueLink;
import bugeater.web.component.util.NullableChoiceRenderer;
import bugeater.web.model.AssignableUsersModel;
import bugeater.web.model.RadeoxModel;
import bugeater.web.model.ReleaseVersionsListModel;
import bugeater.web.model.IssueModel;
import bugeater.web.model.IssueNotesListModel;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

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

	public ViewIssuePage(IModel arg0)
	{
		super();
		init(arg0);
	}

	public ViewIssuePage(PageMap arg0, IModel arg1)
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
	private void init(final IModel model)
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
				((Issue)model.getObject()).getId().toString()
			);
		Link link = new BookmarkablePageLink(
				"idlink", ViewIssuePage.class, params
			);
		add(link);
		link.add(new Label("id"));
		final DateFormat dateFormat = DateFormat.getDateTimeInstance();
		
		add(new Label(
				"openTime",
				new Model(
						dateFormat.format(
								((Issue)model.getObject()).getOpenTime().getTime()
							)
					)
			)
		);
		
		add(new Label("summary"));
		add(new Label("project"));
		add(new Label("category"));
		
		add(new DropDownChoice("priority", Arrays.asList(Priority.values()))
		{
			private static final long serialVersionUID = 1L;
			
			/**
			 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
			 */
			@Override
			protected void onSelectionChanged(Object newSelection)
			{
				issueService.save((Issue)model.getObject());
			}

			/**
			 * @see wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}
		}.setEnabled(canEditIssue));

		DropDownChoice choice = new DropDownChoice(
				"assignedTo", new AssignedUserModel(model),
				new AssignableUsersModel(), new UserBeanChoiceRenderer()
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
					issueService.save((Issue)model.getObject());
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
		add(choice);
		choice.setChoiceRenderer(new NullableChoiceRenderer());
		choice.setEnabled(canEditIssue);
		choice.setNullValid(true);
		
		final IModel newStatusModel = new Model(((Issue)model.getObject()).getCurrentStatus());
		add(
			new DropDownChoice("currentStatus", newStatusModel, Arrays.asList(IssueStatus.values()))
			{
				private static final long serialVersionUID = 1L;
				
				/**
				 * @see wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
				 */
				@Override
				protected void onSelectionChanged(Object newSelection)
				{
					if (newSelection != null) {
						// Get the note, then change the status
						setResponsePage(new EditNotePage(model, (IssueStatus)newSelection));
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
			}.setEnabled(canEditIssue)
		);

		choice = new DropDownChoice(
				"releaseVersion",
				new ReleaseVersionsListModel(
						new PropertyModel(model, "project"), true,
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
				issueService.save((Issue)model.getObject());
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
		add(choice);
		choice.setChoiceRenderer(new NullableChoiceRenderer());
		choice.setNullValid(true);
		choice.setEnabled(canEditIssue);
		
		add(new WatchIssueLink("watchLink", model));
		
		add(new DeleteIssueLink("deleteLink", model));
		
		add(new ListView("notesList", new IssueNotesListModel(model))
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem item)
				{
					Note note = (Note)item.getModelObject();
					item.add(
					new Label(
							"postedBy",
							new Model(
									userService.getUserById(
											note.getUserID()
										).getFullname()
								)
						));
					item.add(
					new Label(
							"postDate",
							new Model(
									dateFormat.format(
											note.getCreateTime().getTime()
										)
								)
						));
					Label label = null;
					for (IssueStatusChange isc : note.getIssue().getStatusChanges()) {
						if (isc.getNote() != null && isc.getNote().equals(note)) {
							label = new Label("statusChange", isc.getIssueStatus().toString());
							break;
						}
					}
					if (label == null) {
						label = new Label("statusChange", "");
					}
					item.add(label);
					item.add(
					new Label(
							"text", new RadeoxModel(note.getText())
						).setEscapeModelStrings(false));
					PageParameters params = new PageParameters();
					params.add(BugeaterConstants.PARAM_NAME_NOTE_ID, String.valueOf(note.getId()));
					Link l = new BookmarkablePageLink(
							"editnotelink", EditNotePage.class, params
						);
					item.add(l);
					l.add(new WebMarkupContainer("editnoteimage").add(new AttributeModifier("src", true, new UrlModel())));
					BugeaterSession session = (BugeaterSession)Session.get();
					boolean editable = false;
					try {
						editable =
							aService.isUserInRole(session.getPrincipal(), SecurityRole.Administrator) ||
							session.getUserBean().getId().equals(note.getUserID());
					} catch (ServletException se) {}
					l.setVisible(editable);
	
				}
			}.setRenderBodyOnly(true)
		);

		add(
		new Link("addNoteLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				setResponsePage(new EditNotePage(model));
			}
		});
		
		add(new IssueAttachmentsPanel("attachments", model));
	}
	
	class AssignedUserModel implements IModel
	{
		private static final long serialVersionUID = 1L;
		
		AssignedUserModel(IModel issueModel)
		{
			super();
			this.issueModel = issueModel;
		}
		
		private IModel issueModel;
		private UserService service;
		private transient IUserBean userBean;

		/**
		 * @see wicket.model.IModel#detach()
		 */
		public void detach()
		{
			userBean = null;
			issueModel.detach();
		}

		public Object getObject()
		{
			if (userBean == null) {
				service =
					(UserService)((BugeaterApplication)Application.get())
					.getSpringBean("userService");
				userBean = service.getUserById(((Issue)issueModel.getObject()).getAssignedUserID());
			}
			return userBean;
		}
		
		public void setObject(Object obj) {}
	}	
	
	private class UrlModel implements IModel
	{
		private static final long serialVersionUID = 1L;
		
		public void detach() {}

		/**
		 * @see wicket.model.Model#getObject()
		 */
		public Object getObject()
		{
			return
				((BugeaterApplication)Application.get()).getServerContextPath() +
				"/images/edit.png";
		}

		public void setObject(Object obj) {}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return getObject().toString();
		}
	}
}

@AuthorizeAction(action = "RENDER", roles = { SecurityRole.ADMINISTRATOR })
class DeleteIssueLink extends ConfirmLink
{
	private static final long serialVersionUID = 1L;
	
	DeleteIssueLink(String name, IModel model)
	{
		super(name, model);
	}
	
	/**
	 * @see bugeater.web.component.ConfirmLink#getConfirmationMessageModel()
	 */
	@Override
	public String getConfirmationMessage()
	{
		return "Once deleted, it cannot be recovered.  Are you sure you want to delete this issue?";
	}

	/**
	 * @see wicket.markup.html.link.Link#onClick()
	 */
	@Override
	public void onClick()
	{
		IssueService service = (IssueService)((BugeaterApplication)BugeaterApplication.get()).getSpringBean("issueService");
		service.delete((Issue)getModelObject());
		setResponsePage(Home.class);
	}
}