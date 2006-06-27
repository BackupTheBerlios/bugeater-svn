package bugeater.service;

/**
 * Roles that a user may fill in this application.
 * 
 * @author pchapman
 */
public enum SecurityRole
{
	Administrator,
	Developer,
	Tester,
	Manager,
	User;
	
	/**
	 * These constants are to be used in the Bugeater app and should not be used
	 * externally.
	 */
	public static final String ADMINISTRATOR = "Administrator";
	public static final String DEVELOPER = "Developer";
	public static final String MANAGER = "Manager";
	public static final String TESTER = "Tester";
	public static final String USER = "User";
	
	/**
	 * Takes a name and returns the matching SecurityRole, or null if there is
	 * none that match.
	 */
	public static final SecurityRole fromName(String name)
	{
		for (SecurityRole role : SecurityRole.values()) {
			if (role.toString().equals(name)) {
				return role;
			}
		}
		return null;
	}
}
