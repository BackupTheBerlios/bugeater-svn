package bugeater.service.impl;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Hit;
import bugeater.service.ISearchResult;

/**
 * Holds one result item from a text search.
 * 
 * @author pchapman
 */
abstract class AbstractSearchResult<T> implements ISearchResult<T>
{
	private static final Log logger = LogFactory.getLog(AbstractSearchResult.class);
	
	/**
	 * Creates a new isntance.
	 */
	AbstractSearchResult(Hit hit)
	{
		super();
		this.hit = hit;
	}

	private Hit hit;
	/**
	 * Returns the hit returned by lucene.
	 */
	protected Hit getHit()
	{
		return hit;
	}
	
	/**
	 * @see bugeater.service.ISearchResult#getScore()
	 */
	public float getScore()
	{
		try {
			return hit.getScore();
		} catch (IOException ioe) {
			logger.error(ioe);
			return 0;
		}
	}
	
	/**
	 * @see bugeater.service.ISearchResult#getMatchText()
	 */
	public abstract String getMatchText();
	
	/**
	 * @see bugeater.service.ISearchResult#getObject()
	 */
	public abstract T getObject();
	
	/**
	 * @see bugeater.service.ISearchResult#getObjectId()
	 */
	public Long getObjectId()
	{
		try {
			return Long.parseLong(hit.getDocument().get("id"));
		} catch (IOException ioe) {
			logger.error(ioe);
			return null;
		}
	}
}
