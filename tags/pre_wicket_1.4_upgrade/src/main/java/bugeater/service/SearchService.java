package bugeater.service;

import java.util.List;

import bugeater.domain.Issue;
import bugeater.domain.Note;

/**
 * A service that provides text searching through issue summaries and notes'
 * text.
 * 
 * @author pchapman
 */
public interface SearchService
{
	/**
	 * Deletes the indicies for the indicated item from within the search
	 * system.
	 * @param result
	 */
	public void deleteIndexes(ISearchResult result);
	
	/**
	 * Searches for matches in issue summaries.
	 * @param queryText The text to search for.
	 * @return Matches
	 */
	public List<ISearchResult<Issue>> searchByIssueSummary(String queryText);
	
	/**
	 * Searches for matches in notes' text.
	 * @param queryText The text to search for.
	 * @return Matches
	 */
	public List<ISearchResult<Note>> searchByNoteText(String queryText);
	
	/**
	 * Indexes the summary of the issue for searching.
	 */
	public void indexIssue(Issue issue);
	
	/**
	 * Indexes the text of the 
	 * @param note
	 */
	public void indexNote(Note note);
}
