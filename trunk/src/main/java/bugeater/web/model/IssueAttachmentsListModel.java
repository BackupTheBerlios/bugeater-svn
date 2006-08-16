package bugeater.web.model;

import java.util.List;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;
import bugeater.service.AttachmentService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;

/**
 * A model that provides a list of attachments for an issue.
 * 
 * @author pchapman
 */
public class IssueAttachmentsListModel
	extends AbstractDetachableModel<List<Attachment>>
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
	private transient List<Attachment>list;

	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return issueModel;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (
				list == null &&
				issueModel != null &&
				issueModel.getObject() != null
			)
		{
			AttachmentService service =
				(AttachmentService)((BugeaterApplication)Application.get()).getSpringBean("attachmentService");
			list = service.getAttachments(issueModel.getObject());
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Attachment> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Attachment> object)
	{
		// Not implemented
	}
}
