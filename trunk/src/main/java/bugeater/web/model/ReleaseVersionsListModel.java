package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import wicket.Application;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;
import wicket.model.Model;

import bugeater.domain.ReleaseVersion;
import bugeater.service.ReleaseVersionService;
import bugeater.service.SortOrder;
import bugeater.web.BugeaterApplication;

/**
 * Returns a list of ReleaseVersions for a project.
 * 
 * @author pchapman
 */
public class ReleaseVersionsListModel
	extends AbstractDetachableModel<List<ReleaseVersion>>
{
	private static final long serialVersionUID = 1L;;
	
	/**
	 * A convenience method that will wrap the project string in a model.
	 */
	public ReleaseVersionsListModel(String project, SortOrder order)
	{
		this(new Model<String>(project), false, order);
	}

	/**
	 * Creates a new instance.
	 * @param projectModel A model that will provide the project string.
	 * @param order The order in which the elements should appear.  They are
	 *              sorted by version number.
	 */
	public ReleaseVersionsListModel(
			IModel<String>projectModel, boolean includeNull, SortOrder order
		)
	{
		super();
		this.includeNull = includeNull;
		this.projectModel = projectModel;
		this.sortOrder = order;
	}
	
	private boolean includeNull;
	private IModel<String> projectModel;
	private List<ReleaseVersion>list;
	private SortOrder sortOrder;

	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return projectModel;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			list = new ArrayList<ReleaseVersion>();
			if (includeNull) {
				list.add(null);
			}
			if (projectModel.getObject() != null) {
				ReleaseVersionService service =
					(ReleaseVersionService)
					((BugeaterApplication)Application.get())
					.getSpringContextLocator().getSpringContext()
					.getBean("releaseVersionService");
				list.addAll(service.loadAll(projectModel.getObject(), sortOrder));
			}
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<ReleaseVersion> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<ReleaseVersion> o)
	{
		// not implemented
	}
}
