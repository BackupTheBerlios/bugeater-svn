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

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The base page for the Bugeater application.  This class can be used to
 * provide site-wide look and feel to the pages.
 * 
 * @author pchapman
 */
public abstract class BugeaterPage<T> extends WebPage
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
	public BugeaterPage(IModel arg0)
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
	public BugeaterPage(PageMap arg0, IModel arg1)
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
		add(new BookmarkablePageLink("bugeaterLink", Home.class).setEnabled(isUser));
		add(new BookmarkablePageLink("homeLink", Home.class).setVisible(isUser));
		add(new BookmarkablePageLink("searchLink", SearchPage.class).setVisible(isUser));
		add(new BookmarkablePageLink("addLink", AddIssuePage.class).setVisible(
				isUserInRole(SecurityRole.Administrator) ||
				isUserInRole(SecurityRole.Developer) ||
				isUserInRole(SecurityRole.Manager) ||
				isUserInRole(SecurityRole.Tester)
			)
		);
		new AdministrationLink(this, "adminLink");
		add(
			new Link("loginLink")
			{
				private static final long serialVersionUID = 1L;
				@SuppressWarnings("unchecked")
				public void onClick()
				{
					setResponsePage(LoginPage.class);
				}
			}.setVisible(((BugeaterSession)Session.get()).getPrincipal() == null)
		);
		add(
			new Link("logoutLink")
			{
				private static final long serialVersionUID = 1L;
				@SuppressWarnings("unchecked")
				public void onClick()
				{
					Session.get().invalidate();
					setResponsePage(Home.class);
				}
			}.setVisible(isUser)
		);
		new IssueByIDForm(this, "issueByIDForm").setVisible(isUser);
		PageParameters params = new PageParameters();
		params.add(BugeaterConstants.PARAM_NAME_CONTENT_URL, "/static/about.html");
		add(new BookmarkablePageLink("aboutLink", StaticContentPage.class, params));
		params = new PageParameters();
		params.add(BugeaterConstants.PARAM_NAME_CONTENT_URL, "/static/usage.html");
		add(new BookmarkablePageLink("usageLink", StaticContentPage.class, params));
	}
	
	private class IssueByIDForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		public IssueByIDForm(MarkupContainer parent, String id)
		{
			super(id);
			parent.add(this);
			
			// Issue by ID
			idModel = new Model();
			add(new TextField("issueByIDField", idModel));
			add(new Button("issueByID")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					try {
						long l = Long.parseLong(idModel.getObject().toString());
						IssueService svc = 
							(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
						Issue i = svc.load(l);
						if (i != null && i.getId() != null) {
							setResponsePage(new ViewIssuePage(i));
						}
					} catch (NumberFormatException nfe) {
					} catch (ObjectNotFoundException onfe) {}
				}
			});
			
			// Search by text
			add(new TextField(
					"searchText", searchTextModel = new Model()
				)
			);		
			add(new Button("textSearch")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					setResponsePage(
							new IssuesListPage(
									new TextSearchModel(
											searchTextModel.getObject().toString()
										),
									"Issues which match the text \"" +
									searchTextModel.getObject() +
									"\":"
								)
						);
				}
			});
		}
		
		private IModel idModel;
		private IModel searchTextModel;
	}
	
	@AuthorizeAction(action=Action.RENDER, roles={SecurityRole.ADMINISTRATOR})
	private class AdministrationLink extends Link
	{
		private static final long serialVersionUID = 1L;
		
		public AdministrationLink(MarkupContainer parent, String id)
		{
			super(id);
			parent.add(this);
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
