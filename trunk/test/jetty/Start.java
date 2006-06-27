package jetty;

import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.BasicConfigurator;

import org.mockejb.jndi.MockContextFactory;

import org.mortbay.jetty.Server;

/**
 * A startup class for testing the TimeClock Web Application in a development
 * environment.
 */
public class Start
{
	private static final Log log = LogFactory.getLog(Start.class);

	private Start()
	{
		super();
	}

	/**
	 * Main function, starts the jetty server.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (log.isDebugEnabled()) {
			log.debug("Starting TimeClock webapp.");
		}
		if (initJNDI() && initJetty()) {
			// Run the first args.  Useful for causing it to launch the browser
			// automatically once jetty has initialized.
			if (args.length > 0) {
				try {
					Runtime.getRuntime().exec(args);
				} catch (IOException ioe) {
					log.error("Unable to execute the command: " + ioe);
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("SIMIS webapp started.");
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("SIMIS webapp not started.");
			}
		}
	}
	
	/**
	 * Initializes the JNDI context with a data source.
	 * 
	 * @return true if the initialization was successfull.
	 */
	protected static final boolean initJNDI()
	{
		try {
			// Get the properties file that contains connectivity info
			ResourceBundle rBundle = ResourceBundle.getBundle("configure");
	
			// Install MockEJB's context factory
			MockContextFactory.setAsInitial();

			// create the initial context that will be used for binding EJBs
			Context context = new InitialContext();

	        // Set up DataSource implementation for Bugeater
			BasicDataSource ds = new BasicDataSource();
	        ds.setDriverClassName(rBundle.getString("jdbc.driver"));
	        ds.setUrl(rBundle.getString("jdbc.url"));
	        ds.setUsername(rBundle.getString("jdbc.username"));
	        ds.setPassword(rBundle.getString("jdbc.password"));
	        // add to the context
	        context.rebind(rBundle.getString("datasource.jndiName"), ds);

	        return true;
		} catch (NamingException ne) {
			log.fatal("Could not initialize JNDI store: " + ne);
			return false;
		}
	}
	
	/**
	 * Starts Jetty.
	 * 
	 * @return true if the start was successfull
	 */
	protected static final boolean initJetty()
	{
        BasicConfigurator.configure();
        Server jettyServer = null;
		try
		{
			URL jettyConfig = new URL("file:test/jetty-config.xml");
			if (jettyConfig == null)
			{
				log.fatal("Unable to locate jetty-config.xml on the classpath");
			}
			jettyServer = new Server(jettyConfig);
			jettyServer.start();
			return true;
		}
		catch (Exception e)
		{
			log.fatal("Could not start the Jetty server: " + e);
			if (jettyServer != null)
			{
				try
				{
					jettyServer.stop();
				}
				catch (InterruptedException e1)
				{
					log.fatal("Unable to stop the jetty server: " + e1);
				}
			}
			return false;
		}
	}
}
