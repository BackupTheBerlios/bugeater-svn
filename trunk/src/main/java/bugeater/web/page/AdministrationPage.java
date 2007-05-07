package bugeater.web.page;

import java.util.List;

import bugeater.domain.Issue;
import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;

import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.spring.injection.SpringBean;

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
			super(container, id);
			new FeedbackPanel(this, "formFeedback");
			
			new ListView<String>(this, "categoryList", new CategoriesModel())
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem<String> item)
				{
					new Label(
							item, "categoryLabel",
							item.getModelObjectAsString()
						);
				}
			};
			final IModel<String> catModel = new Model<String>();
			new TextField<String>(this, "categoryField", catModel);
			new Button(this, "addCategoryBtn")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					String s = catModel.getObject();
					if (s != null && s.length() > 0) {
						AdministrationPage.this.issueService.addCategory(s);
						catModel.setObject(null);
					}
				}
			};
			
			new ListView<String>(this, "projectList", new ProjectsModel())
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem<String> item)
				{
					final String project = item.getModelObject();
					Link link = new Link(item, "editReleaseLink")
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
					};
					new Label(link, "projectLabel", project);
				}
			};
			final IModel<String> projModel = new Model<String>();
			new TextField<String>(this, "projectField", projModel);
			new Button(this, "addProjectBtn")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit()
				{
					String s = projModel.getObject();
					if (s != null || s.length() > 0) {
						AdministrationPage.this.issueService.addProject(s);
						projModel.setObject(null);
					}
				}
			};
		}
	}
	
	private class CategoriesModel extends Model<List <String>>
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * @see wicket.model.Model#getObject()
		 */
		@Override
		public List <String> getObject()
		{
			return issueService.getCategoriesList();
		}
	}
	
	private class ProjectsModel extends Model<List <String>>
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * @see wicket.model.Model#getObject()
		 */
		@Override
		public List <String> getObject()
		{
			return issueService.getProjectsList();
		}
	}
}
