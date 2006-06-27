package bugeater.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * The implementation of this service is responsible for the actual
 * authentication and returning roles for a login.
 * 
 * @author pchapman
 */
public interface AuthenticationService
{
	/**
	 * Logs a user in with a user name and a password. This method is called
	 * if getUserPrincipal returns null and the user must be prompted for
	 * login credentials.
	 * 
	 * <p>
	 * The implementation may only use the response to set cookies and
	 * headers. It may not write output or set the response status. If the
	 * application needs to send a custom error reponse, it may throw an
	 * AuthenticationException.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response, in case any cookie need sending.
	 * @param application
	 *            servlet application
	 * @param user
	 *            the user name.
	 * @param password
	 *            the users input password.
	 * 
	 * @return the logged in principal on success, null on failure.
	 */
	public Principal login(
			HttpServletRequest request, HttpServletResponse response,
			ServletContext application, String user, String password
		) throws AuthenticationException, ServletException;
  
	/**
	 * Gets the authenticated user for the current request.  If the user
	 * has not logged in, just returns null.  If null is returned, the user
	 * will be prompted for login credentials and the
	 * login(HttpServletRequest, HttpServletResponse, ServletContext,
	 * String, String) method will be called to authenticate the credentials.
	 *
	 * <p>The implementation may only use the response to set cookies and
	 * headers.  It may not write output.
	 *
	 * @param request the request trying to authenticate.
	 * @param response the response for setting headers and cookies.
	 * @param application the servlet context
	 *
	 * @return the authenticated user or null if none has logged in
	 */
	public Principal getUserPrincipal(
			HttpServletRequest request, HttpServletResponse response,
			ServletContext application
		) throws ServletException;
  
	/**
	 * Returns true if the user fills the named role.
	 *
	 * @param user the user's Principal.
	 * @param role the role.
	 */
	public boolean isUserInRole(
			Principal user, SecurityRole role
		) throws ServletException;
  
	/**
	 * Logs the user out from the given request.
	 *
	 * @param session for timeout, the session timing out. null if force logout
	 */
	public void logout(
			ServletContext application, HttpSession session,
			String sessionId, Principal user
		) throws ServletException;
}
