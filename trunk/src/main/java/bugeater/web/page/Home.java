package bugeater.web.page;

import java.security.Principal;

import javax.servlet.ServletException;

import org.apache.wicket.PageMap;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import bugeater.bean.IUserBean;
import bugeater.service.AuthenticationService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterSession;
import bugeater.web.component.IssuesListPanel;
import bugeater.web.model.UserIssuesListModel;

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
		add(new Label("userLabel", getString("label.welcome").replace("$", user.toString())));

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
		Label label = new Label("assignedLabel", new ResourceModel("label.assigned"));
		add(label);
		IssuesListPanel listPanel =
			new IssuesListPanel(
					"assignedPanel",
					new UserIssuesListModel(
							UserIssuesListModel.AssociationType.Assigned,
							user.getId()
						)
				);
		add(listPanel);
		label.setVisible(assignable);
		listPanel.setVisible(assignable);

		// Watched issues
		listPanel =
			new IssuesListPanel(
					"watchedPanel",
					new UserIssuesListModel(
							UserIssuesListModel.AssociationType.Watched,
							user.getId()
						)
				);
		add(listPanel);
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
