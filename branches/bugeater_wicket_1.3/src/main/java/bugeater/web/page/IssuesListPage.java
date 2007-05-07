package bugeater.web.page;

import java.util.List;

import bugeater.domain.Issue;
import bugeater.service.SecurityRole;
import bugeater.web.component.IssuesListPanel;

import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.basic.Label;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * A page which lists issues in the model passed to it.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.USER})
public class IssuesListPage extends BugeaterPage<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param model The model that provides the list of issues to display.
	 * @param listDescription A description of the list, such as &quot;Issues
	 *                        with text matching 'Null Pointer'&quot;.
	 */
	public IssuesListPage(
			IModel <List<Issue>>model, String listDescription
		)
	{
		this(model, new Model<String>(listDescription));
	}
	
	/**
	 * @param model The model that provides the list of issues to display.
	 * @param listDescriptionModel A model that provides a description of the
	 *                             list, such as &quot;Issues with text
	 *                             matching 'Null Pointer'&quot;.
	 */
	public IssuesListPage(
			IModel <List<Issue>>model, IModel<String>listDescriptionModel
		)
	{
		super(model);
		new Label(
				this, "descLabel", listDescriptionModel
			).setEscapeModelStrings(true);
		new IssuesListPanel(this, "issuesList", model);
	}
}
