package bugeater.simpleuser;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
public class UserServiceImpl implements UserService, InitializingBean
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
	@Required
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		userDao.setSessionFactory(sessionFactory);
	}
	
	private PlatformTransactionManager transactionManager;
	private PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}
	@Required
	public void setTransactionManager(PlatformTransactionManager manager)
	{
		this.transactionManager = manager;
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		DefaultTransactionDefinition txdef = new DefaultTransactionDefinition();
		TransactionStatus txn = null;
		try {

			txn = getTransactionManager().getTransaction(txdef);
			int count = userDao.getUsersCount(null);
			transactionManager.commit(txn);

			if (count == 0) {

				txn = getTransactionManager().getTransaction(txdef);
				// Create default admin user
				User user = new User("admin", "bugeateradmin");
				user.setEmail("admin@bugeater.pcsw.us");
				user.setFirstName("Bugeater");
				user.setLastName("Admin");
				user.getRoles().add(SecurityRole.Administrator);
				user.getRoles().add(SecurityRole.Developer);
				user.getRoles().add(SecurityRole.Manager);
				user.getRoles().add(SecurityRole.Tester);
				user.getRoles().add(SecurityRole.User);
				userDao.save(user);
				transactionManager.commit(txn);

			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error creating default admin user", e);
			if (txn != null) {
				transactionManager.rollback(txn);
			}
		}
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

	/**
	 * Gets a count of users that are associated with the given role, or all users if role is null.
	 */
	public int getUsersCount(SecurityRole role)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(u.*) as cnt ");
		sql.append("from be_user u ");
		if (role != null) {
			sql.append(" join be_user_role r on u.user_id = r.user_id ");
			sql.append("where r.role = :role ");
		}
		
		SQLQuery query = getSession().createSQLQuery(sql.toString());
		query.addScalar("cnt", new IntegerType());
		if (role != null) {
			query.setParameter("role", role.ordinal());
		}
		return (Integer)query.uniqueResult();
	}
}
