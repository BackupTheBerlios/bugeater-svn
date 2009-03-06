package bugeater.web.model;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;

import bugeater.domain.Note;
import bugeater.service.NoteService;
import bugeater.web.BugeaterApplication;

/**
 * A model used to provide an Note object to the component.
 * 
 * @author pchapman
 */
public class NoteModel implements IModel<Note>
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

	public void detach()
	{
		note = null;
	}

	protected Note load()
	{
		if (noteid == null) {
			NoteService iService =
				(NoteService)((BugeaterApplication)Application.get()).getSpringBean("noteService");
			return iService.load(noteid);
		} else {
			return null;
		}
	}
	
	public Note getObject()
	{
		if (note == null && noteid != null) {
			note = load();
		}
		return note;
	}
	
	public void setObject(Note note)
	{
		this.note = note;
		noteid = note == null ? null : note.getId();
	}
}
