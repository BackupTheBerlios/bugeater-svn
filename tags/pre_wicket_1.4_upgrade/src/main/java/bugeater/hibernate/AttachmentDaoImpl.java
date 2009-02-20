package bugeater.hibernate;

import java.util.List;

import bugeater.dao.AttachmentDao;
import bugeater.domain.Attachment;
import bugeater.domain.Issue;

/**
 * An implementation of the bugeater.dao.AttachmentDao interface.
 * 
 * @author pchapman
 */
public class AttachmentDaoImpl extends AbstractHibernateDao<Attachment>
	implements AttachmentDao
{
	/**
	 * Creates a new instance.
	 */
	public AttachmentDaoImpl()
	{
		super(Attachment.class);
	}

	/**
	 * gets all attachments associated with an issue.
	 */
	@SuppressWarnings("unchecked")
	public List<Attachment>getAttachments(Issue i)
	{
		return (List<Attachment>)getSession().createQuery(
				"select att from Attachment att where att.issue = :issue order by att.fileName"
			).setEntity("issue", i).list();
	}
}
