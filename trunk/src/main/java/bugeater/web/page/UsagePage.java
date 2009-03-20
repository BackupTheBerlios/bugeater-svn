package bugeater.web.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * A page used to show information about how to use bugeater.  The content is
 * largely static.  However, wrapping it in a simple wicket page allows it to
 * receive the formatting of all other pages as well as easy
 * internationalization.
 * 
 * @author pchapman
 */
public class UsagePage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	
	public UsagePage(PageParameters params)
	{
		this();
	}

	public UsagePage() {
		super();
		add(new BookmarkablePageLink<Void>("formattinglink", NoteFormattingPage.class));
	}
}
