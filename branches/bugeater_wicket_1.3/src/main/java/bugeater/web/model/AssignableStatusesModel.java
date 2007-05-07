package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import wicket.model.IModel;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;

public class AssignableStatusesModel
	extends AbstractDetachableEntityListModel<IssueStatus>
{
	private static final long serialVersionUID = 1L;
	
	AssignableStatusesModel(IModel<Issue>issueModel)
	{
		this.issueModel = issueModel;
	}
	
	private IModel<Issue>issueModel;
	/**
	 * @see bugeater.web.model.AbstractDetachableEntityListModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return issueModel;
	}

	@Override
	protected List<IssueStatus> load()
	{
		IssueStatus currentStatus =
			issueModel.getObject().getCurrentStatus();
		List<IssueStatus>list = new ArrayList<IssueStatus>(IssueStatus.values().length);
		for (IssueStatus status : IssueStatus.values()) {
			if (status != currentStatus) {
				list.add(status);
			}
		}
		return list;
	}
}
