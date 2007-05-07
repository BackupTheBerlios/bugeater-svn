package bugeater.dao;

import java.util.List;

import bugeater.domain.LookupValue;

/**
 * An interface that defines data access methods for LookupValue objects.
 * 
 * @author pchapman
 */
public interface LookupValueDao
{
	/**
	 * Delete the value.  This does not delete issues that have been assigned
	 * these values.  It will only keep the value from showing up in lists for
	 * assignment in the future.
	 */
	public void delete(LookupValue value);

	/**
	 * A list of values of the given type, alphabetically ordered.
	 */
	public List<LookupValue>load(LookupValue.ValueType type);

	/**
	 * Gets a specific item by value, or null if it doesn't exist.
	 */
	public LookupValue load(LookupValue.ValueType type, String value);

	/**
	 * Loads a specific lookup value by ID.
	 */
	public LookupValue load(Long id);

	/**
	 * Saves changes to the value.  This does not change the value already
	 * assigned to issues.  It will only affect lists for assignment in the
	 * future.
	 */
	public void save(LookupValue value);
}
