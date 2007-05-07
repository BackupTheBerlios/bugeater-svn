package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;

public class AssignableStatusesModel
	extends AbstractDetachableEntityListModel<IssueStatus>
{
	private static final long serialVersionUID = 1L;
	
	AssignableStatusesModel(IModel issueModel)
	{
		this.issueModel = issueModel;
	}
	
	private IModel issueModel;
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
			((Issue)issueModel.getObject()).getCurrentStatus();
		List<IssueStatus>list = new ArrayList<IssueStatus>(IssueStatus.values().length);
		for (IssueStatus status : IssueStatus.values()) {
			if (status != currentStatus) {
				list.add(status);
			}
		}
		return list;
	}
}
