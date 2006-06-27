package bugeater.service;

import org.springframework.transaction.annotation.Transactional;

import bugeater.domain.Note;

/**
 * An interface that defines the API that a service class that deals with
 * manipulating a Note must implement.
 * 
 * @author pchapman
 */
public interface NoteService
{
	/**
	 * Loads the note class instance by unique ID.
	 */
	public Note load(Long id);
	
	/**
	 * Saves changes to the note record.
	 */
	@Transactional
	public void save(Note n);
}
