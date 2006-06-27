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
		try {
			objectid = Long.parseLong(hit.getDocument().get("id"));
			score = hit.getScore();
		} catch (IOException ioe) {
			logger.error(ioe);
		}
	}
	
	/**
	 * @see bugeater.service.ISearchResult#getScore()
	 */
	private float score;
	public float getScore()
	{
		return score;
	}
	
	/**
	 * @see bugeater.service.ISearchResult#getMatchText()
	 */
	public abstract String getMatchText();
	
	/**
	 * @see bugeater.service.ISearchResult#getObject()
	 */
	public abstract T getObject();
	
	private Long objectid;
	/**
	 * @see bugeater.service.ISearchResult#getObjectId()
	 */
	public Long getObjectId()
	{
		return objectid;
	}
}
