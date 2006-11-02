package bugeater.web.model;

import java.util.List;

import bugeater.domain.Note;
import bugeater.domain.Issue;

import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;

/**
 * Provides a list of all notes associated with an issue.
 * 
 * @author pchapman
 */
public class IssueNotesListModel extends AbstractDetachableModel<List<Note>>
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

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		// The nested model is attached by wicket
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		if (issueModel instanceof AbstractDetachableModel) {
			((AbstractDetachableModel)issueModel).detach();
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Note> onGetObject()
	{
		return issueModel.getObject().getNotes();
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Note> object)
	{
		// Not implemented
	}
}
