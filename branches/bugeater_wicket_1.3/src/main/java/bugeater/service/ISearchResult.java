package bugeater.service;

/**
 * An interface that wrapps the results of a text search.
 * 
 * @author pchapman
 *
 * @param <T> The type of object returned by the search.
 */
public interface ISearchResult<T>
{
	/**
	 * A numeric indication of how closely the result matches the search
	 * criteria.
	 */
	public abstract float getScore();

	/**
	 * The text that matches the search criteria.
	 */
	public abstract String getMatchText();

	/**
	 * the matching object.
	 */
	public abstract T getObject();

	/**
	 * The unique ID of the object.  (The result is assumed to be a persisted
	 * object.
	 */
	public abstract Long getObjectId();
}