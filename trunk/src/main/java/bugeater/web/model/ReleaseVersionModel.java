package bugeater.web.model;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;

import bugeater.domain.ReleaseVersion;
import bugeater.service.ReleaseVersionService;
import bugeater.web.BugeaterApplication;

/**
 * A model used to provide an ReleaseVersion object to the component.
 * 
 * @author pchapman
 */
public class ReleaseVersionModel implements IModel<ReleaseVersion>
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
		super();
		setObject(releaseVersion);
	}

	// MEMBERS
	
	private Long releaseVersionid;
	private transient ReleaseVersion version;

	// METHODS

	/**
	 * @see bugeater.web.model.MutableDetachableModel#detach()
	 */
	public void detach() {
		version = null;
	}
	
	public ReleaseVersion getObject()
	{
		if (version == null && releaseVersionid != null) {
			version = load();
		}
		return version;
	}
	
	public void setObject(ReleaseVersion version)
	{
		this.version = version;
		this.releaseVersionid = version == null ? null : version.getId();
	}

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
