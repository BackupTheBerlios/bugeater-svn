package bugeater.web.component;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterSession;
import bugeater.web.model.IssueModel;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.Session;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

/**
 * Provides a clickable image that will either add the user to an issue's
 * watch list or remove the user from the issue's watch list.
 * 
 * @author pchapman
 */
public class WatchIssueLink extends Panel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	private Link link1;
	private Link link2;
	
	/**
	 * @param parent The parent.
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(
			MarkupContainer parent, String id, Long issueID
		)
	{
		this(parent, id, new IssueModel(issueID));
	}	
	
	/**
	 * @param parent The parent.
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(
			MarkupContainer parent, String id, Issue issue
		)
	{
		this(parent, id, new IssueModel(issue));
	}	
	
	/**
	 * @param parent The parent.
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(
			MarkupContainer parent, String id, IModel<Issue> model
		)
	{
		super(parent, id, model);
		BugeaterSession sess = (BugeaterSession)Session.get();
		String userID = sess.getUserBean().getId();
		Issue i = getModelObject();
		final boolean assigned = userID.equals(i.getAssignedUserID());
		link1 = new Link(this, "watchIssueLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				toggleWatch(true);
			}
		};
		link1.setVisible(!assigned);
		link2 = new Link(this, "noWatchIssueLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				toggleWatch(false);
			}
		};
		link2.setVisible(assigned);
	}
	
	private void toggleWatch(boolean watch)
	{
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		Issue i = getModelObject();
		BugeaterSession sess = (BugeaterSession)Session.get();
		if (watch) {
			i.getWatchers().add(sess.getUserBean().getId());
		} else {
			i.getWatchers().remove(sess.getUserBean().getId());
		}
		service.save(i);
		link1.setVisible(!watch);
		link2.setVisible(watch);
	}
}
