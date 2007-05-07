package bugeater.simpleuser;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import bugeater.bean.IUserBean;
import bugeater.hibernate.AbstractHibernateDao;
import bugeater.service.SecurityRole;
import bugeater.service.UserService;

/**
 * Provides UserServiceImpl and AuthenticationService implementations for the
 * simple user authentication implementation.
 *  
 * @author pchapman
 */
public class UserServiceImpl
	implements UserService
{
	/**
	 * Creates a new instance.
	 */
	public UserServiceImpl()
	{
		super();
	}

	/**
	 * @see bugeater.service.UserService#getUserByPrincipal(java.security.Principal)
	 */
	public IUserBean getUserByPrincipal(Principal principal)
	{
		return (IUserBean)((UserBean)principal);
	}

	/**
	 * @see bugeater.service.UserService#getUserById(java.lang.String)
	 */
	public IUserBean getUserById(String id)
	{
		if (id == null) {
			return null;
		} else {
			User u = userDao.load(Long.parseLong(id));
			if (u == null) {
				return null;
			} else {
				return new UserBean(u);
			}
		}
	}
	
	private UserDao userDao = new UserDao();
	UserDao getUserDao()
	{
		return userDao;
	}

	/**
	 * @see bugeater.service.UserService#getUsersByRole(bugeater.service.SecurityRole)
	 */
	public Set<IUserBean> getUsersByRole(SecurityRole role)
	{
		Set<IUserBean> set = new HashSet<IUserBean>();
		for (User u : userDao.getUsersByRole(role)) {
			set.add(new UserBean(u));
		}
		return set;
	}

	/* spring injected */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		userDao.setSessionFactory(sessionFactory);
	}
}

/**
 * A simple dao that provides method to look up User objects.
 * @author pchapman
 *
 */
class UserDao extends AbstractHibernateDao<User>
{
	UserDao()
	{
		super(User.class);
	}

	/**
	 * Gets all users that are associated with the given role.
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> getUsersByRole(SecurityRole role)
	{
		return (Collection<User>)getSession()
			.createSQLQuery(
					"select u.* " +
					"from be_user_role r " +
					" join be_user u on u.user_id = r.user_id " +
					"where r.role = :role ")
			.addEntity(User.class)
			.setParameter("role", role.ordinal())
			.list();
	}

	/**
	 * Looks up a user record by login.
	 */
	public User lookupByLogin(String login)
	{
		return (User)getSession().createQuery(
				"select u from User u where u.login = :login"
			).setString("login", login)
			.uniqueResult();
	}
}
