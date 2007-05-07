package bugeater.domain;

/**
 * The status of an issue.
 * 
 * @author pchapman
 */
public enum IssueStatus
{
	// *** WARNING, The order of these enumerations are important as that is
	// *** how class members with this enumeration as a type are stored in the
	// *** database.  If new values are added, make *sure* you add them to the
	// *** end.
	Open("Open"),
	Active("Active"),
	Testing("Testing"),
	Closed_Fixed("Closed - Fixed"),
	Closed_NoFix("Closed - No Fix"),
	Closed_Duplicate("Closed - Duplicate"),
	Info_Requested("Information Requested"),
	Pending_Release("Pending Release");
	
	private IssueStatus(String name)
	{
		this.name = name;
	}
	
	private String name;
	/** The name of the issue status. **/
	public String getName()
	{
		return name;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * Takes an int value and returns the IssueStatus at the indicated ordinal. 
	 * @param value The ordinal of the IssueStatus.
	 * @return The IssueStatus at the given ordinal.
	 */
	public static final IssueStatus fromOrdinal(int value)
	{
		IssueStatus retValue = null;
		for (IssueStatus status : IssueStatus.values()) {
			if (status.ordinal() == value) {
				retValue = status;
				break;
			}
		}
		return retValue;
	}
	
	/**
	 * Statuses of those issues that have not been closed.
	 */
	public static final IssueStatus[] PENDING_STATUSES = {
		Open, Active, Testing, Info_Requested, Pending_Release
	};
}
