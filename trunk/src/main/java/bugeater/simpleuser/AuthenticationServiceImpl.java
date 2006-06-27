package bugeater.simpleuser;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bugeater.service.AuthenticationException;
import bugeater.service.AuthenticationService;
import bugeater.service.SecurityRole;

/**
 * A simple implementation of the bugeater.service.AuthenticationService
 * interface that can be used for testing.
 * 
 * @author pchapman
 */
public class AuthenticationServiceImpl implements AuthenticationService
{
	private static final String AUTH_COOKIE = "JSESSIONID";

	/**
	 * A map of sessions that is kept in memory.  If the webapp is restarted,
	 * all sessions will be lost.
	 */
	private static final Map<String, UserBean> sessions =
		Collections.synchronizedMap(new HashMap<String, UserBean>());

	/**
	 * Creates a new instance.
	 */
	public AuthenticationServiceImpl()
	{
		super();
	}
	
	private UserServiceImpl userService;
	/**
	 * Service used to get user data.
	 */
	/* Spring injected */
	public void setUserService(UserServiceImpl service)
	{
		this.userService = service;
	}

	/**
	 * @see bugeater.service.AuthenticationService#login(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.ServletContext, java.lang.String, java.lang.String)
	 */
	public Principal login(
			HttpServletRequest request, HttpServletResponse response,
			ServletContext application, String user, String password
		) throws AuthenticationException, ServletException
	{
		User u = userService.getUserDao().lookupByLogin(user);
		if (u != null && u.checkPassword(password)) {
			UserBean bean = new UserBean(u);
			Cookie cookie = lookupSessionCookie(request);
			if (cookie == null) {
				cookie = new Cookie(AUTH_COOKIE, bean.getId());
				response.addCookie(cookie);
			} else {
				cookie.setValue(bean.getId());
			}
			sessions.put(bean.getId(), bean);
			return new UserBean(u);
		}
		return null;
	}

	/**
	 * @see bugeater.service.AuthenticationService#getUserPrincipal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.ServletContext)
	 */
	public Principal getUserPrincipal(
			HttpServletRequest request, HttpServletResponse response,
			ServletContext application
		) throws ServletException
	{
		Cookie c = lookupSessionCookie(request);
		if (c != null) {
			return sessions.get(c.getValue());
		}
		return null;
	}

	/**
	 * @see bugeater.service.AuthenticationService#isUserInRole(java.security.Principal, bugeater.service.SecurityRole)
	 */
	public boolean isUserInRole(Principal user, SecurityRole role)
		throws ServletException
	{
		return userService.getUserDao().load(
				Long.parseLong(((UserBean)user).getId())
			).getRoles().contains(role);
	}

	/**
	 * @see bugeater.service.AuthenticationService#logout(javax.servlet.ServletContext, javax.servlet.http.HttpSession, java.lang.String, java.security.Principal)
	 */
	public void logout(
			ServletContext application, HttpSession session,
			String sessionId, Principal user
		) throws ServletException
	{
		sessions.remove(((UserBean)user).getId());
	}
	
	/**
	 * Finds the cookie in the HttpServletRequest, if it exists.
	 */
	private Cookie lookupSessionCookie(HttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie c: cookies) {
				if (c.getName().equals(AUTH_COOKIE)) {
					return c;
				}
			}
		}
		return null;
	}
}
