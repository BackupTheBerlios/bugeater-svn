package bugeater.domain;

/**
 * The priority of an issue.
 * 
 * @author pchapman
 */
public enum Priority
{
	Lowest("Lowest"),
	Low("Low"),
	Medium("Medium"),
	High("High"),
	Highest("Highest");
	
	private Priority(String name)
	{
		this.name = name;
	}
	
	private String name;
	/** Returns the name of the priority. **/
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
	public static final Priority fromOrdinal(int value)
	{
		Priority priority = null;
		for (Priority p : Priority.values()) {
			if (p.ordinal() == value) {
				priority = p;
				break;
			}
		}
		return priority;
	}
}
