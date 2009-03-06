package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import bugeater.domain.Issue;
import bugeater.domain.Note;

/**
 * Provides a list of all notes associated with an issue.
 * 
 * @author pchapman
 */
public class IssueNotesListModel implements IModel<List<Note>>
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
	private transient List<Note> notes;
	
	public void detach()
	{
		issueModel.detach();
	}

	public List<Note> getObject()
	{
		if (notes == null) {
			// Draw the notes out of the list since issue notes is a lazily
			// loaded list.  If we where to wait, an attempt to get at the
			// notes may happen after the hibernate session is closed.
			List<Note> list = issueModel.getObject().getNotes();
			notes = new ArrayList<Note>(list.size());
			notes.addAll(list);
		}
		return notes;
	}
	
	public void setObject(List<Note> note) {}
}
