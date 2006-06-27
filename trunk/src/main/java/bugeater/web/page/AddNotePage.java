package bugeater.web.page;

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

import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.Session;
import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.spring.injection.SpringBean;
import wicket.util.string.StringValueConversionException;

/**
 * Allows the user to create a new note either as a stand-alone note, or as
 * part of the issue status change process.
 * 
 * @author pchapman
 */
@AuthorizeInstantiation({SecurityRole.USER})
public class AddNotePage extends BugeaterPage<Issue>
{
	private static final long serialVersionUID = 1L;
	
	public AddNotePage(Long issueID)
	{
		this(new IssueModel(issueID));
	}

	public AddNotePage(Issue arg0)
	{
		this(new IssueModel(arg0));
	}

	public AddNotePage(IModel<Issue> arg0)
	{
		this(arg0, null);
	}
	
	public AddNotePage(Long issueID, IssueStatus newStatus)
	{
		this(new IssueModel(issueID), newStatus);
	}

	public AddNotePage(Issue arg0, IssueStatus newStatus)
	{
		this(new IssueModel(arg0), newStatus);
	}

	public AddNotePage(IModel<Issue> arg0, IssueStatus newStatus)
	{
		super();
		init(arg0, newStatus);
	}

	@SuppressWarnings("unchecked")
	public AddNotePage(PageParameters params)
	{
		super(params);
		if (params.containsKey(BugeaterConstants.PARAM_NAME_ISSUE_ID)) {
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
				
				init(new IssueModel(issueid), newStatus);
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
	
	private void init(IModel<Issue> model, IssueStatus newStatus)
	{
		setModel(model);
		this.newStatus = newStatus;
		new AddNoteForm(this, "addNoteForm");
	}
	
	class AddNoteForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		AddNoteForm(MarkupContainer container, String id)
		{
			super(container, id);
			new FeedbackPanel(this, "formFeedback");
			textModel = new Model<String>();
			new TextArea<String>(this, "text", textModel).setRequired(true);
		}
		
		private IModel<String>textModel;
		
		public void onSubmit()
		{
			BugeaterSession sess = (BugeaterSession)Session.get();
			IUserBean userBean = sess.getUserBean();
			if (newStatus == null) {
				// Create a plain ordinary note
				Note note =
					new Note()
					.setIssue(AddNotePage.this.getModelObject())
					.setText(textModel.getObject())
					.setUserID(userBean.getId());
				noteService.save(note);
			} else {
				// Change status with the given note text
				issueService.changeStatus(
						AddNotePage.this.getModelObject(), userBean,
						newStatus, textModel.getObject()
					);
			}
			setResponsePage(new ViewIssuePage(AddNotePage.this.getModel()));
		}
	}
}
