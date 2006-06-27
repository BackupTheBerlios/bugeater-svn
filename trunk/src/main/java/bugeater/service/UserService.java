package bugeater.service;

import java.security.Principal;
import java.util.Set;

import bugeater.bean.IUserBean;

/**
 * A service implemented to look up and manipulate user data.
 * 
 * @author pchapman
 */
public interface UserService
{
	/**
	 * Provides a user bean for the user associated to the principal.
	 */
	IUserBean getUserByPrincipal(Principal principal);
	
	/**
	 * Provides a user bean for the user with the given unique ID, or null if
	 * the unique ID is not associated to a user.
	 */
	IUserBean getUserById(String id);
	
	/**
	 * Returns all the users that are associated with the given role.
	 */
	Set<IUserBean>getUsersByRole(SecurityRole role);
}
