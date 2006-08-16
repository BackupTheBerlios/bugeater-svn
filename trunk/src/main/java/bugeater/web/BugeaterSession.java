package bugeater.web;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import bugeater.bean.IUserBean;
import bugeater.service.AuthenticationService;
import bugeater.service.UserService;

import wicket.Application;

import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.WebSession;

/**
 * A session object used by the web application to track session state.
 * 
 * @author pchapman
 */
public class BugeaterSession extends WebSession
{
	private static final Log logger = LogFactory.getLog(BugeaterSession.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance.
	 * @param webapp The web application within which this session object is
	 *               used.
	 * @param userService The services used to load the user object when this
	 *                    session is deserialized.
	 */
	BugeaterSession(WebApplication webapp)
	{
		super(webapp);
	}
	
	private transient AuthenticationService authService;
	private AuthenticationService getAuthenticationService()
	{
		if (authService == null) {
			// Retrieve it from the spring context
			authService = (AuthenticationService)((BugeaterApplication)Application.get()).getSpringBean("authenticationService");
		}
		return authService;
	}
		
	private transient Principal principal;
	/**
	 * Returns the principal of the user logged in to the current session or
	 * null if no user has logged in.
	 */
	public Principal getPrincipal()
	{
		if (principal == null) {
			WebRequestCycle cycle = WebRequestCycle.get();
			HttpServletRequest request = cycle.getWebRequest().getHttpServletRequest();
			HttpServletResponse response = cycle.getWebResponse().getHttpServletResponse();
			ServletContext context = ((WebApplication)Application.get()).getServletContext();
			
			try {
				principal = getAuthenticationService().getUserPrincipal(
						request, response, context
					);
			} catch (ServletException se) {
				logger.error(se);
			}
		}
		return principal;
	}
	public void setPrincipal(Principal p)
	{
		this.principal = p;
	}
	
	private transient IUserBean userBean;
	public IUserBean getUserBean()
	{
		if (userBean == null) {
			Principal p = getPrincipal();
			if (p != null) {
				userBean = getUserService().getUserByPrincipal(p);
			}
		}
		return userBean;
	}
	
	private transient UserService userService;
	private UserService getUserService()
	{
		if (userService == null) {
			// Retrieve it from the spring context
			userService = (UserService)((BugeaterApplication)Application.get()).getSpringBean("userService");
		}
		return userService;
	}
}
