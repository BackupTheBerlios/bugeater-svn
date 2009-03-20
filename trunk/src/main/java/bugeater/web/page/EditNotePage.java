package bugeater.web.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

import bugeater.bean.IUserBean;
import bugeater.domain.Issue;
import bugeater.domain.IssueStatus;
import bugeater.domain.Note;
import bugeater.service.IssueService;
import bugeater.service.NoteService;
import bugeater.service.SecurityRole;
import bugeater.web.BugeaterConstants;
import bugeater.web.BugeaterSession;
import bugeater.web.model.IssueModel;
import bugeater.web.model.NoteModel;

/**
 * Allows the user to create a new note either as a stand-alone note, or as
 * part of the issue status change process.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({
	SecurityRole.ADMINISTRATOR, SecurityRole.DEVELOPER,
	SecurityRole.MANAGER, SecurityRole.TESTER
})
public class EditNotePage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	public EditNotePage(Long issueID, Long noteId)
	{
		this(new IssueModel(issueID), new NoteModel(noteId));
	}

	public EditNotePage(Issue arg0, Note arg1)
	{
		this(new IssueModel(arg0), new NoteModel(arg1));
	}

	public EditNotePage(IModel<Issue> arg0, IModel<Note> arg1)
	{
		init(arg0, arg1, null);
	}
	
	public EditNotePage(Long issueID)
	{
		this(new IssueModel(issueID));
	}

	public EditNotePage(Issue arg0)
	{
		this(new IssueModel(arg0));
	}

	public EditNotePage(IModel<Issue> arg0)
	{
		this(arg0, (IssueStatus)null);
	}
	
	public EditNotePage(Long issueID, IssueStatus newStatus)
	{
		this(new IssueModel(issueID), newStatus);
	}

	public EditNotePage(Issue arg0, IssueStatus newStatus)
	{
		this(new IssueModel(arg0), newStatus);
	}

	public EditNotePage(IModel<Issue> arg0, IssueStatus newStatus)
	{
		super();
		init(arg0, new NoteModel((Long)null), newStatus);
	}

	public EditNotePage(PageParameters params)
	{
		super(params);
		if (params.containsKey(BugeaterConstants.PARAM_NAME_NOTE_ID)) {
			try {
				Note note = noteService.load(params.getLong(BugeaterConstants.PARAM_NAME_NOTE_ID));
				init(new IssueModel(note.getIssue()), new NoteModel(note), null);
			} catch (StringValueConversionException svce) {
				throw new IllegalArgumentException(svce);
			}			
		} else if (params.containsKey(BugeaterConstants.PARAM_NAME_ISSUE_ID)) {
			try {
				Long issueid = params.getLong(BugeaterConstants.PARAM_NAME_ISSUE_ID);
				IssueStatus newStatus = null;
				if (params.containsKey(BugeaterConstants.PARAM_NAME_ISSUE_STATUS)) {
					try {
						newStatus =
							IssueStatus.fromOrdinal(
									params.getInt(
											BugeaterConstants.PARAM_NAME_ISSUE_STATUS
										)
								);
					} catch (StringValueConversionException svce) {
						throw new IllegalArgumentException(svce);
					}
				}
				
				init(new IssueModel(issueid), new NoteModel((Long)null), newStatus);
			} catch (StringValueConversionException svce) {
				logger.error(svce);
			}
		} else {
			setResponsePage(Home.class);
		}
	}
	
	@SpringBean
	private IssueService issueService;
	public void setIssueService(IssueService service)
	{
		this.issueService = service;
	}
	
	private IssueStatus newStatus;
	
	@SpringBean
	private NoteService noteService;
	public void setNoteService(NoteService service)
	{
		this.noteService = service;
	}
	
	private void init(
			IModel<Issue> model, IModel<Note> noteModel, IssueStatus newStatus
		)
	{
		setDefaultModel(model);
		issueModel = model;
		this.newStatus = newStatus;
		add(new AddNoteForm("addNoteForm", noteModel));
	}
	
	class AddNoteForm extends Form<Note>
	{
		private static final long serialVersionUID = 1L;
		
		AddNoteForm(String id, IModel<Note> noteModel)
		{
			super(id, noteModel);
			add(new FeedbackPanel("formFeedback"));
			textModel = new Model<String>();
			Note n = noteModel.getObject();
			if (n != null) {
				textModel.setObject(n.getText());
			}
			add(new TextArea<String>("text", textModel).setRequired(true));
			add(new BookmarkablePageLink<Void>("formattinglink", NoteFormattingPage.class));
		}
		
		private IModel<String>textModel;
		
		public void onSubmit()
		{
			Note note = getModelObject();
			if (note == null) {
				BugeaterSession sess = (BugeaterSession)Session.get();
				IUserBean userBean = sess.getUserBean();
				if (newStatus == null) {
					// Create a plain ordinary note
					note =
						new Note()
						.setIssue(issueModel.getObject())
						.setText(textModel.getObject())
						.setUserID(userBean.getId());
				} else {
					// Change status with the given note text
					note = issueService.changeStatus(
							issueModel.getObject(), userBean,
							newStatus, textModel.getObject()
						).getNote();
				}
			} else {
				note.setText(textModel.getObject());
			}
			noteService.save(note);
			PageParameters params = new PageParameters();
			params.add(
					BugeaterConstants.PARAM_NAME_ISSUE_ID,
					String.valueOf(note.getIssue().getId())
				);
			setResponsePage(ViewIssuePage.class, params);
		}
	}
	
	private IModel<Issue> issueModel;
}
