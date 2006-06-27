package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;

public class AssignableStatusesModel
	extends AbstractDetachableModel<List<IssueStatus>>
{
	private static final long serialVersionUID = 1L;
	
	AssignableStatusesModel(IModel<Issue>issueModel)
	{
		this.issueModel = issueModel;
	}
	
	private IModel<Issue>issueModel;
	private List<IssueStatus>list;

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
		// Wicket attaches nested model
		if (list == null) {
			IssueStatus currentStatus =
				issueModel.getObject().getCurrentStatus();
			list = new ArrayList<IssueStatus>(IssueStatus.values().length);
			for (IssueStatus status : IssueStatus.values()) {
				if (status != currentStatus) {
					list.add(status);
				}
			}
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
	protected List<IssueStatus> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<IssueStatus> object)
	{
		// not implemented
	}
}
