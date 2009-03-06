package bugeater.web.component;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterSession;
import bugeater.web.model.IssueModel;

/**
 * Provides a clickable image that will either add the user to an issue's
 * watch list or remove the user from the issue's watch list.
 * 
 * @author pchapman
 */
public class WatchIssueLink extends Panel
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
			String id, Long issueID
		)
	{
		this(id, new IssueModel(issueID));
	}	
	
	/**
	 * @param parent The parent.
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(
			String id, Issue issue
		)
	{
		this(id, new IssueModel(issue));
	}	
	
	/**
	 * @param parent The parent.
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(
			String id, IModel<Issue> model
		)
	{
		super(id, model);
		issueModel = model;
		BugeaterSession sess = (BugeaterSession)Session.get();
		String userID = sess.getUserBean().getId();
		Issue i = issueModel.getObject();
		final boolean assigned = userID.equals(i.getAssignedUserID());
		add(link1 = new Link("watchIssueLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				toggleWatch(true);
			}
			
			public boolean isVisible() {
				return !assigned;
			}
		});
		add(link2 = new Link("noWatchIssueLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				toggleWatch(false);
			}
			
			public boolean isVisible() {
				return assigned;
			}
		});
	}
	
	private void toggleWatch(boolean watch)
	{
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		Issue i = issueModel.getObject();
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
	
	private IModel<Issue> issueModel;
}
