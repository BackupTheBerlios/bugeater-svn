package bugeater.web.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.include.Include;

import bugeater.web.BugeaterConstants;

/**
 * A page used to show static content.
 * 
 * @author pchapman
 */
public class StaticContentPage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param params
	 */
	public StaticContentPage(PageParameters params)
	{
		super(params);
		String url = params.getString(BugeaterConstants.PARAM_NAME_CONTENT_URL);
		add(new Include("content", url));
	}
}
