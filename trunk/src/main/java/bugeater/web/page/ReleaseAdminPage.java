package bugeater.web.page;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import bugeater.bean.ReleaseVersionBean;
import bugeater.domain.ReleaseVersion;
import bugeater.service.ReleaseVersionService;
import bugeater.service.SortOrder;
import bugeater.web.BugeaterConstants;
import bugeater.web.model.ReleaseVersionsListModel;

import wicket.AttributeModifier;
import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.extensions.markup.html.datepicker.DatePicker;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.CompoundPropertyModel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.model.ResourceModel;
import wicket.spring.injection.SpringBean;
import wicket.util.string.StringValueConversionException;

/**
 * @author pchapman
 *
 */
public class ReleaseAdminPage extends BugeaterPage
{
	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
	private static final DateFormat SHORT_DATE_FORMAT =
		DateFormat.getDateInstance(DateFormat.SHORT);
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param arg0
	 */
	@SuppressWarnings("unchecked")
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
		new Label(this, "projectLabel", bean.getProject());
		new ListView<ReleaseVersion>(
				this, "releaseList",
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
				Link link = new Link(item, "editReleaseLink")
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
				new Label(link, "versionLabel", rv.getVersionNumber());
				new Label(
						item, "scheduledDateLabel",
						DATE_FORMAT.format(
								rv.getScheduleReleaseDate().getTime()
							)
					);
				new Label(
						item, "actualDateLabel",
						(
								rv.getActualReleaseDate() == null ? "" :
							DATE_FORMAT.format(
									rv.getActualReleaseDate().getTime()
								)
						)
					);
			}
		};
		new ReleaseAdminForm(this, "form", bean);
	}
	
	private class ReleaseAdminForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		@SuppressWarnings("unchecked")
		ReleaseAdminForm(
				MarkupContainer container, String wicketID,
				ReleaseVersionBean bean
			)
		{
			super(container, wicketID, new CompoundPropertyModel(bean));
			this.bean = bean;
			
			// Instructions
			IModel<String>sModel;
			if (bean.getId() == null) {
				sModel = new ResourceModel("instructions.add");
			} else {
				sModel = new ResourceModel("instructions.edit");
			}
			new FeedbackPanel(this, "formFeedback");
			new Label(this, "instructions", sModel);
			
			// Version number
			new TextField(this, "versionNumber").setRequired(true);
			
			// Scheduled release date
			sdModel = new Model<String>();
			if (bean.getScheduledReleaseDate() != null) {
				sdModel.setObject(SHORT_DATE_FORMAT.format(bean.getScheduledReleaseDate().getTime()));
			}
			TextField field =
				new TextField<String>(this, "scheduledReleaseDate", sdModel);
			field.setRequired(true);
			new DatePicker(this, "scheduledReleaseDatePicker", field);
			
			// Actual date
			adModel = new Model<String>();
			if (bean.getActualReleaseDate() != null) {
				adModel.setObject(SHORT_DATE_FORMAT.format(bean.getActualReleaseDate().getTime()));
			}
			WebMarkupContainer cont =
				new WebMarkupContainer(this, "actualWicketDateInput");
			field = new TextField<String>(cont, "actualReleaseDate", adModel);
			new DatePicker(cont, "actualReleaseDatePicker", field);
			cont.setRenderBodyOnly(true);
			cont.setVisible(bean.getId() != null);
			
			String s;
			if (bean.getId() == null) {
				s = getString("submit.add");
			} else {
				s = getString("submit.edit");
			}
			Button b = new Button(this, "submit")
			{
				private static final long serialVersionUID = 1L;
				public void onSubmit() {
					doSave();
				}
			};
			b.add(new AttributeModifier("value", true, new Model<String>(s)));
		}
		
		private ReleaseVersionBean bean;
		private IModel<String> sdModel;
		private IModel<String> adModel;
		
		@SuppressWarnings("unchecked")
		private void doSave()
		{
			String s = sdModel.getObject();
			Calendar c = null;
			try {
				Date d = SHORT_DATE_FORMAT.parse(s);
				c = Calendar.getInstance();
				c.setTime(d);
			} catch (ParseException pe) {
				error(getString("error.invalid.date"));
				return;
			}
			bean.setScheduledReleaseDate(c);

			c = null;
			s = adModel.getObject();
			if (s != null && s.length() > 0) {
				try {
					Date d = SHORT_DATE_FORMAT.parse(s);
					c = Calendar.getInstance();
					c.setTime(d);
				} catch (ParseException pe) {
					error(getString("error.invalid.date"));
					return;
				}
			}
			bean.setActualReleaseDate(c);
			
			releaseVersionService.save(bean);
			PageParameters params = new PageParameters();
			params.put(BugeaterConstants.PARAM_NAME_PROJECT, bean.getProject());
			setResponsePage(ReleaseAdminPage.class, params);
		}
	}
}
