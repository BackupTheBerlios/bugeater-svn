package bugeater.web.page;

import bugeater.service.SecurityRole;
import bugeater.web.component.SearchPanel;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * A page that will allow the user to search for issues.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.USER})
public class SearchPage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance
	 */
	public SearchPage()
	{
		super();
		add(new SearchPanel("searchPanel"));
	}
}
