package bugeater.simpleuser;

import java.security.Principal;

import bugeater.bean.IUserBean;

/**
 * An implementation of the IUserBean interface for the simple user auth
 * implementation.
 * 
 * @author pchapman
 */
public class UserBean implements IUserBean, Principal
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	// CONSTRUCTORS
	
	/**
	 * A serializable bean that provides user information.
	 */
	public UserBean()
	{
		super();
	}
	
	public UserBean(User user)
	{
		super();
		setEmail(user.getEmail()).setFirstName(user.getFirstname())
			.setId(user.getId().toString()).setLastName(user.getLastname())
			.setLogin(user.getLogin());
	}
	
	// MEMBERS
	
	private String email;
	/**
	 * @see bugeater.bean.IUserBean#getEmail()
	 */
	public String getEmail()
	{
		return email;
	}
	UserBean setEmail(String email)
	{
		this.email = email;
		return this;
	}

	private String firstName;
	/**
	 * @see bugeater.bean.IUserBean#getFirstname()
	 */
	public String getFirstname()
	{
		return firstName;
	}
	UserBean setFirstName(String firstName)
	{
		this.firstName = firstName;
		updateFullName();
		return this;
	}

	private String fullName;
	/**
	 * @see bugeater.bean.IUserBean#getFullname()
	 */
	public String getFullname()
	{
		return fullName;
	}

	private String id;
	/**
	 * @see bugeater.bean.IUserBean#getId()
	 */
	public String getId()
	{
		return id;
	}
	UserBean setId(String id)
	{
		this.id = id;
		return this;
	}

	private String lastName;
	/**
	 * @see bugeater.bean.IUserBean#getLastname()
	 */
	public String getLastname()
	{
		return lastName;
	}
	UserBean setLastName(String lastName)
	{
		this.lastName = lastName;
		updateFullName();
		return this;
	}

	private String login;
	/**
	 * @see bugeater.bean.IUserBean#getLogin()
	 */
	public String getLogin()
	{
		return login;
	}
	UserBean setLogin(String login)
	{
		this.login = login;
		return this;
	}
	
	/**
	 * @see java.security.Principal#getName()
	 */
	public String getName()
	{
		return getLogin();
	}

	// METHODS
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof UserBean && ((UserBean)obj).getId().equals(getId()));
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getId().hashCode();
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		return getFullname();
	}
	
	/**
	 * Updates the full anme member based on the first anme and last name.
	 */
	private void updateFullName()
	{
		StringBuilder sb = new StringBuilder();
		if (firstName != null) {
			sb.append(firstName);
		}
		if (lastName != null) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(lastName);
		}
		fullName = sb.toString();
	}
}
