package bugeater.web.page;

import java.util.Arrays;

import bugeater.bean.CreateIssueBean;
import bugeater.domain.Issue;
import bugeater.domain.Priority;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;

import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.Session;
import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.CompoundPropertyModel;
import wicket.spring.injection.SpringBean;

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
		new AddIssueForm(this, "addIssueForm");
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
		public AddIssueForm(MarkupContainer parent, String id)
		{
			super(parent, id);
			new FeedbackPanel(this, "formFeedback");
			issueBean =
				new CreateIssueBean()
				.setCreator(
					((BugeaterSession)Session.get()).getUserBean()
				);
			setModel(new CompoundPropertyModel(issueBean));

			new TextField<String>(
					AddIssueForm.this, "summary"
				).setRequired(true);
			
			new DropDownChoice<String>(
					AddIssueForm.this, "project",
					issueService.getProjectsList()
				).setRequired(true);
			
			new DropDownChoice<String>(
					AddIssueForm.this, "category",
					issueService.getCategoriesList()
				).setRequired(true);

			new DropDownChoice<Priority>(
					AddIssueForm.this, "priority",
					Arrays.<Priority>asList(Priority.values())
				).setRequired(true);
			
			new TextArea<String>(
					AddIssueForm.this, "description"
				).setRequired(true);
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
