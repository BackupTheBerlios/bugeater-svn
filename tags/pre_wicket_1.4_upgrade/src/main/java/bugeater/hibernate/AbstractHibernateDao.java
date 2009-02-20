package bugeater.hibernate;

import bugeater.dao.EntityNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * A base class for dao implementations that deal with hibernate persisted
 * objects.
 * 
 * @author pchapman
 * 
 * @param <T> The class of the persisted object.
 */
public abstract class AbstractHibernateDao<T> extends Object
{
	// MEMBERS

	private Class entityClass;
    private SessionFactory sessionFactory;
    protected static final Log logger =
    	LogFactory.getLog(AbstractHibernateDao.class);

    /**
     * The session factory that provides hibernate sessions.
     */
    public SessionFactory getSessionFactory()
    {
    	return this.sessionFactory;
    }

    /**
     * Sets the session factory that provides hibernate sessions.
     * 
     * @param sessionFactory The factory.
     */
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieves a hibernate session from the factory.
     */
    public Session getSession()
    {
        return sessionFactory.getCurrentSession();
    }
    
	// CONSTRUCTORS

    /**
     * Creates a new instance that can delete, load, and save persisted
     * objects of the given type.
     * 
     * @param dataClass The class type of the persisted objects.  This must
     *                  match the class type of T.
     */
	protected AbstractHibernateDao(Class <T>dataClass)
	{
		super();
		this.entityClass = dataClass;
	}

    // METHODS
	
	/**
	 * Begins a hibernate transaction.
	 */
	public void begin()
	{
		getSession().beginTransaction();
	}

	/**
	 * Commits the hibernate transaction.
	 */
	public void commit()
	{
		getSession().getTransaction().commit();
	}

	/**
	 * Deletes the persisted object.  This method is protected.  Subclasses can
	 * override this method and open access to it.
	 * 
	 * @param persistedObject
	 */
    protected void delete(T persistedObject)
    {
        getSession().delete( persistedObject );
    }
    
	/**
	 * Deletes the persisted object with the given id.  This method is
	 * protected.  Subclasses can override this method and open access to it.
	 * 
	 * @param persistedObject
	 */
    protected void delete(Long id)
    {
    	delete(loadChecked(id));
    }

    /**
     * Loads the persisted object with the given id.
     * @param id The unique ID of the persisted object.
     */
    @SuppressWarnings("unchecked")
    public T load(Long id)
    {
        return (T)getSession().load(entityClass, id);
    }

    /**
     * Loads the persisted object with the given id.  If the object isn't
     * found, an exception is thrown.
     * @param id The unique ID.
     * @throws EntityNotFoundException
     */
    @SuppressWarnings("unchecked")
    public T loadChecked(Long id)
    	throws EntityNotFoundException
    {
        T persistedObject = load(id);

        if(persistedObject == null) {
        	throw new EntityNotFoundException(entityClass, id);
        }

        return persistedObject;
    }

    /**
     * Saves the persisted object.
     */
    public void save(T persistedObject)
    {
   		getSession().saveOrUpdate( persistedObject );
    }
}
