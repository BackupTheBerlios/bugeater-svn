package bugeater.web.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A link which will get a confirmation from the user before it is followed.
 * 
 * @author pchapman
 */
public abstract class ConfirmLink<T> extends Link<T>
{
	/**
	 * Creates a new instance.
	 * @param parent
	 * @param id
	 */
	public ConfirmLink(String id)
	{
		super(id);
		addOnClick();
	}

	/**
	 * @param parent
	 * @param id
	 * @param object
	 */
	public ConfirmLink(String id, IModel<T> object)
	{
		super(id, object);
		addOnClick();
	}

	public abstract String getConfirmationMessage();
	
	private void addOnClick()
	{  
		StringBuilder sb = new StringBuilder("javascript:return confirm('");
		sb.append(getConfirmationMessage().replace("'", "\'"));
		sb.append("');");
		add(
				new AttributeModifier(
						"onClick", true, new Model<String>(sb.toString())  
					)
			);
	} 
}
