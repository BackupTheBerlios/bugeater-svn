package bugeater.web.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.Session;
import wicket.authorization.Action;
import wicket.authorization.strategies.role.annotations.AuthorizeAction;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.link.PageLink;
import wicket.model.IModel;
import wicket.model.Model;

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
	
	private final void init()
	{
		new PageLink(this, "homeLink", Home.class);
		new PageLink(this, "searchLink", SearchPage.class);
		new PageLink(this, "addLink", AddIssuePage.class);
		new Link(this, "logoutLink")
		{
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			public void onClick()
			{
				Session.get().invalidate();
				setResponsePage(Home.class);
			}
		};
		new IssueByIDForm(this, "issueByIDForm");
		new AdministrationLink(this, "adminLink");
	}
	
	private class IssueByIDForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		public IssueByIDForm(MarkupContainer parent, String id)
		{
			super(parent, id);
			idModel = new Model<String>();
			new TextField<String>(this, "issueByIDField", idModel);
		}
		
		private IModel<String> idModel;

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit()
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
