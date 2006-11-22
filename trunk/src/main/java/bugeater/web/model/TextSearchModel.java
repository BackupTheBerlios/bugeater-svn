package bugeater.web.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;

import wicket.Application;
import wicket.model.AbstractDetachableModel;

import bugeater.domain.Issue;
import bugeater.domain.Note;
import bugeater.service.ISearchResult;
import bugeater.service.IssueService;
import bugeater.service.SearchService;
import bugeater.web.BugeaterApplication;

/**
 * A model which will provide a list of issues based on text search criteria.
 */
public class TextSearchModel extends AbstractDetachableModel<List<Issue>>
{
	private static final long serialVersionUID = 1L;
	
	public TextSearchModel(String text)
	{
		super();
		SearchService service =
			(SearchService)((BugeaterApplication)Application.get()).getSpringBean("searchService");
		List<ISearchResult<Issue>>iResults = service.searchByIssueSummary(text);
		List<ISearchResult<Note>>nResults = service.searchByNoteText(text);
		Set <Long>iSet = new HashSet<Long>();
		for (ISearchResult<Issue> result : iResults) {
			try {
				iSet.add(result.getObjectId());
			} catch (ObjectNotFoundException onfe) {
				// If the issue was somehow deleted out of the system
				// without updating the text search module, delete the
				// issue from the text search module.
//				service.deleteIndexes(result);
			}
		}
		for (ISearchResult<Note> result : nResults) {
			try {
				iSet.add(result.getObject().getIssue().getId());
			} catch (ObjectNotFoundException onfe) {
				// If the note was somehow deleted out of the system
				// without updating the text search module, delete the
				// note from the text search module.
//				service.deleteIndexes(result);
			}
		}
		ids = new ArrayList<Long>(iSet);
	}
	
	private List<Long> ids;
	private List<Issue>list;

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			IssueService service =
				(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
			list = new ArrayList<Issue>();
			Long id;
			for (Iterator<Long> iter = ids.iterator(); iter.hasNext(); ) {
				id = iter.next();
				try {
					list.add(service.load(id));
				} catch (ObjectNotFoundException onfe) {
					// If the issue was somehow deleted out of the system
					// without updating the text search module, delete the
					// issue from the text search module.
					iter.remove();
				}
			}
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<Issue> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<Issue> object)
	{
		// not implemented
	}
}
