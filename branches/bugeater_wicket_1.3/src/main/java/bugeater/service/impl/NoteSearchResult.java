package bugeater.service.impl;

import org.apache.lucene.search.Hit;
import bugeater.dao.NoteDao;
import bugeater.domain.Note;

/**
 * A search result that wraps a note object that matches a search's parameters.
 * 
 * @author pchapman
 */
public class NoteSearchResult extends AbstractSearchResult<Note>
{
	/**
	 * @param hit The hit from lucene.
	 */
	public NoteSearchResult(Hit hit)
	{
		super(hit);
	}
	
	private NoteDao noteDao;
	/**
	 * The dao used to retrieve notes.
	 */
	public NoteSearchResult setNoteDao(NoteDao dao)
	{
		this.noteDao = dao;
		return this;
	}

	/**
	 * @see bugeater.service.impl.AbstractSearchResult#getMatchText()
	 */
	@Override
	public String getMatchText()
	{
		return getObject().getText();
	}

	/**
	 * @see bugeater.service.impl.AbstractSearchResult#getObject()
	 */
	@Override
	public Note getObject()
	{
		return noteDao.load(getObjectId());
	}
}
