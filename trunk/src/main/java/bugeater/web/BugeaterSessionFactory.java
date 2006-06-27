package bugeater.web;

import wicket.ISessionFactory;
import wicket.Request;
import wicket.Session;

import wicket.protocol.http.WebApplication;

/**
 * A factory used by wicket to instantiate new BugeaterSession objects whenever a
 * new web session is started.
 * 
 * @author pchapman
 */
public class BugeaterSessionFactory implements ISessionFactory
{
	/**
	 * Creates a new instance. 
	 */
	BugeaterSessionFactory()
	{
		super();
	}

	/**
	 * @see wicket.ISessionFactory#newSession()
	 */
	public Session newSession(Request req)
	{
		return new BugeaterSession((WebApplication)WebApplication.get());
	}	
}
