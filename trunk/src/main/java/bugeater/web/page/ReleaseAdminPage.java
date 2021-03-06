package bugeater.web.page;

import java.text.DateFormat;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

import bugeater.bean.ReleaseVersionBean;
import bugeater.domain.ReleaseVersion;
import bugeater.service.ReleaseVersionService;
import bugeater.service.SortOrder;
import bugeater.web.BugeaterConstants;
import bugeater.web.model.ReleaseVersionsListModel;

/**
 * @author pchapman
 *
 */
public class ReleaseAdminPage extends BugeaterPage
{
	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param arg0
	 */
	public ReleaseAdminPage(PageParameters params)
	{
		super(params);
		ReleaseVersionBean bean = null;
		if (params.containsKey(BugeaterConstants.PARAM_NAME_RELEASE_VER_ID)) {
			try {
				ReleaseVersion rv =
					releaseVersionService.load(
							params.getLong(BugeaterConstants.PARAM_NAME_RELEASE_VER_ID)
						);
				bean = new ReleaseVersionBean(rv);
			} catch (StringValueConversionException svce) {
				logger.error(svce);
			}
		} else if (params.containsKey(BugeaterConstants.PARAM_NAME_PROJECT)) {
			bean = new ReleaseVersionBean();
			bean.setProject(
					params.getString(BugeaterConstants.PARAM_NAME_PROJECT)
				);
		}
		if (bean == null) {
			setResponsePage(Home.class);
		} else {
			init(bean);
		}
	}
	
	public ReleaseAdminPage(String project)
	{
		super();
		init(new ReleaseVersionBean().setProject(project));
	}
	
	public ReleaseAdminPage(ReleaseVersionBean bean)
	{
		super();
		init(bean);
	}
	
	@SpringBean
	private ReleaseVersionService releaseVersionService;
	public void setReleaseVersionService(ReleaseVersionService service)
	{
		this.releaseVersionService = service;
	}
	
	private void init(ReleaseVersionBean bean)
	{
		add(new Label("projectLabel", bean.getProject()));
		add(new ListView<ReleaseVersion>(
				"releaseList",
				new ReleaseVersionsListModel(
						bean.getProject(), SortOrder.Ascending
					)
			)
		{
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unchecked")
			public void populateItem(ListItem<ReleaseVersion> item)
			{
				ReleaseVersion rv = item.getModelObject();
				final Long rvID = rv.getId();
				Link link = new Link("editReleaseLink")
				{
					private static final long serialVersionUID = 1L;
					public void onClick()
					{
						PageParameters params = new PageParameters();
						params.put(
								BugeaterConstants.PARAM_NAME_RELEASE_VER_ID,
								rvID.toString()
							);
						setResponsePage(ReleaseAdminPage.class, params);
					}
				};
				item.add(link);
				link.add(new Label("versionLabel", rv.getVersionNumber()));
				item.add(new Label(
						"scheduledDateLabel",
						DATE_FORMAT.format(
								rv.getScheduleReleaseDate().getTime()
							)
					));
				item.add(new Label(
						"actualDateLabel",
						(
								rv.getActualReleaseDate() == null ? "" :
							DATE_FORMAT.format(
									rv.getActualReleaseDate().getTime()
								)
						)
					));
			}
		});
		add(new ReleaseAdminForm("form", bean));
	}
	
	private class ReleaseAdminForm extends Form<ReleaseVersionBean>
	{
		private static final long serialVersionUID = 1L;
		
		@SuppressWarnings("unchecked")
		ReleaseAdminForm(String wicketID, ReleaseVersionBean bean)
		{
			super(wicketID, new CompoundPropertyModel(bean));
			
			// Instructions
			IModel<String>sModel;
			if (bean.getId() == null) {
				sModel = new ResourceModel("instructions.add");
			} else {
				sModel = new ResourceModel("instructions.edit");
			}
			add(new FeedbackPanel("formFeedback"));
			add(new Label("instructions", sModel));
			
			// Version number
			add(new TextField("versionNumber").setRequired(true));
			
			// Scheduled release date
			TextField field =
				new DateTextField("scheduledReleaseDate", null, new StyleDateConverter("S-", true));
			add(field);
			field.setRequired(true);
			field.add(new DatePicker());
			
			// Actual date
			WebMarkupContainer cont =
				new WebMarkupContainer("actualWicketDateInput");
			add(cont);
			cont.add(field = new DateTextField("actualReleaseDate", null, new StyleDateConverter("S-", true)));
			field.add(new DatePicker());
			cont.setRenderBodyOnly(true);
			cont.setVisible(bean.getId() != null);
			
			String s;
			if (bean.getId() == null) {
				s = "submit.add";
			} else {
				s = "submit.edit";
			}
			Button b = new Button("submit")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit() {
					doSave();
				}
			};
			add(b);
			b.add(new AttributeModifier("value", true, new ResourceModel(s)));
		}
				
		private void doSave()
		{
			ReleaseVersionBean bean = getModel().getObject();
			releaseVersionService.save(bean);
			PageParameters params = new PageParameters();
			params.put(BugeaterConstants.PARAM_NAME_PROJECT, bean.getProject());
			setResponsePage(ReleaseAdminPage.class, params);
		}
	}
}
