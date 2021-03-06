package bugeater.web.page;

import java.security.Principal;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.ObjectNotFoundException;

import bugeater.domain.Issue;
import bugeater.service.AuthenticationService;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterSession;
import bugeater.web.model.TextSearchModel;

/**
 * The base page for the Bugeater application.  This class can be used to
 * provide site-wide look and feel to the pages.
 * 
 * @author pchapman
 */
public abstract class BugeaterPage extends WebPage
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
	public BugeaterPage(IModel<?> arg0)
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
	public BugeaterPage(PageMap arg0, IModel<?> arg1)
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
		add(new BookmarkablePageLink<Void>("bugeaterLink", Home.class).setEnabled(isUser));
		add(new BookmarkablePageLink<Void>("homeLink", Home.class).setVisible(isUser));
		add(new BookmarkablePageLink<Void>("searchLink", SearchPage.class).setVisible(isUser));
		add(new BookmarkablePageLink<Void>("addLink", AddIssuePage.class).setVisible(
				isUserInRole(SecurityRole.Administrator) ||
				isUserInRole(SecurityRole.Developer) ||
				isUserInRole(SecurityRole.Manager) ||
				isUserInRole(SecurityRole.Tester)
			));
		add(new BookmarkablePageLink<Void>("adminLink", AdministrationPage.class).setVisible(
				isUserInRole(SecurityRole.Administrator)
			));
		add(new Link<Void>("loginLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				setResponsePage(LoginPage.class);
			}
		}.setVisible(((BugeaterSession)Session.get()).getPrincipal() == null));
		add(new Link<Void>("logoutLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				Session.get().invalidate();
				setResponsePage(Home.class);
			}
		}.setVisible(isUser));
		add(new IssueByIDForm("issueByIDForm").setVisible(isUser));
		add(new BookmarkablePageLink<Void>("aboutLink", AboutPage.class));
		add(new BookmarkablePageLink<Void>("usageLink", UsagePage.class));
		add(new Label("versionLabel", version));
	}
	
	private static final String version;
	static {
    	ResourceBundle bundle = ResourceBundle.getBundle("bugeaterversion");
    	version = bundle.getString("application.version");
	}
	
	private class IssueByIDForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;
		
		public IssueByIDForm(String id)
		{
			super(id);
			
			// Issue by ID
			idModel = new Model<String>();
			add(new TextField<String>("issueByIDField", idModel));
			add(new Button("issueByID")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					try {
						long l = Long.parseLong(idModel.getObject());
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
			add(new TextField<String>(
					"searchText", searchTextModel = new Model<String>()
				));		
			add(new Button("textSearch")
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
			});
		}
		
		private IModel<String> idModel;
		private IModel<String> searchTextModel;
	}
}
