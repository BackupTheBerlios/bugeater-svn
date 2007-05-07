package bugeater.web.model;

import java.util.List;

import wicket.model.IModel;
import wicket.model.LoadableDetachableModel;

public abstract class AbstractDetachableEntityListModel<T> extends
		LoadableDetachableModel<List<T>>
{
	public AbstractDetachableEntityListModel()
	{
		super();
	}

	/**
	 * Gets a nested model for detaching.
	 */
	public IModel getNestedModel()
	{
		return null;
	}

	/**
	 * An overriden detach method that makes sure nested models are detached.
	 */
	@Override
	public final void detach()
	{
		IModel model = getNestedModel();
		if (model != null) {
			model.detach();
		}
		super.detach();
	}
}