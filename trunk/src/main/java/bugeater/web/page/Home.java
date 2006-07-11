package bugeater.web.page;

import java.security.Principal;

import javax.servlet.ServletException;

import bugeater.bean.IUserBean;

import bugeater.service.AuthenticationService;
import bugeater.service.SecurityRole;

import bugeater.web.BugeaterSession;
import bugeater.web.component.IssuesListPanel;
import bugeater.web.component.SearchPanel;
import bugeater.web.model.UserIssuesListModel;

import wicket.PageMap;
import wicket.Session;

import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import wicket.markup.html.basic.Label;
import wicket.markup.html.link.PageLink;
import wicket.model.ResourceModel;

import wicket.spring.injection.SpringBean;

/**
 * The start page for the webapp.  This page lists all the user's
 * assigned (if applicable) and watched issues.  From here, they may
 * search for other issues to view, look at a list of issues that they
 * created, or create a new issue.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.USER})
public class Home extends BugeaterPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance.
	 */
	public Home()
	{
		super();
		// Load the UI
		IUserBean user = ((BugeaterSession)Session.get()).getUserBean();
		new Label(this, "userLabel", getString("label.welcome").replace("$", user.toString()));

		// Assigned issues
		Principal p = ((BugeaterSession)Session.get()).getPrincipal();
		boolean assignable = false;
		try {
			assignable =
				authService.isUserInRole(p, SecurityRole.Developer) ||
				authService.isUserInRole(p, SecurityRole.Tester);
		} catch (ServletException se) {
			logger.error(se);
		}
		Label label = new Label(this, "assignedLabel", new ResourceModel("label.assigned"));
		IssuesListPanel listPanel =
			new IssuesListPanel(
					this, "assignedPanel",
					new UserIssuesListModel(
							UserIssuesListModel.AssociationType.Assigned,
							user.getId()
						)
				);
		label.setVisible(assignable);
		listPanel.setVisible(assignable);

		// Watched issues
		listPanel =
			new IssuesListPanel(
					this, "watchedPanel",
					new UserIssuesListModel(
							UserIssuesListModel.AssociationType.Watched,
							user.getId()
						)
				);
		label.setVisible(assignable);
		listPanel.setVisible(assignable);
	}

	/**
	 * Creates a new instance.
	 * @param pageMap
	 */
	public Home(PageMap pageMap)
	{
		this();
	}
	
	@SpringBean
	private AuthenticationService authService;
	public void setAuthenticationService(AuthenticationService service)
	{
		this.authService = service;
	}
}
