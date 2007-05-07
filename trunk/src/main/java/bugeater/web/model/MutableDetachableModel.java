package bugeater.web.model;

import wicket.model.IDetachable;
import wicket.model.IModel;

/**
 * @author pchapman
 */
public abstract class MutableDetachableModel<T> extends Object
	implements IModel<T>, IDetachable
{
	/** keeps track of whether this model is attached or detached */
	private transient boolean attached = false;

	/** temporary, transient object. */
	private transient T transientModelObject;

	/**
	 * Construct.
	 */
	public MutableDetachableModel()
	{
	}

	/**
	 * This constructor is used if you already have the object retrieved and
	 * want to wrap it with a detachable model.
	 * 
	 * @param object
	 *            retrieved instance of the detachable object
	 */
	public MutableDetachableModel(T object)
	{
		this.transientModelObject = object;
		attached = true;
	}

	/**
	 * @see wicket.model.IDetachable#detach()
	 * 
	 * IMPORTANT: overrides must call super
	 */
	public void detach()
	{
		if (attached)
		{
			attached = false;
			transientModelObject = null;
		}
	}

	/**
	 * @see wicket.model.IModel#getObject()
	 */
	public final T getObject()
	{
		if (!attached)
		{
			attached = true;
			transientModelObject = load();
		}
		return transientModelObject;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Model:classname=[").append(getClass().getName()).append("]");
		sb.append(":attached=").append(attached).append(":tempModelObject=[").append(
				this.transientModelObject).append("]");
		return sb.toString();
	}

	/**
	 * Loads and returns the (temporary) model object.
	 * 
	 * @return the (temporary) model object
	 */
	protected abstract T load();

	/**
	 * @see wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(T object)
	{
		attached = true;
		transientModelObject = object;
	}
}
