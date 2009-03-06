package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;

public class AssignableStatusesModel implements IModel<List<IssueStatus>>
{
	private static final long serialVersionUID = 1L;
	
	AssignableStatusesModel(IModel<Issue>issueModel)
	{
		this.issueModel = issueModel;
	}
	
	private IModel<Issue>issueModel;
	private transient List<IssueStatus> list;
	
	public void detach()
	{
		issueModel.detach();
	}

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
	
	public List<IssueStatus> getObject()
	{
		if (list == null) {
			list = load();
		}
		return list;
	}
	
	public void setObject(List<IssueStatus> list) {}
}
