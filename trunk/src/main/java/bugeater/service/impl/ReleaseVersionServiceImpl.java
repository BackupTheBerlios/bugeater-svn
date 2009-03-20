package bugeater.service.impl;

import java.sql.Date;
import java.util.List;

import bugeater.bean.ReleaseVersionBean;
import bugeater.dao.ReleaseVersionDao;
import bugeater.domain.ReleaseVersion;
import bugeater.service.ReleaseVersionService;
import bugeater.service.SortOrder;

/**
 * An implmenentation of the ReleaseVersionService interface.
 * 
 * @author pchapman
 */
public class ReleaseVersionServiceImpl implements ReleaseVersionService
{
	/**
	 * Creates a new instance.
	 */
	public ReleaseVersionServiceImpl()
	{
		super();
	}
	
	private ReleaseVersionDao rvDao;
	public void setReleaseVersionDao(ReleaseVersionDao dao)
	{
		this.rvDao = dao;
	}

	/**
	 * @see bugeater.service.ReleaseVersionService#loadAll(bugeater.service.SortOrder)
	 */
	public List<ReleaseVersion> loadAll(SortOrder order)
	{
		return rvDao.loadAll(order);
	}

	/**
	 * @see bugeater.service.ReleaseVersionService#loadAll(java.lang.String, bugeater.service.SortOrder)
	 */
	public List<ReleaseVersion> loadAll(String project, SortOrder order)
	{
		return rvDao.loadAll(project, order);
	}

	/**
	 * @see bugeater.service.ReleaseVersionService#load(java.lang.Long)
	 */
	public ReleaseVersion load(Long id)
	{
		return rvDao.load(id);
	}

	/**
	 * @see bugeater.service.ReleaseVersionService#save(bugeater.domain.ReleaseVersion)
	 */
	public void save(ReleaseVersionBean bean)
	{
		// Either create a new ReleaseVersion, or look up an existing one
		ReleaseVersion release = null;
		if (bean.getId() == null) {
			release = new ReleaseVersion();
			release.setProject(bean.getProject());
		} else {
			release = load(bean.getId());
		}
		// Synchronized the members
		if (bean.getActualReleaseDate() == null) {
			release.setActualReleaseDate(null);
		} else {
			release.setActualReleaseDate(new Date(bean.getActualReleaseDate().getTime()));
		}
		release.setScheduleReleaseDate(new Date(bean.getScheduledReleaseDate().getTime()));
		release.setVersionNumber(bean.getVersionNumber());
		// Save the object.
		rvDao.save(release);
	}
}
