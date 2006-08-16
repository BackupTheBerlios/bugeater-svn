package bugeater.web.model;

import bugeater.domain.ReleaseVersion;

import bugeater.service.ReleaseVersionService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;

/**
 * A model used to provide an ReleaseVersion object to the component.
 * 
 * @author pchapman
 */
public class ReleaseVersionModel extends AbstractDetachableModel<ReleaseVersion>
{
	private static final long serialVersionUID = 1L;
	
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 * @param releaseVersionID The unique ID of the releaseVersion.
	 */
	public ReleaseVersionModel(Long releaseVersionID)
	{
		super();
		this.releaseVersion = null;
		this.releaseVersionid = releaseVersionID;
		onAttach();
	}
	
	/**
	 * Creates a new instance.
	 * @param releaseVersion The releaseVersion.
	 */
	public ReleaseVersionModel(ReleaseVersion releaseVersion)
	{
		super();
		this.releaseVersionid = releaseVersion == null ? null : releaseVersion.getId();
		this.releaseVersion = releaseVersion;
	}

	// MEMBERS
	
	private ReleaseVersion releaseVersion;
	private Long releaseVersionid;
	
	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	// METHODS
	
	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (releaseVersion == null && releaseVersionid != null) {
			ReleaseVersionService iService =
				(ReleaseVersionService)((BugeaterApplication)Application.get()).getSpringBean("releaseVersionService");
			releaseVersion = iService.load(releaseVersionid);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		releaseVersion = null;		
	}
	
	

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected ReleaseVersion onGetObject()
	{
		return releaseVersion;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(<T>)
	 */
	@Override
	protected void onSetObject(ReleaseVersion releaseVersion) 
	{
		this.releaseVersion = releaseVersion;
		this.releaseVersionid = releaseVersion == null ? null : releaseVersion.getId();
	}	
}
