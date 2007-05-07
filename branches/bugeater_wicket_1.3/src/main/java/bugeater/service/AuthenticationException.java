package bugeater.service;

/**
 * An exception thrown when a user login is invalid.
 * 
 * @author pchapman
 */
public class AuthenticationException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public enum Reason
	{
		DEACTIVATED_LOGIN("login.deactivated"),
		INVALID_LOGIN("login.invalid"),
		INVALID_PASSWORD("password.invalid");
		
		private String descKey;
		Reason(String descKey)
		{
			this.descKey = descKey;
		}
		public String getDescriptionKey()
		{
			return descKey;
		}
	}
	
	/**
	 * Creates a new instance with the given reason.
	 */
	public AuthenticationException(Reason reason)
	{
		super();
		this.reason = reason;
	}
	
	/**
	 * An authentication exception caused by another exception while
	 * authenticating.
	 * @param cause The nested cause.
	 */
	public AuthenticationException(Throwable cause)
	{
		super(cause);
	}

	private Reason reason;
	
	/**
	 * The reason the authentication failed.
	 */
	public Reason getReason()
	{
		return reason;
	}
}
