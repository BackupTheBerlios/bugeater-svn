package bugeater.web.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

/**
 * Returns reasonable text for a null value.
 * 
 * @author pchapman
 */
public class NullableChoiceRenderer extends ChoiceRenderer
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see wicket.markup.html.form.ChoiceRenderer#getDisplayValue(T)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getDisplayValue(Object object)
	{
		if (object == null) {
			return "(Not Assigned)";
		} else {
			return super.getDisplayValue(object);
		}
	}
}
