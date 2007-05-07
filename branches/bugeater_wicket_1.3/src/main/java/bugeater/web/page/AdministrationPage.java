package bugeater.web.page;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Allows the user to administer lookup lists for the application
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.ADMINISTRATOR})
public class AdministrationPage extends BugeaterPage<Issue>
{
	private static final long serialVersionUID = 1L;
	
	public AdministrationPage()
	{
		super();
		new AddForm(this, "addForm");
	}
	
	@SpringBean
	private IssueService issueService;
	public void setIssueService(IssueService service)
	{
		this.issueService = service;
	}
	
	class AddForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		AddForm(MarkupContainer container, String id)
		{
			super(id);
			container.add(this);
			add(new FeedbackPanel("formFeedback"));
			
			add(new ListView("categoryList", new CategoriesModel())
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem item)
				{
					item.add(
						new Label(
							"categoryLabel", item.getModelObjectAsString()
						)
					);
				}
			});
			final IModel catModel = new Model();
			this.add(new TextField("categoryField", catModel));
			add(new Button("addCategoryBtn")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					Object o = catModel.getObject();
					String s = o == null ? null : o.toString();
					if (s != null && s.length() > 0) {
						AdministrationPage.this.issueService.addCategory(s);
						catModel.setObject(null);
					}
				}
			});
			
			add(new ListView("projectList", new ProjectsModel())
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem item)
				{
					final String project = item.getModelObject().toString();
					item.add(new Link("editReleaseLink")
					{
						private static final long serialVersionUID = 1L;
						@SuppressWarnings("unchecked")
						public void onClick()
						{
							PageParameters params = new PageParameters();
							params.add(
									BugeaterConstants.PARAM_NAME_PROJECT,
									project
								);
							setResponsePage(ReleaseAdminPage.class, params);
						}
					});
					item.add(new Label("projectLabel", project));
				}
			});
			final IModel projModel = new Model();
			this.add(new TextField("projectField", projModel));
			this.add(new Button("addProjectBtn")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					String s = projModel.getObject().toString();
					if (s != null || s.length() > 0) {
						AdministrationPage.this.issueService.addProject(s);
						projModel.setObject(null);
					}
				}
			});
		}
	}
	
	private class CategoriesModel extends Model
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * @see wicket.model.Model#getObject()
		 */
		@Override
		public Object getObject()
		{
			return issueService.getCategoriesList();
		}
	}
	
	private class ProjectsModel extends Model
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * @see wicket.model.Model#getObject()
		 */
		@Override
		public Object getObject()
		{
			return issueService.getProjectsList();
		}
	}
}
