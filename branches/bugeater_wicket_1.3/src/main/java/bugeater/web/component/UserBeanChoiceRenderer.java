package bugeater.web.component;

import bugeater.bean.IUserBean;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * A choice renderer that renders an IUserBean as the user's full name.
 * 
 * @author pchapman
 */
public class UserBeanChoiceRenderer implements IChoiceRenderer
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see wicket.markup.html.form.IChoiceRenderer#getDisplayValue(java.lang.Object)
	 */
	public Object getDisplayValue(Object object)
	{
		if (object instanceof IUserBean) {
			return ((IUserBean)object).getFullname();
		} else {
			return null;
		}
	}

	/**
	 * @see wicket.markup.html.form.IChoiceRenderer#getIdValue(java.lang.Object, int)
	 */
	public String getIdValue(Object object, int index)
	{
		if (object instanceof IUserBean) {
			return ((IUserBean)object).getId();
		} else {
			return null;
		}
	}
}
