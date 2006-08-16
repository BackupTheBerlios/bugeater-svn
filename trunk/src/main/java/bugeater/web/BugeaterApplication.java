package bugeater.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import bugeater.web.page.AddIssuePage;
import bugeater.web.page.Home;
import bugeater.web.page.SearchPage;
import bugeater.web.page.StaticContentPage;
import bugeater.web.page.ViewIssuePage;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import wicket.ISessionFactory;
import wicket.Page;
import wicket.PageParameters;
import wicket.RequestCycle;

import wicket.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.WebRequestCycle;
import wicket.settings.IRequestCycleSettings;
import wicket.spring.injection.SpringComponentInjector;

import wicket.util.time.Duration;

/**
 * The application class used to initialize wicket for the Bugeater web app.
 * 
 * @author pchapman
 */
public class BugeaterApplication extends WebApplication
{
	/**
	 * Creates a new instance.  This should only be called from wicket.  There
	 * is no reason for this app to create a new instance.
	 */
	public BugeaterApplication()
	{
		super();
	}
	
	private ApplicationContext applicationContext;
    
    public Object getSpringBean(String name)
    {
    	return applicationContext.getBean(name);
    }

	/**
	 * @see wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage()
	{
		return Home.class;
	}
    
	/**
	 * @see wicket.protocol.http.WebApplication#getSessionFactory()
	 */
	public ISessionFactory getSessionFactory()
	{
        return new BugeaterSessionFactory();
	}
	
	/**
	 * Builds a fully qualified path to the indicated page with the given
	 * parameters.
	 * @param pageClass The class of the Wicket page to link to.
	 * @param pageParameters The parameters to send to the page (send an empty
	 *                       PageParameters instance if there are no parameters
	 *                       to send.
	 * @return The url in the form of a String.
	 */
	public String buildFullyQualifiedPath(
			Class<? extends Page>pageClass, PageParameters pageParameters
		)
	{
		WebRequestCycle cycle = (WebRequestCycle)RequestCycle.get();
		return
			getServerPath() +
			cycle.urlFor(null, pageClass, pageParameters);
	}
	
	private String getServerPath()
	{
		StringBuilder sb = new StringBuilder();
		WebRequestCycle cycle = (WebRequestCycle)RequestCycle.get();
		HttpServletRequest req = ((WebRequest)cycle.getRequest()).getHttpServletRequest();
		String scheme = req.getScheme();
		int port = req.getServerPort();
		sb.append(scheme); // http, https
		sb.append("://");
		sb.append(req.getServerName());
		if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443))
		{
			sb.append(':');
			sb.append(req.getServerPort());
		}
		return sb.toString();
	}
	
	public String getServerContextPath()
	{
		WebRequestCycle cycle = (WebRequestCycle)RequestCycle.get();
		HttpServletRequest req = ((WebRequest)cycle.getRequest()).getHttpServletRequest();
		return
			getServerPath() + req.getContextPath();
	}

	/**
	 * @see wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init()
	{
		// Set up Spring
        ServletContext servletContext = getServletContext();
        applicationContext =
        	WebApplicationContextUtils.getRequiredWebApplicationContext(
        			servletContext
        		);
		addComponentInstantiationListener(new SpringComponentInjector(this));
		// Set resource settings
        getResourceSettings().setThrowExceptionOnMissingResource(false);
        getRequestCycleSettings().setRenderStrategy(
        		IRequestCycleSettings.RenderStrategy.REDIRECT_TO_RENDER
        	);
        getResourceSettings().addResourceFolder("web/WEB-INF/classes");
        getResourceSettings().setResourcePollFrequency(Duration.ONE_MINUTE);
        getSharedResources().add("attachment", new AttachmentResource());
        
        // Set security settings
        getSecuritySettings().setAuthorizationStrategy(
				new AnnotationsRoleAuthorizationStrategy(
						new BugeaterRoleCheckingStrategy()
					)
        	);
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(
        		new BugeaterUnauthorizedComponentInstantiationListener()
        	);
        
        
        // Mount pages
        mountBookmarkablePage("/addissue", AddIssuePage.class);
        mountBookmarkablePage("/myissues", Home.class);
        mountBookmarkablePage("/searchissues", SearchPage.class);
        mountBookmarkablePage("/staticcontent", StaticContentPage.class);
        mountBookmarkablePage("/viewissue", ViewIssuePage.class);
        
        // Do super's init.
		super.init();
	}
}
