package bugeater.web.component;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import bugeater.domain.Issue;
import bugeater.web.BugeaterConstants;
import bugeater.web.page.ViewIssuePage;

/**
 * A panel that shows a list of issues and provides a links to view them.
 * 
 * @author pchapman
 */
public class IssuesListPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param parent The parent of this panel.
	 * @param id The wicketid of the panel.
	 * @param model The model that contains the issues to list.
	 */
	public IssuesListPanel(
			String id, IModel <List<Issue>>model
		)
	{
		super(id);
		
		add(new ListView<Issue>("issueList", model)
		{
			private static final long serialVersionUID = 1L;
			public void populateItem(ListItem <Issue>item)
			{
				Issue i = item.getModelObject();
				final Long issueid = i.getId();
				PageParameters params = new PageParameters();
				params.add(
						BugeaterConstants.PARAM_NAME_ISSUE_ID,
						issueid.toString()
					);
				Link link =
					new BookmarkablePageLink(
							"viewIssueLink", ViewIssuePage.class, params
						);
				link.add(new Label("summaryLabel", i.getSummary()));
				item.add(new Label("projectLabel", i.getProject()));
				item.add(new Label("priorityLabel", i.getPriority().toString()));
				item.add(new Label("statusLabel", i.getCurrentStatus().toString()));
				item.add(new WatchIssueLink("watchIssueLink", i));
			}
		});
	}
}
