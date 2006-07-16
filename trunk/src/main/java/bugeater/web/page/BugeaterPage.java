package bugeater.web.page;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;

import bugeater.domain.Issue;
import bugeater.service.AuthenticationService;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;
import bugeater.web.model.TextSearchModel;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.Session;
import wicket.authorization.Action;
import wicket.authorization.strategies.role.annotations.AuthorizeAction;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.Link;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.spring.injection.SpringBean;

/**
 * The base page for the Bugeater application.  This class can be used to
 * provide site-wide look and feel to the pages.
 * 
 * @author pchapman
 */
public abstract class BugeaterPage<T> extends WebPage<T>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public BugeaterPage()
	{
		super();
		init();
	}

	/**
	 * @param arg0
	 */
	public BugeaterPage(IModel <T>arg0)
	{
		super(arg0);
		init();
	}

	/**
	 * @param arg0
	 */
	public BugeaterPage(PageMap arg0)
	{
		super(arg0);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BugeaterPage(PageMap arg0, IModel <T>arg1)
	{
		super(arg0, arg1);
		init();
	}

	/**
	 * @param arg0
	 */
	public BugeaterPage(PageParameters arg0)
	{
		super(arg0);
		init();
	}

	protected final Log logger = LogFactory.getLog(getClass());
	
	@SpringBean
	private AuthenticationService authService;
	public void setAuthenticationService(AuthenticationService authService)
	{
		this.authService = authService;
	}
	protected AuthenticationService getAuthenticationService()
	{
		return authService;
	}

	protected boolean isUserInRole(SecurityRole role)
	{
		try {
			Principal principal = ((BugeaterSession)Session.get()).getPrincipal();
			return getAuthenticationService().isUserInRole(principal, role);
		} catch (Throwable t) {
			logger.error(t);
			return false;
		}
	}
	
	private final void init()
	{
		boolean isUser = isUserInRole(SecurityRole.User);
		new BookmarkablePageLink(this, "bugeaterLink", Home.class).setEnabled(isUser);
		new BookmarkablePageLink(this, "homeLink", Home.class).setVisible(isUser);
		new BookmarkablePageLink(this, "searchLink", SearchPage.class).setVisible(isUser);
		new BookmarkablePageLink(this, "addLink", AddIssuePage.class).setVisible(
				isUserInRole(SecurityRole.Administrator) ||
				isUserInRole(SecurityRole.Developer) ||
				isUserInRole(SecurityRole.Manager) ||
				isUserInRole(SecurityRole.Tester)
			);
		new AdministrationLink(this, "adminLink");
		new Link(this, "loginLink")
		{
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			public void onClick()
			{
				setResponsePage(LoginPage.class);
			}
		}.setVisible(((BugeaterSession)Session.get()).getPrincipal() == null);
		new Link(this, "logoutLink")
		{
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			public void onClick()
			{
				Session.get().invalidate();
				setResponsePage(Home.class);
			}
		}.setVisible(isUser);
		new IssueByIDForm(this, "issueByIDForm").setVisible(isUser);
		PageParameters params = new PageParameters();
		params.add(BugeaterConstants.PARAM_NAME_CONTENT_URL, "/static/about.html");
		new BookmarkablePageLink(this, "aboutLink", StaticContentPage.class, params);
		params = new PageParameters();
		params.add(BugeaterConstants.PARAM_NAME_CONTENT_URL, "/static/usage.html");
		new BookmarkablePageLink(this, "usageLink", StaticContentPage.class, params);
	}
	
	private class IssueByIDForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		public IssueByIDForm(MarkupContainer parent, String id)
		{
			super(parent, id);
			
			// Issue by ID
			idModel = new Model<String>();
			new TextField<String>(this, "issueByIDField", idModel);
			new Button(this, "issueByID")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					try {
						long l = Long.parseLong(idModel.getObject());
						IssueService svc = 
							(IssueService)((BugeaterApplication)Application.get())
							.getSpringContextLocator().getSpringContext()
							.getBean("issueService");
						Issue i = svc.load(l);
						if (i != null && i.getId() != null) {
							setResponsePage(new ViewIssuePage(i));
						}
					} catch (NumberFormatException nfe) {
					} catch (ObjectNotFoundException onfe) {}
				}
			};
			
			// Search by text
			new TextField<String>(
					this, "searchText", searchTextModel = new Model<String>()
				);		
			new Button(this, "textSearch")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					setResponsePage(
							new IssuesListPage(
									new TextSearchModel(
											searchTextModel.getObject()
										),
									"Issues which match the text \"" +
									searchTextModel.getObject() +
									"\":"
								)
						);
				}
			};
		}
		
		private IModel<String> idModel;
		private IModel<String> searchTextModel;
	}
	
	@AuthorizeAction(action=Action.RENDER, roles={SecurityRole.ADMINISTRATOR})
	private class AdministrationLink extends Link
	{
		private static final long serialVersionUID = 1L;
		
		public AdministrationLink(MarkupContainer parent, String id)
		{
			super(parent, id);
		}

		/**
		 * @see wicket.markup.html.link.Link#onClick()
		 */
		@Override
		@SuppressWarnings("unchecked")
		public void onClick()
		{
			setResponsePage(AdministrationPage.class);
		}
		
	}
}
