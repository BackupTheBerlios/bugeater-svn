package bugeater.web.page;

import java.util.Arrays;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import bugeater.bean.CreateIssueBean;
import bugeater.domain.Issue;
import bugeater.domain.Priority;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;

/**
 * A web page in which a new issue can be created.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({
	SecurityRole.ADMINISTRATOR, SecurityRole.DEVELOPER,
	SecurityRole.MANAGER, SecurityRole.TESTER
})
public class AddIssuePage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new page instance.
	 */
	public AddIssuePage()
	{
		super();
		add(new AddIssueForm("addIssueForm"));
	}
	
	@SpringBean
	private IssueService issueService;
	public void setIssueService(IssueService service)
	{
		this.issueService = service;
	}

	class AddIssueForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		private CreateIssueBean issueBean;
		
		@SuppressWarnings("unchecked")
		public AddIssueForm(String id)
		{
			super(id);
			add(new FeedbackPanel("formFeedback"));
			issueBean =
				new CreateIssueBean()
				.setCreator(
					((BugeaterSession)Session.get()).getUserBean()
				);
			setModel(new CompoundPropertyModel(issueBean));

			add(new RequiredTextField<String>("summary"));
			
			add(new DropDownChoice<String>(
					"project", issueService.getProjectsList()
				).setRequired(true));
			
			add(new DropDownChoice<String>(
					"category", issueService.getCategoriesList()
				).setRequired(true));

			add(new DropDownChoice<Priority>(
					"priority", Arrays.<Priority>asList(Priority.values())
				).setRequired(true));
			
			add(new TextArea<String>("description").setRequired(true));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit()
		{
			Issue issue = issueService.createIssue(issueBean);
			PageParameters params = new PageParameters();
			params.add(
					BugeaterConstants.PARAM_NAME_ISSUE_ID,
					String.valueOf(issue.getId())
				);
			setResponsePage(ViewIssuePage.class, params);
		}
	}
}
