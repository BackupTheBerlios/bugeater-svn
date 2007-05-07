package bugeater.web.model;

import bugeater.domain.ReleaseVersion;

import bugeater.service.ReleaseVersionService;
import bugeater.web.BugeaterApplication;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;

/**
 * A model used to provide a ReleaseVersion object to the component.
 * 
 * @author pchapman
 */
public class ReleaseVersionModel implements IModel
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
	private transient ReleaseVersion releaseVersion;

	// METHODS

	/**
	 * @see bugeater.web.model.MutableDetachableModel#detach()
	 */
	public void detach() {
		releaseVersion = null;
	}

	/**
	 * @see org.apache.wicket.model.IModel#getObject()
	 */
	public Object getObject()
	{
		if (releaseVersion == null && releaseVersionid != null) {
			ReleaseVersionService iService =
				(ReleaseVersionService)((BugeaterApplication)Application.get()).getSpringBean("releaseVersionService");
			releaseVersion = iService.load(releaseVersionid);
		}
		return releaseVersion;
	}

	/**
	 * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Object object)
	{
		if (object == null) {
			releaseVersionid = null;
			releaseVersion = null;
		} else if (object instanceof ReleaseVersion) {
			releaseVersion = (ReleaseVersion)object;
			releaseVersionid = releaseVersion.getId();
		}
	}
}
