package bugeater.bean;

import java.io.Serializable;

/**
 * A simple bean interface which provides user info.
 * 
 * @author pchapman
 */
public interface IUserBean extends Serializable
{
	/**
	 * Implementations should be sure to implement the equals method.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o);
	
	/**
	 * The email address of the user.
	 */
	public String getEmail();

	/**
	 * the first name of the user.
	 */
	public String getFirstname();

	/**
	 * the full name of the user.
	 */
	public String getFullname();
	
	/**
	 * The unique ID of the user.
	 */
	public String getId();

	/**
	 * The last name of the user.
	 */
	public String getLastname();

	/**
	 * The login of the user.
	 */
	public String getLogin();
	
	/**
	 * Implementations should be sure to implement the hashCode method.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode();
}
