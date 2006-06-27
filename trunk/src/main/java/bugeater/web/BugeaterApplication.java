package bugeater.web;

import bugeater.web.page.Home;
import org.springframework.context.ApplicationContext;

import wicket.ISessionFactory;
import wicket.Page;

import wicket.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import wicket.settings.IRequestCycleSettings;
import wicket.spring.injection.AnnotSpringWebApplication;

import wicket.util.time.Duration;

/**
 * The application class used to initialize wicket for the Bugeater web app.
 * 
 * @author pchapman
 */
public class BugeaterApplication extends AnnotSpringWebApplication
{
	/**
	 * Creates a new instance.  This should only be called from wicket.  There
	 * is no reason for this app to create a new instance.
	 */
	public BugeaterApplication()
	{
		super();
	}
    
	/**
	 * @see wicket.spring.injection.annot.AnnotSpringWebApplication#internalGetApplicationContext()
	 * @return
	 */
    ApplicationContext getApplicationContext()
    {
    	return internalGetApplicationContext();
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
	 * @see wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init()
	{
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
        
        // Do super's init.
		super.init();
	}
}
