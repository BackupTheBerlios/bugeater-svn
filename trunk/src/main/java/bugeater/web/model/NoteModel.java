package bugeater.web.model;

import bugeater.domain.Note;

import bugeater.service.NoteService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.AbstractDetachableModel;

/**
 * A model used to provide an Note object to the component.
 * 
 * @author pchapman
 */
public class NoteModel extends AbstractDetachableModel<Note>
{
	private static final long serialVersionUID = 1L;
	
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 * @param noteID The unique ID of the Note.
	 */
	public NoteModel(Long noteID)
	{
		super();
		this.note = null;
		this.noteid = noteID;
		onAttach();
	}
	
	/**
	 * Creates a new instance.
	 * @param note The Note.
	 */
	public NoteModel(Note note)
	{
		super();
		setObject(note);
	}

	// MEMBERS
	
	private Note note;
	private Long noteid;

	// METHODS
	
	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (note == null && noteid != null) {
			NoteService iService =
				(NoteService)((BugeaterApplication)Application.get()).getSpringBean("noteService");
			note = iService.load(noteid);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		note = null;		
	}
	
	

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected Note onGetObject()
	{
		return note;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(<T>)
	 */
	@Override
	protected void onSetObject(Note note) 
	{
		this.noteid = note == null ? null : note.getId();
		this.note = note;
	}	
}
