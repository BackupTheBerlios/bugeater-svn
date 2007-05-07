package bugeater.web.model;

import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.engine.context.BaseRenderContext;

import wicket.model.IModel;
import wicket.model.Model;

/**
 * A model that gets String input from the nested model and passes it though
 * Radeox to convert the input from wiki text to xhtml markup.
 * 
 * @author pchapman
 */
public class RadeoxModel implements IModel<String>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance. 
	 */
	public RadeoxModel(IModel<String> nestedModel)
	{
		super();
		this.nestedModel = nestedModel;
	}

	/**
	 * Creates a model from the object's toString() method.
	 * 
	 * @param object
	 */
	public RadeoxModel(Object object)
	{
		this(new Model<String>(object == null ? "" : object.toString()));
	}
	
	private transient RenderContext context;
	private transient RenderEngine engine;
	private IModel<String>nestedModel;

	/**
	 * @see wicket.model.IDetachable#detach()
	 */
	public void detach()
	{
		nestedModel.detach();
	}

	/**
	 * @see wicket.model.IModel#getObject()
	 */
	public String getObject()
	{
		if (context == null) {
			context = new BaseRenderContext();
		    engine = new BaseRenderEngine();
		}
	    return engine.render(nestedModel.getObject(), context);
	}

	/**
	 * @see wicket.model.IModel#setObject(T)
	 */
	public void setObject(String object) {}
}
