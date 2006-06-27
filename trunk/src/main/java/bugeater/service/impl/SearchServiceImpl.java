package bugeater.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import bugeater.dao.IssueDao;
import bugeater.dao.NoteDao;
import bugeater.domain.Issue;
import bugeater.domain.Note;
import bugeater.service.ISearchResult;
import bugeater.service.SearchService;

/**
 * An implementation of the bugeater.service.SearchService
 * interface which uses lucene.
 * 
 * @author pchapman
 */
public class SearchServiceImpl implements SearchService
{
	private static final Log logger =
		LogFactory.getLog(SearchServiceImpl.class);
	
	/**
	 * Creates a new instance.
	 */
	public SearchServiceImpl()
	{
		super();
	}

	/**
	 * The directory in which indices are stored.
	 * @param path
	 */
	/* Spring injected */
	public void setIndexDirectory(String path)
	{
		try {
			writer = new IndexWriter(path, new SimpleAnalyzer(), true);
			searcher = new IndexSearcher(path);
		} catch (IOException ioe) {
			logger.error(ioe);
		}
	}
	
	/* Spring injected */
	private IssueDao issueDao;
	public void setIssueDao(IssueDao dao)
	{
		this.issueDao = dao;
	}
	private QueryParser issueParser;
	/* Spring injected */
	private NoteDao noteDao;
	public void setNoteDao(NoteDao dao)
	{
		this.noteDao = dao;
	}
	private QueryParser noteParser;
	private Searcher searcher;
	private IndexWriter writer;

	/**
	 * @see bugeater.service.SearchService#searchByIssueSummary(java.lang.String)
	 */
	public List<ISearchResult<Issue>> searchByIssueSummary(String queryText)
	{
		try {
			if (issueParser == null) {
				issueParser = new QueryParser("summary", new SimpleAnalyzer());
			}
			Query q = issueParser.parse(queryText);
			Hits hits = searcher.search(q);
			Hit hit;
			List<ISearchResult<Issue>> list =
				new ArrayList<ISearchResult<Issue>>();
			for (Iterator iter = hits.iterator() ; iter.hasNext() ;) {
				hit = (Hit)iter.next();
				list.add(new IssueSearchResult(hit).setIssueDao(issueDao));
			}
			return list;
		} catch (IOException ioe) {
			logger.error(ioe);
		} catch (ParseException pe) {
			logger.error(pe);
		}
		
		return null;
	}

	/**
	 * @see bugeater.service.SearchService#searchByNoteText(java.lang.String)
	 */
	public List<ISearchResult<Note>> searchByNoteText(String queryText)
	{
		try {
			if (noteParser == null) {
				noteParser = new QueryParser("text", new SimpleAnalyzer());
			}
			Query q = noteParser.parse(queryText);
			Hits hits = searcher.search(q);
			Hit hit;
			List<ISearchResult<Note>> list =
				new ArrayList<ISearchResult<Note>>();
			for (Iterator iter = hits.iterator() ; iter.hasNext() ;) {
				hit = (Hit)iter.next();
				list.add(new NoteSearchResult(hit).setNoteDao(noteDao));
			}
			return list;
		} catch (IOException ioe) {
			logger.error(ioe);
		} catch (ParseException pe) {
			logger.error(pe);
		}
		
		return null;
	}

	/**
	 * @see bugeater.service.SearchService#indexIssue(bugeater.domain.Issue)
	 */
	public void indexIssue(Issue issue)
	{
		Document doc = new Document();
		if (issue.getId() != null) {
			doc.add(
					new Field(
							"type", Issue.class.getName(),
							Field.Store.YES, Field.Index.NO
						)
				);
			doc.add(
					new Field(
							"id", issue.getId().toString(),
							Field.Store.YES, Field.Index.NO
						)
				);
			doc.add(
					new Field(
							"summary", issue.getSummary(),
							Field.Store.NO, Field.Index.TOKENIZED
						)
				);
			try {
				writer.addDocument(doc);
			} catch (IOException ioe) {
				logger.error(ioe);
			}
		}
	}

	/**
	 * @see bugeater.service.SearchService#indexNote(bugeater.domain.Note)
	 */
	public void indexNote(Note note)
	{
		Document doc = new Document();
		if (note.getId() != null) {
			doc.add(
					new Field(
							"type", Note.class.getName(),
							Field.Store.YES, Field.Index.NO
						)
				);
			doc.add(
					new Field(
							"id", note.getId().toString(),
							Field.Store.YES, Field.Index.NO
						)
				);
			doc.add(
					new Field(
							"text", note.getText(),
							Field.Store.NO, Field.Index.TOKENIZED
						)
				);
			try {
				writer.addDocument(doc);
			} catch (IOException ioe) {
				logger.error(ioe);
			}
		}
	}

}
