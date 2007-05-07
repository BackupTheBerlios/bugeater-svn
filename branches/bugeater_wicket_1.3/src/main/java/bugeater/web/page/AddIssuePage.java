package bugeater.web.page;

import java.util.Arrays;

import bugeater.bean.CreateIssueBean;
import bugeater.domain.Issue;
import bugeater.domain.Priority;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
			super(id);
			parent.add(this);
			add(new FeedbackPanel("formFeedback"));
			issueBean =
				new CreateIssueBean()
				.setCreator(
					((BugeaterSession)Session.get()).getUserBean()
				);
			setModel(new CompoundPropertyModel(issueBean));

			add(new TextField("summary").setRequired(true));

			add(new DropDownChoice("project", issueService.getProjectsList()).setRequired(true));
			
			add(new DropDownChoice("category", issueService.getCategoriesList()).setRequired(true));

			add(new DropDownChoice(
					"priority", Arrays.<Priority>asList(Priority.values())
				).setRequired(true));
			
			add(new TextArea("description").setRequired(true));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		@SuppressWarnings("unchecked")
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
