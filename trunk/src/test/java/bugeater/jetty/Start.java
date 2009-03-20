package bugeater.jetty;

import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.mockejb.jndi.MockContextFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start
{
	private static final Logger logger = LoggerFactory.getLogger(Start.class);

	public static void main(String[] args) throws Exception {
		initJNDI();
		Server server = new Server();
		SocketConnector connector = new SocketConnector();
		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8081);
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/bugeater");
		bb.setWar("src/main/webapp");
		bb.setDescriptor("src/main/webapp/web.xml");
		

		
		// START JMX SERVER
		// MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		// server.getContainer().addEventListener(mBeanContainer);
		// mBeanContainer.start();
		
		server.addHandler(bb);

		try {
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			while (System.in.available() == 0) {
				Thread.sleep(5000);
			}
			server.stop();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}
	
	protected static final boolean initJNDI() {
		try {
			// Get the properties file that contains connectivity info
			ResourceBundle rBundle = ResourceBundle.getBundle("jetty_test_config");
	
			// Install MockEJB's context factory
			MockContextFactory.setAsInitial();
	        // create the initial context that will be used for binding EJBs
			Context context = new InitialContext();
	        // Create an instance of the MockContainer
//	        MockContainer mockContainer = new MockContainer( context );

	        // Set up DataSource implementation
	        // Use Jakarta DBCP
			logger.debug(
				"Driver: {}\tURL: {}\tUsername: {}\tJNDI Name: {}",
				new String[]{
					rBundle.getString("jdbc.driver"),
					rBundle.getString("jdbc.url"),
					rBundle.getString("jdbc.username"),
					rBundle.getString("datasource.jndiName")
				}
			);
			BasicDataSource ds = new BasicDataSource();
	        ds.setDriverClassName(rBundle.getString("jdbc.driver"));
	        ds.setUrl(rBundle.getString("jdbc.url"));
	        ds.setUsername(rBundle.getString("jdbc.username"));
	        ds.setPassword(rBundle.getString("jdbc.password"));
	        // add to the context
	        context.rebind(rBundle.getString("datasource.jndiName"), ds);
	        return true;
		} catch (NamingException ne) {
			return false;
		}
	}
}
