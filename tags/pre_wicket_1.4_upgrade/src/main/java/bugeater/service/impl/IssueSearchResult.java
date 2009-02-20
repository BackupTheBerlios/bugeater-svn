/**
 * 
 */
package bugeater.service.impl;

import org.apache.lucene.search.Hit;
import bugeater.dao.IssueDao;
import bugeater.domain.Issue;

/**
 * A search result that wrapps Issues that match the search criteria.
 * 
 * @author pchapman
 */
public class IssueSearchResult extends AbstractSearchResult<Issue>
{
	/**
	 * @param hit
	 */
	public IssueSearchResult(Hit hit)
	{
		super(hit);
	}
	
	private IssueDao issueDao;
	/**
	 * The dao that will be used to retrieve issue records.
	 */
	public IssueSearchResult setIssueDao(IssueDao dao)
	{
		this.issueDao = dao;
		return this;
	}

	/**
	 * @see bugeater.service.impl.AbstractSearchResult#getMatchText()
	 */
	@Override
	public String getMatchText()
	{
		return getObject().getSummary();
	}

	/**
	 * @see bugeater.service.impl.AbstractSearchResult#getObject()
	 */
	@Override
	public Issue getObject()
	{
		return issueDao.load(getObjectId());
	}
}
