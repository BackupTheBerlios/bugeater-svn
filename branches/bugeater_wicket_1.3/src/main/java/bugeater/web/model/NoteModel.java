package bugeater.web.model;

import bugeater.domain.Note;

import bugeater.service.NoteService;
import bugeater.web.BugeaterApplication;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;

/**
 * A model used to provide a Note object to the component.
 * 
 * @author pchapman
 */
public class NoteModel implements IModel
{
	private static final long serialVersionUID = 1L;
	
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 * @param noteID The unique ID of the note.
	 */
	public NoteModel(Long noteID)
	{
		super();
		this.noteid = noteID;
	}
	
	/**
	 * Creates a new instance.
	 * @param note The note.
	 */
	public NoteModel(Note note)
	{
		super();
		setObject(note);
	}

	// MEMBERS
	
	private Long noteid;
	private transient Note note;

	/**
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach()
	{
		note = null;
	}

	/**
	 * @see org.apache.wicket.model.IModel#getObject()
	 */
	public Object getObject()
	{
		if (note == null && noteid != null) {
			NoteService iService =
				(NoteService)((BugeaterApplication)Application.get()).getSpringBean("noteService");
			note = iService.load(noteid); 
		}
		return note;
	}

	/**
	 * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Object object)
	{
		if (object == null) {
			note = null;
			noteid = null;
		} else if (object instanceof Note) {
			note = (Note)object;
			noteid = note.getId();
		}
	}
}
