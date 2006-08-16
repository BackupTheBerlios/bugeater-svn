package bugeater.web;

import java.security.Principal;

import javax.persistence.Transient;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import bugeater.service.AuthenticationService;
import bugeater.service.SecurityRole;

import wicket.Application;
import wicket.Session;
import wicket.authorization.strategies.role.IRoleCheckingStrategy;
import wicket.authorization.strategies.role.Roles;

/**
 * A class that provides the wicket auth api an indication of whether the
 * user has particular roles.
 * 
 * @author pchapman
 */
public class BugeaterRoleCheckingStrategy implements IRoleCheckingStrategy
{
	private static final Log logger =
		LogFactory.getLog(BugeaterRoleCheckingStrategy.class);
	
	/**
	 * Creates a new instance.
	 */
	public BugeaterRoleCheckingStrategy()
	{
		super();
	}
	
	@Transient
	private AuthenticationService authService;
	private AuthenticationService getAuthenticationService()
	{
		if (authService == null) {
			// Retrieve it from the spring context
			authService = (AuthenticationService)((BugeaterApplication)Application.get()).getSpringBean("authenticationService");
		}
		return authService;
	}

	/**
	 * @see wicket.authorization.strategies.role.IRoleCheckingStrategy#hasAnyRole(wicket.authorization.strategies.role.Roles)
	 */
	public boolean hasAnyRole(Roles roles)
	{
		Principal p = ((BugeaterSession)Session.get()).getPrincipal();
		if (p == null) {
			return false;
		}
		
		SecurityRole role;
		for (String roleName : roles) {
			role = SecurityRole.valueOf(roleName);
			try {
				if (
						role != null &&
						getAuthenticationService().isUserInRole(p, role)
					)
				{
					return true;
				}
			} catch (ServletException se) {
				logger.error(se);
			}
		}
		
		return false;
	}
}
