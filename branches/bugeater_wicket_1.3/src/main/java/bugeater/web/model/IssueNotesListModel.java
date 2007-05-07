package bugeater.web.model;

import java.util.List;

import bugeater.domain.Note;
import bugeater.domain.Issue;

import wicket.model.IModel;

/**
 * Provides a list of all notes associated with an issue.
 * 
 * @author pchapman
 */
public class IssueNotesListModel extends AbstractDetachableEntityListModel<Note>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance. 
	 */
	public IssueNotesListModel(IModel<Issue> issueModel)
	{
		super();
		this.issueModel = issueModel;
	}
	
	private IModel<Issue>issueModel;

	@Override
	protected List<Note> load()
	{
		return issueModel.getObject().getNotes();
	}
}
