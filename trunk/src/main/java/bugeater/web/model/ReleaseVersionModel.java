package bugeater.web.model;

import bugeater.domain.ReleaseVersion;

import bugeater.service.ReleaseVersionService;
import bugeater.web.BugeaterApplication;

import wicket.Application;

/**
 * A model used to provide an ReleaseVersion object to the component.
 * 
 * @author pchapman
 */
public class ReleaseVersionModel extends MutableDetachableModel<ReleaseVersion>
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
		this.releaseVersionid = releaseVersionID;
	}
	
	/**
	 * Creates a new instance.
	 * @param releaseVersion The releaseVersion.
	 */
	public ReleaseVersionModel(ReleaseVersion releaseVersion)
	{
		super(releaseVersion);
		this.releaseVersionid = releaseVersion == null ? null : releaseVersion.getId();
	}

	// MEMBERS
	
	private Long releaseVersionid;

	// METHODS

	/**
	 * @see bugeater.web.model.MutableDetachableModel#detach()
	 */
	@Override
	public void detach() {
		ReleaseVersion releaseVersion = getObject();
		this.releaseVersionid = releaseVersion == null ? null : releaseVersion.getId();
		super.detach();
	}

	/* (non-Javadoc)
	 * @see bugeater.web.model.MutableDetachableModel#load()
	 */
	@Override
	protected ReleaseVersion load() {
		if (releaseVersionid == null) {
			return null;
		} else {
			ReleaseVersionService iService =
				(ReleaseVersionService)((BugeaterApplication)Application.get()).getSpringBean("releaseVersionService");
			return iService.load(releaseVersionid);
		}
	}	
}
