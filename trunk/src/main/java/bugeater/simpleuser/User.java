package bugeater.simpleuser;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;

import bugeater.service.SecurityRole;

/**
 * A simple user object that is persisted in the bugeater database.
 * 
 * @author pchapman
 */
@Entity
@Table (name = "be_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User
{
	// CONSTRUCTORS
	
	/**
	 * 
	 */
	public User()
	{
		super();
	}
	
	public User(String login, String password) {
		super();
		this.login = login;
		this.password = password;
	}
	
	// MEMBERS
	
	@Column( name="email" )
	private String email;
	/**
	 * @see bugeater.bean.Ipublic User#getEmail()
	 */
	public String getEmail()
	{
		return email;
	}
	public User setEmail(String email)
	{
		this.email = email;
		return this;
	}

	@Column( name="firstName" )
	private String firstName;
	/**
	 * @see bugeater.bean.IUserBean#getFirstname()
	 */
	public String getFirstname()
	{
		return firstName;
	}
	public User setFirstName(String firstName)
	{
		this.firstName = firstName;
		return this;
	}

	@Id @GeneratedValue
	@Column(name="user_id")
	private Long id;
	public Long getId()
	{
		return id;
	}
	public User setId(Long id)
	{
		this.id = id;
		return this;
	}

	@Column( name="lastName" )
	private String lastName;
	/**
	 * @see bugeater.bean.IUserBean#getLastname()
	 */
	public String getLastname()
	{
		return lastName;
	}
	public User setLastName(String lastName)
	{
		this.lastName = lastName;
		return this;
	}

	@Column( name="login", unique=true )
	private String login;
	/**
	 * @see bugeater.bean.IUserBean#getLogin()
	 */
	public String getLogin()
	{
		return login;
	}
	public User setLogin(String login)
	{
		this.login = login;
		return this;
	}
	
	@Column( name="password")
	@AccessType("field")
	private String password;

	@CollectionOfElements
	@JoinTable(
            name="be_user_role",
            joinColumns = @JoinColumn(name="user_id")
    )
    @Column(name="role", nullable=false)
	private Set<SecurityRole>roles = new HashSet<SecurityRole>();
	/**
	 * @see bugeater.bean.IUserBean#getEmail()
	 * @return
	 */
	public Set<SecurityRole>getRoles()
	{
		return roles;
	}
	protected User setRoles(Set<SecurityRole> roles)
	{
		this.roles = roles;
		return this;
	}
	
	/**
	 * Checks the given value against the user's password.
	 * @return true if the passwords match, else false.
	 */
	public boolean checkPassword(String password)
	{
		return password.equals(this.password);
	}
}
