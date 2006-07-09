package bugeater.web.component;

import java.util.List;

import bugeater.domain.Issue;
import bugeater.web.BugeaterConstants;
import bugeater.web.page.ViewIssuePage;

import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

/**
 * A panel that shows a list of issues and provides a links to view them.
 * 
 * @author pchapman
 */
public class IssuesListPanel extends Panel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param parent The parent of this panel.
	 * @param id The wicketid of the panel.
	 * @param model The model that contains the issues to list.
	 */
	public IssuesListPanel(
			MarkupContainer parent, String id, IModel <List<Issue>>model
		)
	{
		super(parent, id);
		
		new ListView<Issue>(this, "issueList", model)
		{
			private static final long serialVersionUID = 1L;
			public void populateItem(ListItem <Issue>item)
			{
				Issue i = item.getModelObject();
				final Long issueid = i.getId();
				Link link = new Link(item, "viewIssueLink")
				{
					private static final long serialVersionUID = 1L;
					public void onClick()
					{
						PageParameters params = new PageParameters();
						params.add(
								BugeaterConstants.PARAM_NAME_ISSUE_ID,
								issueid.toString()
							);
						setResponsePage(ViewIssuePage.class, params);
					}
				};
				new Label(link, "summaryLabel", i.getSummary());
				new Label(item, "projectLabel", i.getProject());
				new Label(item, "priorityLabel", i.getPriority().toString());
				new Label(item, "statusLabel", i.getCurrentStatus().toString());
				new WatchIssueLink(item, "watchIssueLink", i);
			}
		};
	}
}
