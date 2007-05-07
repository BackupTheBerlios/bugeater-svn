package bugeater.web.component;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;
import bugeater.service.AttachmentService;
import bugeater.web.BugeaterConstants;
import bugeater.web.model.IssueAttachmentsListModel;
import bugeater.web.model.IssueModel;

import wicket.MarkupContainer;
import wicket.ResourceReference;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.spring.injection.SpringBean;
import wicket.util.value.ValueMap;

/**
 * A panel that shows all attachments for an issue and allows the user to
 * upload more.
 * 
 * @author pchapman
 */
public class IssueAttachmentsPanel extends Panel<Issue>
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
			MarkupContainer parent, String id, Long issueID
		)
	{
		this(parent, id, new IssueModel(issueID));
	}

	/**
	 * @param parent The parent in which the panel belongs.
	 * @param id The id of the panel in markup.
	 * @param issue The issue for which attachments are to be displayed.
	 */
	public IssueAttachmentsPanel(
			MarkupContainer parent, String id, Issue issue
		)
	{
		this(parent, id, new IssueModel(issue));
	}

	/**
	 * @param parent The parent in which the panel belongs.
	 * @param id The id of the panel in markup.
	 * @param model A model that provides the issue for which attachments are
	 *              to be displayed.
	 */
	public IssueAttachmentsPanel(
			MarkupContainer parent, String id, IModel <Issue>model
		)
	{
		super(parent, id, model);
		new ListView<Attachment>(
				this, "attachmentsList", new IssueAttachmentsListModel(model)
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
                ExternalLink link = new ExternalLink(item, "downloadLink", url);
				
				ValueMap params = new ValueMap();
				params.add(
						BugeaterConstants.PARAM_NAME_ATTACHMENT_ID,
						att.getId().toString()
					);
				new Label(link, "attachmentLabel", att.getFileName());
			}
		};
		new AddAttachmentForm(this, "addForm");
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

		public AddAttachmentForm(MarkupContainer parent, String id)
		{
			super(parent, id);
			uploadField = new FileUploadField(this, "attachmentFile");
		}
		
		private FileUploadField uploadField;
		
		public void onSubmit()
		{
			FileUpload upload = uploadField.getFileUpload();
			if (upload != null) {
				Attachment a =
					new Attachment()
					.setIssue(IssueAttachmentsPanel.this.getModelObject())
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
}
