package bugeater.web.model;

import bugeater.domain.Note;

import bugeater.service.NoteService;
import bugeater.web.BugeaterApplication;

import wicket.Application;

/**
 * A model used to provide an Note object to the component.
 * 
 * @author pchapman
 */
public class NoteModel extends MutableDetachableModel<Note>
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
		super(note);
		this.noteid = note == null ? null : note.getId();
	}

	// MEMBERS
	
	private Long noteid;

	/**
	 * @see bugeater.web.model.MutableDetachableModel#detach()
	 */
	@Override
	public void detach()
	{
		noteid = getObject().getId();
		super.detach();
	}

	/**
	 * @see bugeater.web.model.MutableDetachableModel#load()
	 */
	@Override
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
}
