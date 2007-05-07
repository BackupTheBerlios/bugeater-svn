package bugeater.web.component;

import wicket.AttributeModifier;
import wicket.MarkupContainer;
import wicket.markup.html.link.Link;
import wicket.model.IModel;
import wicket.model.Model;

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
	public ConfirmLink(MarkupContainer parent, String id)
	{
		super(parent, id);
		addOnClick();
	}

	/**
	 * @param parent
	 * @param id
	 * @param object
	 */
	public ConfirmLink(MarkupContainer parent, String id, IModel<T> object)
	{
		super(parent, id, object);
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
