package bugeater.web.component;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.web.BugeaterApplication;
import bugeater.web.BugeaterSession;
import bugeater.web.model.IssueModel;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Provides a clickable image that will either add the user to an issue's
 * watch list or remove the user from the issue's watch list.
 * 
 * @author pchapman
 */
public class WatchIssueLink extends Panel
{
	private static final long serialVersionUID = 1L;
	
	private IModel watchedModel;
	
	/**
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(String id, Long issueID)
	{
		this(id, new IssueModel(issueID));
	}	
	
	/**
	 * @param parent The parent.
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(String id, Issue issue)
	{
		this(id, new IssueModel(issue));
	}	
	
	/**
	 * @param id The unique ID of the panel.
	 * @param model The model holding the issue.
	 */
	public WatchIssueLink(String id, IModel model)
	{
		super(id, model);
		BugeaterSession sess = (BugeaterSession)Session.get();
		String userID = sess.getUserBean().getId();
		Issue i = (Issue)getModelObject();
		final Long issueID = i.getId();
		final boolean assigned = userID.equals(i.getAssignedUserID());
		watchedModel = new Model(i.getWatchers().contains(userID));
		Link link = new Link("watchIssueLink")
		{
			private static final long serialVersionUID = 1L;
			public void onClick()
			{
				IssueService service =
					(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
				Issue i = service.load(issueID);
				BugeaterSession sess = (BugeaterSession)Session.get();
				if ((Boolean)watchedModel.getObject()) {
					i.getWatchers().remove(sess.getUserBean().getId());
				} else {
					i.getWatchers().add(sess.getUserBean().getId());
				}
				watchedModel.setObject(watchedModel.getObject());
				service.save(i);
			}
		};
		link.add(new StaticImage("watchIssueImg", new UrlModel()));
		this.add(link);
		setVisible(!assigned);
	}
	
	private class UrlModel implements IModel
	{
		private static final long serialVersionUID = 1L;
		
		public void onAttach() {}
		
		public void detach()
		{
			watchedModel.detach();
		}

		/**
		 * @see wicket.model.IModel#getObject()
		 */
		public String getObject()
		{
			return
				((BugeaterApplication)Application.get()).getServerContextPath() +
				"/images/" + (((Boolean)watchedModel.getObject()) ? "no" : "") +
				"watch.png";
		}

		/**
		 * @see wicket.model.IModel#setObject(T)
		 */
		public void setObject(Object object)
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
