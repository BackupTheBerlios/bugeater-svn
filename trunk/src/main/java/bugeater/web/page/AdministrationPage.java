package bugeater.web.page;

import java.util.List;

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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import bugeater.service.IssueService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;

/**
 * Allows the user to administer lookup lists for the application
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.ADMINISTRATOR})
public class AdministrationPage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	public AdministrationPage()
	{
		super();
		new AddForm("addForm");
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
		
		AddForm(String id)
		{
			super(id);
			new FeedbackPanel("formFeedback");
			
			new ListView<String>("categoryList", new CategoriesModel())
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem<String> item)
				{
					item.add(new Label(
							"categoryLabel",
							item.getModelObject()
						));
				}
			};
			final IModel<String> catModel = new Model<String>();
			new TextField<String>("categoryField", catModel);
			new Button("addCategoryBtn")
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
			
			add(new ListView<String>("projectList", new ProjectsModel())
			{
				private static final long serialVersionUID = 1L;
				public void populateItem(ListItem<String> item)
				{
					final String project = item.getModelObject();
					Link link = new Link("editReleaseLink")
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
					item.add(link);
					link.add(new Label("projectLabel", project));
				}
			});
			final IModel<String> projModel = new Model<String>();
			add(new TextField<String>("projectField", projModel));
			add(new Button("addProjectBtn")
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
			});
		}
	}
	
	private class CategoriesModel extends AbstractReadOnlyModel<List <String>>
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
	
	private class ProjectsModel extends AbstractReadOnlyModel<List <String>>
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
