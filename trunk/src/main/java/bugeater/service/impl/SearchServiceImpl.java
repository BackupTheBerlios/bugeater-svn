package bugeater.service.impl;

import java.io.File;
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
	private String indexDir;
	public void setIndexDirectory(String path)
	{
		this.indexDir = path;
//		try {
//			writer = new IndexWriter(path, new SimpleAnalyzer(), true);
//			searcher = new IndexSearcher(path);
//		} catch (IOException ioe) {
//			logger.error(ioe);
//		}
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
	
	private Searcher getSearcher()
		throws IOException
	{
		Searcher searcher = new IndexSearcher(indexDir);
		return searcher;
	}
	
	private IndexWriter getWriter()
		throws IOException
	{
		boolean createNew = false;
		File testfile = new File(new File(indexDir), "initialized");
		if (! testfile.exists()) {
			testfile.createNewFile();
			createNew = true;
		}
		IndexWriter writer =
			new IndexWriter(indexDir, new SimpleAnalyzer(), createNew);
		return writer;
	}
	
	private QueryParser noteParser;
//	private Searcher searcher;
//	private IndexWriter writer;

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
			Searcher searcher = getSearcher();
			Hits hits = searcher.search(q);
			Hit hit;
			List<ISearchResult<Issue>> list =
				new ArrayList<ISearchResult<Issue>>();
			for (Iterator iter = hits.iterator() ; iter.hasNext() ;) {
				hit = (Hit)iter.next();
				list.add(new IssueSearchResult(hit).setIssueDao(issueDao));
			}
			searcher.close();
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
			Searcher searcher = getSearcher();
			Hits hits = searcher.search(q);
			Hit hit;
			List<ISearchResult<Note>> list =
				new ArrayList<ISearchResult<Note>>();
			for (Iterator iter = hits.iterator() ; iter.hasNext() ;) {
				hit = (Hit)iter.next();
				list.add(new NoteSearchResult(hit).setNoteDao(noteDao));
			}
			searcher.close();
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
				index(doc);
			} catch (IOException ioe) {
				logger.error(ioe);
			}
		}
	}
	
	private void index(Document doc)
		throws IOException
	{
		IndexWriter writer = getWriter();
		writer.addDocument(doc);
		writer.optimize();
		writer.close();
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
				index(doc);
			} catch (IOException ioe) {
				logger.error(ioe);
			}
		}
	}

}
