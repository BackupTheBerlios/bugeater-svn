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
import wicket.model.Model;

/**
 * Provides a clickable image that will either add the user to an issue's
 * watch list or remove the user from the issue's watch list.
 * 
 * @author pchapman
 */
public class WatchIssueLink extends Panel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	private IModel<Boolean>watchedModel;
	
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
		final Long issueID = i.getId();
		final boolean assigned = userID.equals(i.getAssignedUserID());
		watchedModel = new Model<Boolean>(i.getWatchers().contains(userID));
		Link link = new Link(this, "watchIssueLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				IssueService service =
					(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
				Issue i = service.load(issueID);
				BugeaterSession sess = (BugeaterSession)Session.get();
				if (watchedModel.getObject()) {
					i.getWatchers().remove(sess.getUserBean().getId());
				} else {
					i.getWatchers().add(sess.getUserBean().getId());
				}
				watchedModel.setObject(watchedModel.getObject());
				service.save(i);
			}
		};
		new StaticImage(link, "watchIssueImg", new UrlModel());
		setVisible(!assigned);
	}
	
	private class UrlModel extends Model<String>
	{
		private static final long serialVersionUID = 1L;
		/**
		 * @see wicket.model.Model#getNestedModel()
		 */
		@Override
		public IModel getNestedModel()
		{
			return watchedModel;
		}

		/**
		 * @see wicket.model.Model#getObject()
		 */
		@Override
		public String getObject()
		{
			return
				((BugeaterApplication)Application.get()).getServerContextPath() +
				"/images/" + (watchedModel.getObject() ? "no" : "") +
				"watch.png";
		}

		/**
		 * @see wicket.model.Model#setObject(T)
		 */
		@Override
		public void setObject(String object)
		{
			// Not implemented
		}

		/**
		 * @see wicket.model.Model#toString()
		 */
		@Override
		public String toString()
		{
			return getObject();
		}
	}
}
