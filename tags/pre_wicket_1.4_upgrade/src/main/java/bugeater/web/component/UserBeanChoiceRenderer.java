package bugeater.web.component;

import bugeater.bean.IUserBean;
import wicket.markup.html.form.IChoiceRenderer;

/**
 * A choice renderer that renders an IUserBean as the user's full name.
 * 
 * @author pchapman
 */
public class UserBeanChoiceRenderer implements IChoiceRenderer<IUserBean>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see wicket.markup.html.form.IChoiceRenderer#getDisplayValue(java.lang.Object)
	 */
	public Object getDisplayValue(IUserBean object)
	{
		if (object == null) {
			return null;
		}
		return object.getFullname();
	}

	/**
	 * @see wicket.markup.html.form.IChoiceRenderer#getIdValue(java.lang.Object, int)
	 */
	public String getIdValue(IUserBean object, int index)
	{
		if (object == null) {
			return null;
		}
		return object.getId();
	}
}
