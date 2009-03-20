package bugeater.web.page;

import org.apache.wicket.PageParameters;

/**
 * A page used to show information about bugeater.  The content is static.
 * However, wrapping it in a simple wicket page allows it to receive the
 * formatting of all other pages as well as easy internationalization.
 * 
 * @author pchapman
 */
public class AboutPage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	public AboutPage() {
		super();
	}
	
	/**
	 * @param params
	 */
	public AboutPage(PageParameters params)
	{
		super(params);
	}
}
