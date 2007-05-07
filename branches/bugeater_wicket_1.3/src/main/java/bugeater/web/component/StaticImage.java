package bugeater.web.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Component for html img tags with static content. The model represents the
 * path to the static image.
 * 
 * This component will change its tag name to img so that it could be attach to
 * span tags.
 * 
 * @author igor
 * @author pchapman
 */
public class StaticImage extends WebComponent
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	// CONSTRUCTORS
		
	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param path path to image
	 */
	public StaticImage(String id, String path)
	{
		super(id, new Model(path));
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param path path to image
	 * @param altText the alternate text
	 */
	public StaticImage(String id, String path, String altText)
	{
		this(id, new Model(path), new Model(altText));
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param model model containing the path to the image
	 */
	public StaticImage(String id, IModel model)
	{
		super(id, model);
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param srcModel model containing the path to the image
	 * @param altModel model containing the alt text
	 */
	public StaticImage(String id, IModel srcModel, IModel altModel)
	{
		super(id, srcModel);
		this.alt = altModel;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public StaticImage(String id)
	{
		super(id);
	}
	
	// MEMBERS
	
	private IModel alt;
	/**
	 * Returns the alternate text for the image.
	 */
	public IModel getAlt()
	{
		return alt;
	}

	/**
	 * Sets the model for the alt attribute value
	 * 
	 * @param model
	 * @return this for chaining
	 */
	public StaticImage setAlt(IModel model)
	{
		this.alt = model;
		return this;
	}
	
	// METHODS

	/**
	 * @see wicket.Component#onComponentTag(ComponentTag)
	 */
	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);

		tag.setName("img");
		tag.getAttributes().put("src", getModelObjectAsString());

		// Guard against both a null alt model and a null value in the model.
		String altText = null;
		if (alt != null) {
			Object s = alt.getObject();
			if (s != null) {
				altText = s.toString();
			}
		}
		if (altText != null) {
			tag.getAttributes().put("alt", altText);
		}
	}
}
