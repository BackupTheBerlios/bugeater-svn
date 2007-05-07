package bugeater.dao;

import bugeater.domain.Note;

/**
 * An interface that defines data access methods for Note objects.
 * 
 * @author pchapman
 */
public interface NoteDao
{
	/**
	 * Loads a specific Note by ID.
	 */
	public Note load(Long id);

	/**
	 * Saves changes to the issue.
	 */
	public void save(Note issue);
}
