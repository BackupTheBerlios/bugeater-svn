package bugeater.hibernate;

import bugeater.dao.NoteDao;

import bugeater.domain.Note;

/**
 * An implementation of the bugeater.dao.NoteDao interface.
 * 
 * @author pchapman
 */
public class NoteDaoImpl extends AbstractHibernateDao<Note> implements NoteDao
{
	/**
	 * @param dataClass
	 */
	public NoteDaoImpl()
	{
		super(Note.class);
	}
}
