package bugeater.web.page;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import bugeater.domain.Issue;
import bugeater.service.SecurityRole;
import bugeater.web.component.IssuesListPanel;

/**
 * A page which lists issues in the model passed to it.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.USER})
public class IssuesListPage extends BugeaterPage
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
		add(new Label(
				"descLabel", listDescriptionModel
			).setEscapeModelStrings(true));
		add(new IssuesListPanel("issuesList", model));
	}
}
