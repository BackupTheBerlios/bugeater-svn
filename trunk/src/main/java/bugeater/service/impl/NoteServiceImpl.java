package bugeater.service.impl;

import bugeater.dao.NoteDao;

import bugeater.domain.Note;

import bugeater.service.MailService;
import bugeater.service.NoteService;
import bugeater.service.SearchService;
import bugeater.service.UserService;

/**
 * An impelementation of the bugeater.service.NoteService
 * interface.
 * 
 * @author pchapman
 */
public class NoteServiceImpl implements NoteService
{
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 */
	public NoteServiceImpl()
	{
		super();
	}
	
	// MEMBERS
	
	// Injected by spring
	private MailService mailService;
	public void setMailService(MailService service)
	{
		this.mailService = service;
	}
	
	// Injected by spring
	private NoteDao noteDao;
	public void setNoteDao(NoteDao dao)
	{
		this.noteDao = dao;
	}
	
	// Injected by spring
	private SearchService searchService;
	public void setSearchService(SearchService service)
	{
		this.searchService = service;
	}
	
	// Injected by spring
	private UserService userService;
	public void setUserService(UserService service)
	{
		this.userService = service;
	}
	
	// METHODS
	
	/**
	 * @see bugeater.service.NoteService#load(String)
	 */
	public Note load(Long id)
	{
		return noteDao.load(id);
	}
	
	/**
	 * @see bugeater.service.NoteService#save(bugeater.domain.User)
	 */
	public void save(Note n)
	{
		boolean isnew = n.getId() == null;
		noteDao.save(n);
		searchService.indexNote(n);
		if (isnew && n.getId() != null) {
			EmailHelper.emailNotePosted(mailService, userService, n);
		}
	}
}
