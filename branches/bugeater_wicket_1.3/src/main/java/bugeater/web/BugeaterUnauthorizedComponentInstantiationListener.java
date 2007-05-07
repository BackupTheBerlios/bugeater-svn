package bugeater.web;

import bugeater.web.page.LoginPage;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;

/**
 * Listens for an exception thrown when a component cannot be instantiated
 * due to lack of authorization.
 * 
 * @author pchapman
 */
public class BugeaterUnauthorizedComponentInstantiationListener
	implements IUnauthorizedComponentInstantiationListener
{
	/**
	 * Creates a new instance.
	 */
	public BugeaterUnauthorizedComponentInstantiationListener()
	{
		super();
	}

	/**
	 * @see wicket.authorization.IUnauthorizedComponentInstantiationListener#onUnauthorizedInstantiation(wicket.Component)
	 */
	public void onUnauthorizedInstantiation(Component component)
	{
		// Get sign-in page class
		final Class<? extends Page> signInPageClass = LoginPage.class;

		// If there is a sign in page class declared, and the unauthorized
		// component is a page, but it's not the sign in page and the user
		// is not logged in.
		if (
				signInPageClass != null &&
				component instanceof Page &&
				signInPageClass != component.getClass() &&
				((BugeaterSession)Session.get()).getPrincipal() == null
			)
		{
			// Redirect to intercept page to let the user sign in
			throw new RestartResponseAtInterceptPageException(signInPageClass);
		} else {
			// The component was not a page, so throw an exception
			throw new UnauthorizedInstantiationException(component.getClass());
		}
	}
}
