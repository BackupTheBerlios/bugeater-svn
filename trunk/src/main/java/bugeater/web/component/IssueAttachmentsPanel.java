package bugeater.web.component;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;
import bugeater.service.AttachmentService;
import bugeater.web.BugeaterConstants;
import bugeater.web.model.IssueAttachmentsListModel;
import bugeater.web.model.IssueModel;

/**
 * A panel that shows all attachments for an issue and allows the user to
 * upload more.
 * 
 * @author pchapman
 */
public class IssueAttachmentsPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(IssueAttachmentsPanel.class);
	
	/**
	 * @param parent The parent in which the panel belongs.
	 * @param id The id of the panel in markup.
	 * @param issue The ID of the issue for which attachments are to be
	 *              displayed.
	 */
	public IssueAttachmentsPanel(
			String id, Long issueID
		)
	{
		this(id, new IssueModel(issueID));
	}

	/**
	 * @param parent The parent in which the panel belongs.
	 * @param id The id of the panel in markup.
	 * @param issue The issue for which attachments are to be displayed.
	 */
	public IssueAttachmentsPanel(
			String id, Issue issue
		)
	{
		this(id, new IssueModel(issue));
	}

	/**
	 * @param parent The parent in which the panel belongs.
	 * @param id The id of the panel in markup.
	 * @param model A model that provides the issue for which attachments are
	 *              to be displayed.
	 */
	public IssueAttachmentsPanel(
			String id, IModel <Issue>model
		)
	{
		super(id, model);
		issueModel = model;
		add(new ListView<Attachment>(
				"attachmentsList", new IssueAttachmentsListModel(issueModel)
			)
		{
			private static final long serialVersionUID = 1L;
			public void populateItem(ListItem <Attachment>item)
			{
				Attachment att = item.getModelObject();
				ResourceReference ref = new ResourceReference("attachment");
				String url =
					getRequestCycle().urlFor(ref)  + "?" +
					BugeaterConstants.PARAM_NAME_ATTACHMENT_ID + "=" +
					att.getId().toString();
                ExternalLink link = new ExternalLink("downloadLink", url);
				item.add(link);
				ValueMap params = new ValueMap();
				params.add(
						BugeaterConstants.PARAM_NAME_ATTACHMENT_ID,
						att.getId().toString()
					);
				link.add(new Label("attachmentLabel", att.getFileName()));
			}
		});
		add(new AddAttachmentForm("addForm"));
	}
	
	@SpringBean
	private AttachmentService aService;
	public void setAttachmentService(AttachmentService service)
	{
		this.aService = service;
	}
	
	class AddAttachmentForm extends Form
	{
		private static final long serialVersionUID = 1L;

		public AddAttachmentForm(String id)
		{
			super(id);
			add(uploadField = new FileUploadField("attachmentFile"));
		}
		
		private FileUploadField uploadField;
		
		public void onSubmit()
		{
			FileUpload upload = uploadField.getFileUpload();
			if (upload != null) {
				Attachment a =
					new Attachment()
					.setIssue(issueModel.getObject())
					.setContentType(upload.getContentType())
					.setFileName(upload.getClientFileName());
				try {
					InputStream is = upload.getInputStream();
					aService.save(a, is);
					is.close();
				} catch (IOException ioe) {
					logger.error(ioe);
				}
			}
		}
	}
	
	private IModel<Issue> issueModel;
}
