package bugeater.web.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;

import wicket.Application;

import bugeater.domain.Issue;
import bugeater.domain.Note;
import bugeater.service.ISearchResult;
import bugeater.service.IssueService;
import bugeater.service.SearchService;
import bugeater.web.BugeaterApplication;

/**
 * A model which will provide a list of issues based on text search criteria.
 */
public class TextSearchModel extends AbstractDetachableEntityListModel<Issue>
{
	private static final long serialVersionUID = 1L;
	
	public TextSearchModel(String text)
	{
		super();
		// The search is performed once in this constructor.  The unique IDs of
		// the resultant issues are retained.
		SearchService service =
			(SearchService)((BugeaterApplication)Application.get()).getSpringBean("searchService");
		Set <Long>iSet = new HashSet<Long>();
		List<ISearchResult<Issue>>iResults = service.searchByIssueSummary(text);
		for (ISearchResult<Issue> result : iResults) {
			try {
				iSet.add(result.getObjectId());
			} catch (ObjectNotFoundException onfe) {
				// If the issue was somehow deleted out of the system
				// without updating the text search module, ignore
			}
		}
		iResults = null;
		List<ISearchResult<Note>>nResults = service.searchByNoteText(text);
		for (ISearchResult<Note> result : nResults) {
			try {
				iSet.add(result.getObject().getIssue().getId());
			} catch (ObjectNotFoundException onfe) {
				// If the note was somehow deleted out of the system
				// without updating the text search module, ignore
			}
		}
		nResults = null;
		ids = new ArrayList<Long>(iSet);
	}
	
	private List<Long> ids;

	@Override
	protected List<Issue> load()
	{
		List<Issue>list = new ArrayList<Issue>();
		IssueService service =
			(IssueService)((BugeaterApplication)Application.get()).getSpringBean("issueService");
		list = new ArrayList<Issue>();
		if (ids != null) {
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
		return list;
	}
}
