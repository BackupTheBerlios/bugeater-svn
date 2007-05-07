package bugeater.web.model;

import java.util.Collections;
import java.util.List;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;
import bugeater.service.AttachmentService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.IModel;

/**
 * A model that provides a list of attachments for an issue.
 * 
 * @author pchapman
 */
public class IssueAttachmentsListModel
	extends AbstractDetachableEntityListModel<Attachment>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance. 
	 */
	public IssueAttachmentsListModel(IModel<Issue> issueModel)
	{
		super();
		this.issueModel = issueModel;
	}
	
	private IModel<Issue>issueModel;
	protected IModel getNestModel()
	{
		return issueModel;
	}

	@Override
	protected List<Attachment> load()
	{
		List<Attachment> list = null;
		if (
				issueModel == null ||
				issueModel.getObject() == null
			)
		{
			list = Collections.emptyList();
		} else {
			AttachmentService service =
				(AttachmentService)((BugeaterApplication)Application.get()).getSpringBean("attachmentService");
			list = service.getAttachments(issueModel.getObject());
		}
		return list;
	}
}
