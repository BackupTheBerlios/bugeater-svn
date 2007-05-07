package bugeater.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
	extends AbstractDetachableEntityListModel<ReleaseVersion>
{
	private static final long serialVersionUID = 1L;;
	
	/**
	 * Returns all versions for all projects.
	 */
	public ReleaseVersionsListModel(SortOrder order)
	{
		this((String)null, order);
	}
	
	/**
	 * A convenience method that will wrap the project string in a model.
	 */
	public ReleaseVersionsListModel(String project, SortOrder order)
	{
		this(new Model(project), false, order);
	}

	/**
	 * Creates a new instance.
	 * @param projectModel A model that will provide the project string.
	 * @param order The order in which the elements should appear.  They are
	 *              sorted by version number.
	 */
	public ReleaseVersionsListModel(
			IModel projectModel, boolean includeNull, SortOrder order
		)
	{
		super();
		this.includeNull = includeNull;
		this.projectModel = projectModel;
		this.sortOrder = order;
	}
	
	private boolean includeNull;
	private IModel projectModel;
	private SortOrder sortOrder;

	/**
	 * @see bugeater.web.model.AbstractDetachableEntityListModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return projectModel;
	}

	@Override
	protected List<ReleaseVersion> load()
	{
		List <ReleaseVersion> list = new ArrayList<ReleaseVersion>();
		if (includeNull) {
			list.add(null);
		}
		if (projectModel.getObject() == null) {
			ReleaseVersionService service =
				(ReleaseVersionService)((BugeaterApplication)Application.get()).getSpringBean("releaseVersionService");
			list.addAll(service.loadAll(sortOrder));
		} else {
			ReleaseVersionService service =
				(ReleaseVersionService)((BugeaterApplication)Application.get()).getSpringBean("releaseVersionService");
			list.addAll(service.loadAll(((String)projectModel.getObject()), sortOrder));
		}
		return list;
	}
}
