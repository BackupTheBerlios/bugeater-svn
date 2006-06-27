package bugeater.service;

/**
 * An enumeration used to provide a sorting direction for those listing methods
 * that require one.
 * 
 * @author pchapman
 */
public enum SortOrder
{
	Ascending("Asc"),
	Descending("Desc");
	
	private SortOrder(String sqlToken)
	{
		this.sqlToken = sqlToken;
	}
	
	private String sqlToken;
	/**
	 * The token used in a SQL query to invoke the sort order.
	 * @return
	 */
	public String getSqlToken()
	{
		return sqlToken;
	}
}
