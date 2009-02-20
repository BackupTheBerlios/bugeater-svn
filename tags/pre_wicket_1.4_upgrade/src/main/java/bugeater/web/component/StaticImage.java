package bugeater.web.component;

import wicket.MarkupContainer;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebComponent;
import wicket.model.IModel;
import wicket.model.Model;

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
public class StaticImage extends WebComponent<String>
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
	public StaticImage(MarkupContainer container, String id, String path)
	{
		super(container, id, new Model<String>(path));
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param path path to image
	 * @param altText the alternate text
	 */
	public StaticImage(
			MarkupContainer container, String id, String path, String altText
		)
	{
		this(container, id, new Model<String>(path), new Model<String>(altText));
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param model model containing the path to the image
	 */
	public StaticImage(MarkupContainer container, String id, IModel<String> model)
	{
		super(container, id, model);
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param srcModel model containing the path to the image
	 * @param altModel model containing the alt text
	 */
	public StaticImage(
			MarkupContainer container, String id,
			IModel<String> srcModel, IModel<String> altModel
		)
	{
		super(container, id, srcModel);
		this.alt = altModel;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public StaticImage(MarkupContainer container, String id)
	{
		super(container, id);
	}
	
	// MEMBERS
	
	private IModel<String> alt;
	/**
	 * Returns the alternate text for the image.
	 */
	public IModel<String> getAlt()
	{
		return alt;
	}

	/**
	 * Sets the model for the alt attribute value
	 * 
	 * @param model
	 * @return this for chaining
	 */
	public StaticImage setAlt(IModel<String> model)
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
			String s = alt.getObject();
			if (s != null) {
				altText = s;
			}
		}
		if (altText != null) {
			tag.getAttributes().put("alt", altText);
		}
	}
}
