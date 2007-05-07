package bugeater.dao;

import java.io.Serializable;

/**
 * An exception class used to indicate that a persisted entity could not be
 * found in the persistance store (Database).
 * 
 * @author pchapman
 */
public class EntityNotFoundException
	extends javax.persistence.EntityNotFoundException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance reporting that the entity with the given class
	 * and unique ID could not be found.
	 * @param clazz The class of the entity not found.
	 * @param id The unique ID of the entity.
	 */
	public EntityNotFoundException(Class clazz, Serializable id)
	{
		super(
				"An object of type " + clazz + " with ID " +
				(id == null ? "(null)" : id.toString()) +
				" does not exist."
			);
	}
}
